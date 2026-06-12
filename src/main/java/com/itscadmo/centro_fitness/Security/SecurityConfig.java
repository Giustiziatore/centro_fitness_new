package com.itscadmo.centro_fitness.Security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity 
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService customUserDetailsService; 

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    // --- 1. CONFIGURAZIONE DEL FIREWALL (SecurityFilterChain) ---
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                /* Disabilita il CSRF */
                .csrf(AbstractHttpConfigurer::disable)

                /* Configurazione CORS di base */
                .cors(c -> c.configurationSource(corsConfigurationSource()))

                /* Regole di Autorizzazione */
                .authorizeHttpRequests(auth -> auth
                        
                        // INFRASTRUTTURA BASE
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll() 
                        .requestMatchers("/api/auth/**").permitAll() 
                        
                        // ENDPOINTS DI SOLA LETTURA (Pubblici)
                        .requestMatchers(HttpMethod.GET, "/api/corsi/**").permitAll() 
                        .requestMatchers(HttpMethod.GET, "/api/lezioni/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/sale/**").permitAll()
                        
                        // PROTEZIONE BASE: Tutto il resto richiede autenticazione
                        .requestMatchers("/api/clienti/**").authenticated() 
                        .requestMatchers("/api/abbonamenti/**").authenticated()
                        .requestMatchers("/api/iscrizioni/**").authenticated()

                        // Ruoli specifici (operazioni sensibili)
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/istruttori/**").hasAnyRole("ADMIN", "ISTRUTTORE")

                        // Tutto il resto è protetto per default
                        .anyRequest().authenticated()
                )
                /* Gestione Sessione: Stateless (per JWT) */
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                /* Configura il provider di autenticazione */
                .authenticationProvider(authenticationProvider())

                /* Aggiunge il filtro JWT */
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // --- 2. DEFINIZIONI DEI BEAN DI SICUREZZA ---

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); 
    }

    @Bean
    @SuppressWarnings("deprecation")
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService); 
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // --- 3. CONFIGURAZIONE CORS DI BASE ---
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .toList());
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
