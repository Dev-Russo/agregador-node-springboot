package br.ufv.sin142.aggregadornode.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
// NOVAS IMPORTAÇÕES
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
// FIM DAS NOVAS IMPORTAÇÕES
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/api/aggregator/results").permitAll()
                .requestMatchers("/ws/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/actuator/**").hasRole("ADMIN") // Esta regra precisa de um usuário com a role ADMIN
                .anyRequest().authenticated()
            )
            .httpBasic(withDefaults());

        return http.build();
    }

    //
    // NOVO BLOCO PARA DEFINIR USUÁRIOS E SENHAS
    //
    @Bean
    public UserDetailsService userDetailsService() {
        // Cria um usuário chamado "admin" com a senha "password" e a permissão "ADMIN"
        UserDetails admin = User.withUsername("admin")
            .password("{noop}password") // {noop} indica que a senha não está criptografada (apenas para teste)
            .roles("ADMIN")
            .build();

        // Você pode criar outros usuários se precisar
        UserDetails user = User.withUsername("user")
            .password("{noop}12345")
            .roles("USER")
            .build();

        // Retorna um gerenciador de usuários em memória com os usuários criados
        return new InMemoryUserDetailsManager(admin, user);
    }
}