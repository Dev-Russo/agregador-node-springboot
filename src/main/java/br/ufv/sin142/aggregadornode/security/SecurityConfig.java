package br.ufv.sin142.aggregadornode.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Injeta o valor da chave de API do application.properties
    @Value("${nodes.api.key}")
    private String apiKey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Cria nosso filtro de chave de API
        ApiKeyAuthFilter apiKeyFilter = new ApiKeyAuthFilter(apiKey);

        http
            // Adiciona nosso filtro customizado ANTES do filtro padrão de autenticação do Spring
            .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class)

            // Desabilita CSRF, pois nossa API é stateless e não usa sessões com cookies
            .csrf(csrf -> csrf.disable())

            // Configura a política de criação de sessão para STATELESS (sem sessões)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Define as regras de autorização para cada endpoint
            .authorizeHttpRequests(authz -> authz
                // Requer que requisições para /actuator/** tenham a role 'ADMIN'
                .requestMatchers("/actuator/**").hasRole("ADMIN")

                // Permite que requisições GET para /api/aggregator/results sejam públicas
                .requestMatchers(HttpMethod.GET, "/api/aggregator/results").permitAll()

                // Para POST em /api/aggregator/data, nosso filtro já vai cuidar da autenticação.
                // Se o filtro passar, permitimos o acesso.
                // Usamos anyRequest() aqui, mas o filtro já protegeu a rota.
                .requestMatchers(HttpMethod.POST, "/api/aggregator/data").permitAll()

                // Nega qualquer outra requisição que não foi explicitamente permitida acima
                .anyRequest().denyAll()
            )

            // Habilita a Autenticação Básica HTTP (usada para os endpoints do Actuator)
            .httpBasic(withDefaults());

        return http.build();
    }
}