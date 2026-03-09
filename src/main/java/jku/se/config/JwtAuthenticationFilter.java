package jku.se.config;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest  request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain         filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        // 1. Header prüfen: muss "Bearer " inklusive Leerzeichen sein
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        // 2. Token validieren (fängt intern alle JwtException ab)
        if (!jwtService.isTokenValid(token)) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Ungültiges oder abgelaufenes JWT");
            return;
        }

        // 3. Jetzt sicher den Username extrahieren
        String username;
        try {
            username = jwtService.extractUsername(token);
        } catch (JwtException e) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "JWT konnte nicht geparst werden");
            return;
        }

        // 4. Wenn noch keine Authentifizierung gesetzt und Username bekannt:
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 5. (Optional) Nochmals gegen UserDetails prüfen
            if (jwtService.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 6. Filterkette auf jeden Fall fortsetzen
        filterChain.doFilter(request, response);
    }
}
