package br.ufv.sin142.aggregadornode.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull; // <<< ADICIONE ESTE IMPORT
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final String headerName = "X-API-KEY";
    private final String expectedApiKey;

    public ApiKeyAuthFilter(String expectedApiKey) {
        this.expectedApiKey = expectedApiKey;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                  @NonNull HttpServletResponse response,
                                  @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        
        // Verificamos se a requisição é para o endpoint que queremos proteger com a chave
        if (request.getRequestURI().equals("/api/aggregator/data") && request.getMethod().equals("POST")) {
            String apiKey = request.getHeader(headerName);

            if (expectedApiKey.equals(apiKey)) {
                // Chave válida, continua o processamento
                filterChain.doFilter(request, response);
            } else {
                // Chave inválida ou ausente, rejeita a requisição
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("API Key inválida ou ausente");
            }
        } else {
            // Se não for a rota que queremos proteger, apenas continua o processamento normal
            filterChain.doFilter(request, response);
        }
    }
}