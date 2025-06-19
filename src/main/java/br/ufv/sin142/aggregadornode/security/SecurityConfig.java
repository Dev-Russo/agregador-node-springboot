package br.ufv.sin142.aggregadornode.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // BLOCO CORRIGIDO ABAIXO
            .authorizeHttpRequests(authz -> authz
                // 1. Libera o healthcheck, os resultados e o websocket para todos
                .requestMatchers(HttpMethod.GET, "/api/aggregator/results").permitAll()
                .requestMatchers("/ws/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                // 2. Protege o resto do actuator para admins
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                // 3. Qualquer outra requisição é negada
                .anyRequest().authenticated() // Usar authenticated() aqui é um pouco mais flexível que denyAll()
            )
            .httpBasic(withDefaults());

        return http.build();
    }
}