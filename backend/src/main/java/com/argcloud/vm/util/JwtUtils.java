package com.argcloud.vm.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Clase de utilidad para operaciones relacionadas con JSON Web Tokens (JWT).
 * Proporciona métodos para generar, validar y extraer información de los tokens.
 */
@Component
public class JwtUtils {

    @Value("${argcloud.jwt.secret}")
    private String jwtSecret;

    @Value("${argcloud.jwt.expiration}")
    private int jwtExpirationMs;

    /**
     * Genera un token JWT para un email.
     * @param email el email para el cual se genera el token.
     * @return el token JWT como una cadena de texto.
     */
    public String generateJwtToken(String email) {
        return generateTokenFromEmail(email);
    }

    /**
     * Genera un token a partir de un email.
     * @param email el email a incluir en el token.
     * @return el token JWT generado.
     */
    public String generateTokenFromEmail(String email) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Obtiene el email a partir de un token JWT.
     * @param token el token JWT.
     * @return el email contenido en el token.
     */
    public String getEmailFromJwtToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Valida un token JWT.
     * @param authToken el token a validar.
     * @return true si el token es válido, false en caso contrario.
     */
    public boolean validateJwtToken(String authToken) {
        System.out.println("🔐 JwtUtils - Validando token JWT...");
        System.out.println("   Token (primeros 30 chars): " + authToken.substring(0, Math.min(authToken.length(), 30)) + "...");
        System.out.println("   Secret configurado: " + (jwtSecret != null && !jwtSecret.isEmpty() ? "✅ Sí" : "❌ No"));
        
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            System.out.println("   SecretKey creada: ✅");
            
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(authToken)
                .getBody();
                
            System.out.println("   Claims extraídas: ✅");
            System.out.println("   Subject (email): " + claims.getSubject());
            System.out.println("   Issued at: " + claims.getIssuedAt());
            System.out.println("   Expires at: " + claims.getExpiration());
            System.out.println("   Token válido: ✅");
            
            return true;
        } catch (MalformedJwtException e) {
            System.err.println("❌ Token JWT inválido (malformado): " + e.getMessage());
            e.printStackTrace();
        } catch (ExpiredJwtException e) {
            System.err.println("❌ Token JWT expirado: " + e.getMessage());
            System.err.println("   Fecha de expiración: " + e.getClaims().getExpiration());
            System.err.println("   Fecha actual: " + new Date());
        } catch (UnsupportedJwtException e) {
            System.err.println("❌ Token JWT no soportado: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("❌ JWT claims string está vacío: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Error inesperado validando JWT: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
} 