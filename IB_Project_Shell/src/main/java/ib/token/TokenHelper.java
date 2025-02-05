package ib.token;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import ib.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class TokenHelper {
	// Izdavac tokena
		@Value("IB_Project_Shell")
		private String APP_NAME;

		// Tajna koju samo backend aplikacija treba da zna kako bi mogla da generise i proveri JWT https://jwt.io/
		@Value("somesecret")
		public String SECRET;

		// Period vazenja
		@Value("300000")
		private int EXPIRES_IN;

		// Naziv headera kroz koji ce se prosledjivati JWT u komunikaciji server-klijent
		@Value("Authorization")
		private String AUTH_HEADER;

		// Moguce je generisati JWT za razlicite klijente (npr. web i mobilni klijenti nece imati isto trajanje JWT, JWT za mobilne klijente ce trajati duze jer se mozda aplikacija redje koristi na taj nacin)
		private static final String AUDIENCE_UNKNOWN = "unknown";
		private static final String AUDIENCE_WEB = "web";
		private static final String AUDIENCE_MOBILE = "mobile";
		private static final String AUDIENCE_TABLET = "tablet";

		// Algoritam za potpisivanje JWT
		private SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

		// Funkcija za generisanje JWT token
		public String generateToken(String username, String roles) {
			return Jwts.builder()
					.setIssuer(APP_NAME)
					.setSubject(username)
					.setAudience(generateAudience())
					.setIssuedAt(new Date())
					.setExpiration(generateExpirationDate())
					.claim("roles", roles)
					// .claim("key", value) //moguce je postavljanje proizvoljnih podataka u telo JWT tokena
					.signWith(SIGNATURE_ALGORITHM, SECRET).compact();
		}

		private String generateAudience() {

			return AUDIENCE_WEB;
		}

		private Date generateExpirationDate() {
			return new Date(new Date().getTime() + EXPIRES_IN);
		}

		// Funkcija za refresh JWT tokena
		public String refreshToken(String token) {
			String refreshedToken;
			try {
				final Claims claims = this.getAllClaimsFromToken(token);
				claims.setIssuedAt(new Date());
				refreshedToken = Jwts.builder()
						.setClaims(claims)
						.setExpiration(generateExpirationDate())
						.signWith(SIGNATURE_ALGORITHM, SECRET).compact();
			} catch (Exception e) {
				refreshedToken = null;
			}
			return refreshedToken;
		}

		public boolean canTokenBeRefreshed(String token, Date lastPasswordReset) {
			final Date created = this.getIssuedAtDateFromToken(token);
			return (!(this.isCreatedBeforeLastPasswordReset(created, lastPasswordReset))
					&& (!(this.isTokenExpired(token)) || this.ignoreTokenExpiration(token)));
		}

		// Funkcija za validaciju JWT tokena
		public Boolean validateToken(String token, UserDetails userDetails) {
			User user = (User) userDetails;
			final String username = getUsernameFromToken(token);
			final Date created = getIssuedAtDateFromToken(token);
			
			return (username != null && username.equals(userDetails.getUsername())
					&& !isCreatedBeforeLastPasswordReset(created, user.getLastPasswordResetDate()));
		}

		public String getUsernameFromToken(String token) {
			String username;
			try {
				final Claims claims = this.getAllClaimsFromToken(token);
				username = claims.getSubject();
			} catch (Exception e) {
				username = null;
			}
			return username;
		}

		public Date getIssuedAtDateFromToken(String token) {
			Date issueAt;
			try {
				final Claims claims = this.getAllClaimsFromToken(token);
				issueAt = claims.getIssuedAt();
			} catch (Exception e) {
				issueAt = null;
			}
			return issueAt;
		}

		public String getAudienceFromToken(String token) {
			String audience;
			try {
				final Claims claims = this.getAllClaimsFromToken(token);
				audience = claims.getAudience();
			} catch (Exception e) {
				audience = null;
			}
			return audience;
		}

		public Date getExpirationDateFromToken(String token) {
			Date expiration;
			try {
				final Claims claims = this.getAllClaimsFromToken(token);
				expiration = claims.getExpiration();
			} catch (Exception e) {
				expiration = null;
			}
			return expiration;
		}

		public int getExpiredIn() {
			return EXPIRES_IN;
		}

		// Funkcija za preuzimanje JWT tokena iz zahteva
		public String getToken(HttpServletRequest request) {
			String authHeader = getAuthHeaderFromHeader(request);

				if (authHeader != null && authHeader.startsWith("Bearer ")) {
				return authHeader.substring(7);
			}

			return null;
		}

		public String getAuthHeaderFromHeader(HttpServletRequest request) {
			return request.getHeader(AUTH_HEADER);
		}
		
		private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
			return (lastPasswordReset != null && created.before(lastPasswordReset));
		}

		private Boolean isTokenExpired(String token) {
			final Date expiration = this.getExpirationDateFromToken(token);
			return expiration.before(new Date());
		}

		private Boolean ignoreTokenExpiration(String token) {
			String audience = this.getAudienceFromToken(token);
			return (audience.equals(AUDIENCE_TABLET) || audience.equals(AUDIENCE_MOBILE));
		}

		// Funkcija za citanje svih podataka iz JWT tokena
		private Claims getAllClaimsFromToken(String token) {
			Claims claims;
			try {
				claims = Jwts.parser()
						.setSigningKey(SECRET)
						.parseClaimsJws(token)
						.getBody();
			} catch (Exception e) {
				claims = null;
			}
			return claims;
		}

}
