package edu.uniquindio.dentalmanagementsystembackend.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Component
public class JWTUtils {
    // Método que genera un token JWT basado en el email del usuario y un mapa de claims (reclamaciones).
// Las claims pueden ser datos adicionales sobre el usuario o el contexto de autenticación.
    public String generateToken(String email, Map<String, Object> claims) {

        // Se obtiene el tiempo actual utilizando la clase Instant de java.time.
        Instant now = Instant.now();

        // Construimos y devolvemos el token JWT. Utiliza el email como sujeto, incluye las claims
        // y define la fecha de emisión y la fecha de expiración (1 hora después de la emisión).
        return Jwts.builder()
                .claims(claims)  // Agregar las claims adicionales al token
                .subject(email)  // Establecer el email como el sujeto del token
                .issuedAt(Date.from(now))  // Establecer la fecha y hora de emisión del token
                .expiration(Date.from(now.plus(1L, ChronoUnit.HOURS)))  // Expira en 1 hora
                .signWith(getKey())  // Firmar el token utilizando la clave secreta generada
                .compact();  // Genera el token como una cadena compacta (JWT en formato serializado)
    }

    // Método para analizar (parsear) un token JWT y obtener las claims dentro del token.
// Lanza excepciones si el token está expirado, es inválido o tiene problemas de formato.
    public Jws<Claims> parseJwt(String jwtString) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, IllegalArgumentException {
        // Se crea un JwtParser para validar el token con la clave secreta.
        JwtParser jwtParser = Jwts.parser().verifyWith(getKey()).build();

        // Se analiza el token JWT y devuelve las claims firmadas.
        return jwtParser.parseSignedClaims(jwtString);
    }

    // Método privado que genera y devuelve una clave secreta (SecretKey) para firmar los tokens.
// Usa el algoritmo HMAC-SHA basado en una clave de longitud suficiente para garantizar la seguridad.
    private SecretKey getKey() {
        // La clave secreta que se utiliza para firmar y verificar el token.
        // Debe tener al menos 256 bits (32 bytes) para ser válida con HMAC-SHA256.
        String claveSecreta = "secretsecretsecretsecretsecretsecretsecretsecret";

        // Convierte la cadena de texto en un array de bytes, que es requerido por el método de generación de claves.
        byte[] secretKeyBytes = claveSecreta.getBytes();

        // Se utiliza la clase Keys de la biblioteca JJWT para crear una clave secreta HMAC-SHA.
        return Keys.hmacShaKeyFor(secretKeyBytes);
    }

}
