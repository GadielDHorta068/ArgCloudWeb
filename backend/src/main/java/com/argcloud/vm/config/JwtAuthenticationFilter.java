package com.argcloud.vm.config;

import com.argcloud.vm.service.UserService;
import com.argcloud.vm.util.JwtUtils;
import com.argcloud.vm.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Filtro de autenticación que intercepta las peticiones para validar el token JWT.
 * Este filtro se ejecuta una vez por cada petición.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserService userService;

    /**
     * Constructor del filtro de autenticación JWT.
     * @param jwtUtils      Utilidad para manejar los tokens JWT.
     * @param userService   Servicio para manejar los usuarios.
     */
    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserService userService) {
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }

    /**
     * Lógica del filtro para procesar una petición, validar el token JWT y establecer la autenticación en el contexto de seguridad.
     *
     * @param request       La petición HTTP.
     * @param response      La respuesta HTTP.
     * @param filterChain   La cadena de filtros.
     * @throws ServletException si ocurre un error en el servlet.
     * @throws IOException      si ocurre un error de entrada/salida.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                email = jwtUtils.getEmailFromJwtToken(token);
            } catch (Exception e) {
                // Consider logging this exception with a proper logger
            }
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtils.validateJwtToken(token)) {
                Optional<User> userOptional = userService.findByEmail(email);
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    
                    // Crear una lista de authorities
                    List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

                    // Crear autenticación con authorities
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(
                            user.getEmail(), // Principal
                            null, // Credentials
                            authorities // Authorities
                        );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        
        filterChain.doFilter(request, response);
    }
} 