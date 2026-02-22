package com.joyeria.joyeria_api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAuthenticationFilter class
 *
 * @Version: 1.0.0 - 21 feb. 2026
 * @Author: Matias Belmar - mati.belmar0625@gmail.com
 * @Since: 1.0.0 2026/02/21
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // obtiene el header del Authorization
        final String authHeader = request.getHeader("Authorization");

        // si no hay un header o no empieza con un "Bearer " va a continuear
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        //extraer el token (quitardo "Bearer ")
        final String jwt = authHeader.substring(7);
        final String userEmail = jwtUtils.extractUsername(jwt);

        //si hay email y no esta autenticado aun
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            //cargar usuario de la BD
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            //validar token
            if (jwtUtils.validateToken(jwt, userDetails)) {

                //crear autenticacion
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                //establecer autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        //continuar con el siguiente filtro
        filterChain.doFilter(request, response);
    }
}
