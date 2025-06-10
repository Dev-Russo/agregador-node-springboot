package br.ufv.sin142.aggregadornode.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Desabilita CSRF, pois não usaremos sessões baseadas em formulários/cookies
            .csrf(csrf -> csrf.disable())

            // Define a política de sessão como STATELESS, ideal para APIs
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Define as regras de autorização para cada endpoint
            .authorizeHttpRequests(authz -> authz
                // Permite acesso público ao healthcheck para o Docker
                .requestMatchers("/actuator/health").permitAll()
                
                // Protege todos os outros endpoints do Actuator com a role ADMIN
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                
                // Permite que qualquer um consulte os resultados agregados
                .requestMatchers(HttpMethod.GET, "/api/aggregator/results").permitAll()

                // Nega qualquer outra requisição que não foi explicitamente permitida
                .anyRequest().denyAll()
            )

            // Habilita a Autenticação Básica HTTP para os endpoints protegidos
            .httpBasic(withDefaults());

        return http.build();
    }
}