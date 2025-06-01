package org.jono.medicalmodelsservice.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.UUID;

@Configuration
public class SecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain asFilterChain(HttpSecurity http)
            throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http.cors((cors) -> cors
                .configurationSource(myWebsiteConfigurationSource()));

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults());

        http
                .exceptionHandling((exceptions) ->
                        exceptions.defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"), new MediaTypeRequestMatcher(MediaType.TEXT_HTML))
                )
                .oauth2ResourceServer((resourceServer) -> resourceServer
                        .jwt(Customizer.withDefaults()));



        return http.build();
    }

    UrlBasedCorsConfigurationSource myWebsiteConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT","DELETE","OPTIONS"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .formLogin(Customizer.withDefaults())
                .csrf(c -> c.disable());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails userDetails = User.withUsername("bill")
                .password("password")
                .roles("USER")
                .build();
        var user = User.withUsername("john")
                .password("12345")
                .roles("read")
                .build();
        var rtrenneman = User.withUsername("rtrenneman")
                .password("YjzJdH6!G??tntQ#")
                .roles("SUPPORT")
                .build();
        var mmoss = User.withUsername("mmoss")
                .password("y?jaHKGTaji6xAd9")
                .roles("SUPPORT")
                .build();
        var jbarber = User.withUsername("jbarber")
                .password("GM!mQn!8K8Db9p#p")
                .roles("SUPPORT")
                .build();
        var lcuddy = User.withUsername("lcuddy")
                .password("YjzJdH6!G??tntQ#")
                .roles("ADMIN")
                .build();
        var ghouse = User.withUsername("ghouse")
                .password("S!p5fs!MFx&&GTPs")
                .roles("USER")
                .build();
        var jwilson = User.withUsername("jwilson")
                .password("s9dQd$grL!!Y5?$h")
                .roles("USER")
                .build();
        var spotter = User.withUsername("spotter")
                .password("N78S9x9ft$HFGMrf")
                .roles("ADMIN")
                .build();
        var bpierce = User.withUsername("bpierce")
                .password("C$At$BBGL5yLP&AM")
                .roles("USER")
                .build();
        var woreilly = User.withUsername("woreilly")
                .password("so#KKNYiqe!F5!Ph")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(userDetails, user, rtrenneman, mmoss, jbarber, lcuddy, ghouse, jwilson, spotter, bpierce, woreilly);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        var registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("client")
                .clientSecret("secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost:8080/login/oauth2/code/my_authorization_server")
                .scope(OidcScopes.OPENID)
                .build();

        var nextAuthClient = RegisteredClient.withId(UUID.randomUUID().toString()) // Use your client ID
                .clientId("next-auth-client")
                .clientSecret("next-auth-client-secret") // Use your actual secret
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUris(uris -> uris.add("http://localhost:3000/auth/callback/my_authorization_server"))
                .redirectUris(uris -> uris.add("http://localhost:3001/auth/callback/my_authorization_server"))
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .clientSettings(ClientSettings.builder()
                        .requireProofKey(true)
                        .build())
                .build();


        return new InMemoryRegisteredClientRepository(registeredClient, nextAuthClient);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return context -> {
            if (context.getPrincipal() != null) {
                // Extract authorities from the authenticated principal
                context.getPrincipal().getAuthorities();


                var authorities = context.getPrincipal().getAuthorities().stream()
                        .map(grantedAuthority -> grantedAuthority.getAuthority())
                        .toList();

                // Add a custom claim to include roles/authorities
                context.getClaims().claim("roles", authorities);
            }
        };
    }
}
