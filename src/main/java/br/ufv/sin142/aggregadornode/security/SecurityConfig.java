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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Desabilitar CSRF (padrão para APIs stateless)
            .csrf(csrf -> csrf.disable())
            
            // 2. Configurar a política de sessão para ser stateless
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 3. Definir as regras de autorização para as requisições HTTP
            .authorizeHttpRequests(auth -> auth
                // Regras de liberação explícita (permitAll)
                .requestMatchers(HttpMethod.GET, "/api/aggregator/results").permitAll()
                .requestMatchers("/ws/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                
                // Regra de proteção para Admin
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                
                // Qualquer outra requisição não listada acima deve ser autenticada
                .anyRequest().authenticated()
            )
            
            // 4. Habilitar o popup de autenticação básica HTTP
            .httpBasic(withDefaults());

        return http.build();
    }
}