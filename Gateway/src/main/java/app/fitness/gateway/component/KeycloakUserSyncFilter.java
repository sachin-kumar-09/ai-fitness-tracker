package app.fitness.gateway.component;

import app.fitness.gateway.user.dto.RegisterRequest;
import app.fitness.gateway.user.service.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.text.ParseException;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeycloakUserSyncFilter implements WebFilter {

    private final UserService userService;

    // Mono in reactive programming is like promise.
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("Entering KeycloakUserSyncFilter :: filter at {}", System.currentTimeMillis());
        String keycloakId = exchange
                .getRequest()
                .getHeaders()
                .getFirst("X-User-Id");

        String token = exchange
                .getRequest()
                .getHeaders()
                .getFirst("Authorization");

        RegisterRequest registerRequest = getUserDetails(token);

        if(keycloakId == null) {
            keycloakId = registerRequest.getKeycloakId();
        }

        if(keycloakId != null && token != null) {
            String finalKeycloakId = keycloakId;
            return userService.validateUser(keycloakId)
                    .flatMap(exist -> {
                        if(!exist) {
                            if(registerRequest != null) {
                                return userService.registerUser(registerRequest)
                                        .then(Mono.empty());
                            } else {
                                return Mono.empty();
                            }
                        } else {
                            log.info("KeycloakUserSyncFilter :: filter :: user already exists, skipping user sync");
                            return Mono.empty();
                        }
                    })
                    // defer means running the abpve code fully then its called.
                    .then(Mono.defer(() -> {
                        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                                .header("X-User-Id", finalKeycloakId)
                                .build();
                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
            }));
        }

        return chain.filter(exchange);
    }

    private RegisterRequest getUserDetails(String token) {
        log.info("Entering KeycloakUserSyncFilter :: getUserDetails at {}", System.currentTimeMillis());
        try{
            String tokenWithoutBearer = token.replace("Bearer ", "").trim();

            SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();

            RegisterRequest request = new RegisterRequest();
            request.setEmail(jwtClaimsSet.getStringClaim("email"));
            request.setFirstName(jwtClaimsSet.getStringClaim("given_name"));
            request.setLastName(jwtClaimsSet.getStringClaim("family_name"));
            request.setPassword("password");
            request.setKeycloakId(jwtClaimsSet.getStringClaim("sub"));

            return request;

        } catch (ParseException e) {
            throw new RuntimeException(e);
        } finally {
            log.info("Exiting KeycloakUserSyncFilter :: getUserDetails at {}", System.currentTimeMillis());
        }
    }
}
