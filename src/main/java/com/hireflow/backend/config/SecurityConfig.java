package com.hireflow.backend.config;

import com.hireflow.backend.security.JwtAuthConverter;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.net.URI;
import java.util.List;
import java.util.Set;

/** Supabase JWT doğrulama, CORS ve endpoint yetkilerini buradan tanımlar. */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Value("${supabase.jwt.jwk-set-uri}")
    private String jwkSetUri; // JWKS adresi (public key buradan çekilir)
    
    private final JwtAuthConverter jwtAuthConverter;

    public SecurityConfig(JwtAuthConverter jwtAuthConverter) {
        this.jwtAuthConverter = jwtAuthConverter;
    }

    /** HTTP güvenlik zinciri: hangi URL açık, hangisi JWT ister. */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // REST + JWT; CSRF token yok
            .cors(Customizer.withDefaults())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // session tutulmaz
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/v1/test/public").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/parameters/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/academy/forms").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/academy/forms/*/questions").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/academy/forms/{formId}/questions").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/academy/questions/types").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/academy/universities").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/academy/departments").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/academy/forms/*/apply").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/academy/forms/{formId}/apply").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .decoder(jwtDecoder()) // imza doğrulama
                    .jwtAuthenticationConverter(jwtAuthConverter) // DB rollerini ROLE_* yapar
                )
            );

        return http.build();
    }

    /** Supabase ES256 JWT'lerini JWKS ile doğrular. */
    @Bean
    public JwtDecoder jwtDecoder() {
        try {
            // Supabase JWKS endpoint'inden public key'leri al
            JWKSource<SecurityContext> jwkSource = new RemoteJWKSet<>(URI.create(jwkSetUri).toURL());
            
            // ES256 algoritmasını açıkça selector'e bildir (varsayılan RSA değil!)
            JWSKeySelector<SecurityContext> jwsKeySelector =
                    new JWSVerificationKeySelector<>(Set.of(JWSAlgorithm.ES256), jwkSource);

            // JWT processor'ı ES256 key selector ile yapılandır
            ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
            jwtProcessor.setJWSKeySelector(jwsKeySelector);

            return new NimbusJwtDecoder(jwtProcessor);
        } catch (Exception e) {
            throw new IllegalStateException("JWT Decoder başlatılamadı", e);
        }
    }

    /** Local ve Vercel frontend origin'lerine CORS izni verir. */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(
                "https://*.vercel.app",
                "https://hireflow-frontend-omega.vercel.app",
                "http://localhost:5173",
                "http://localhost:3000"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin"
        ));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}