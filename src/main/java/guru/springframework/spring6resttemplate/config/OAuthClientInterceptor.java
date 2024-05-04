package guru.springframework.spring6resttemplate.config;

import static java.util.Objects.isNull;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;

@Component
public class OAuthClientInterceptor implements ClientHttpRequestInterceptor {

    public final OAuth2AuthorizedClientManager manager;
    public final Authentication principal;
    public final ClientRegistration clientRegistration;

    public OAuthClientInterceptor(OAuth2AuthorizedClientManager manager,
            ClientRegistrationRepository clientRegistrationRepository) {
        this.manager = manager;
        this.principal = createPrincipal();
        this.clientRegistration = clientRegistrationRepository.findByRegistrationId("springauth");
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        OAuth2AuthorizeRequest auth2AuthorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId(clientRegistration.getRegistrationId())
                .principal(createPrincipal())
                .build();

        OAuth2AuthorizedClient client = manager.authorize(auth2AuthorizeRequest);

        if (isNull(client)) {
            throw new IllegalStateException("Missing credentials");
        }

        request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken().getTokenValue());

        return execution.execute(request, body);
    }

    private Authentication createPrincipal() {
        return new Authentication() {

            @Override
            public String getName() {
                return clientRegistration.getClientId();
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return Collections.emptySet();
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return this;
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }

        };
    }

}