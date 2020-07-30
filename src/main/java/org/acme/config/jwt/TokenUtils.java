package org.acme.config.jwt;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;

import java.io.*;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utilities for generating a JWT for testing
 */
public class TokenUtils {

    private TokenUtils() {
        // no-op: utility class
    }

    public static String generateTokenString(
            String userRolleName,
            String userLogin,
            String userVorname,
            String userNachname) throws Exception {
        List<String> groups = Arrays.asList(userRolleName);

        // Generate an RSA key pair, which will be used for signing and verification of the JWT, wrapped in a JWK
        RsaJsonWebKey rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048); //2048

        // Give the JWK a Key ID (kid), which is just the polite thing to do
        rsaJsonWebKey.setKeyId("k1");

        // Create the Claims, which will be the content of the JWT
        JwtClaims claims = new JwtClaims();
        claims.setSubject("subject"); // the subject/principal is whom the token is about
        claims.setGeneratedJwtId(1); // a unique identifier for the token
        claims.setExpirationTimeMinutesInTheFuture(720); // time when the token will expire (10 minutes from now)
        claims.setIssuedAtToNow();  // when the token was issued/created (now)
        claims.setNotBeforeMinutesInThePast(2); // time before which the token is not yet valid (2 minutes ago)

        claims.setClaim("userLogin", userLogin); // additional claims/attributes about the subject can be added
        claims.setClaim("name", userVorname + " " + userNachname);
        claims.setStringListClaim("groups", groups); // multi-valued claims work too and will end up as a JSON array

        claims.setIssuer("Issuer");  // who creates the token and signs it
//    claims.setAudience("Audience"); // to whom the token is intended to be sent


        // A JWT is a JWS and/or a JWE with JSON claims as the payload.
        // In this example it is a JWS so we create a JsonWebSignature object.
        JsonWebSignature jws = new JsonWebSignature();

        // The payload of the JWS is JSON content of the JWT Claims
        jws.setPayload(claims.toJson());

        PrivateKey pk = readPrivateKey("/META-INF/resources/privateKey.pem");
//        PrivateKey pk = readPrivateKey(sharedPath + "/resources/privateKey.pem");

        // The JWT is signed using the private key
        jws.setKey(pk);

        // Set the Key ID (kid) header because it's just the polite thing to do.
        // We only have one key in this example but a using a Key ID helps
        // facilitate a smooth key rollover process
        jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());

        // Set the signature algorithm on the JWT/JWS that will integrity protect the claims
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

        // Sign the JWS and produce the compact serialization or the complete JWT/JWS
        // representation, which is a string consisting of three dot ('.') separated
        // base64url-encoded parts in the form Header.Payload.Signature
        // If you wanted to encrypt it, you can simply set this jwt as the payload
        // of a JsonWebEncryption object and set the cty (Content Type) header to "jwt".
        String jwt = jws.getCompactSerialization();

        return jwt;

    }

    public static String generateChatTokenString(
            String userNickname) throws Exception {
//        List<String> groups = Arrays.asListuserRolleName);

        // Generate an RSA key pair, which will be used for signing and verification of the JWT, wrapped in a JWK
        RsaJsonWebKey rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);

        // Give the JWK a Key ID (kid), which is just the polite thing to do
        rsaJsonWebKey.setKeyId("k1");

        // Create the Claims, which will be the content of the JWT
        JwtClaims claims = new JwtClaims();
//        claims.setSubject("subject"); // the subject/principal is whom the token is about
        claims.setGeneratedJwtId(1); // a unique identifier for the token
        claims.setExpirationTimeMinutesInTheFuture(720); // time when the token will expire (10 minutes from now)
        claims.setIssuedAtToNow();  // when the token was issued/created (now)
        claims.setNotBeforeMinutesInThePast(2); // time before which the token is not yet valid (2 minutes ago)
//        claims.setClaim(TokenUtils.JWT_NICKNAME_FIELDNAME, userNickname);
        claims.setIssuer("Issuer");  // who creates the token and signs it


        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());

        PrivateKey pk = readPrivateKey("/META-INF/resources/privateKey.pem");
        jws.setKey(pk);

        jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
        String jwt = jws.getCompactSerialization();


        return jwt;

    }

    private static String readTokenContent(String jsonResName) throws IOException {
        InputStream contentIS = org.acme.config.jwt.TokenUtils.class.getResourceAsStream(jsonResName);
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(contentIS))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        }
    }


    /**
     * Read a PEM encoded private key from the classpath
     *
     * @param pemResName - key file resource name
     * @return PrivateKey
     * @throws Exception on decode failure
     */
    public static PrivateKey readPrivateKey(final String pemResName) throws Exception {
        InputStream contentIS = org.acme.config.jwt.TokenUtils.class.getResourceAsStream("/META-INF/resources/privateKey.pem");
        byte[] tmp = new byte[4096];
        int length = contentIS.read(tmp);
        return decodePrivateKey(new String(tmp, 0, length, "UTF-8"));
    }

    public static PublicKey readPublicKey(final String pemResName) throws Exception {
        InputStream contentIS = new FileInputStream(pemResName);
        byte[] tmp = new byte[4096];
        int length = contentIS.read(tmp);
        return decodePublicKey(new String(tmp, 0, length, "UTF-8"));
    }

    /**
     * Decode a PEM encoded private key string to an RSA PrivateKey
     *
     * @param pemEncoded - PEM string for private key
     * @return PrivateKey
     * @throws Exception on decode failure
     */
    public static PrivateKey decodePrivateKey(final String pemEncoded) throws Exception {
        byte[] encodedBytes = toEncodedBytes(pemEncoded);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(keySpec);
    }

    public static PublicKey decodePublicKey(final String pemEncoded) throws Exception {
        byte[] encodedBytes = toEncodedBytesPublicKey(pemEncoded);

        X509EncodedKeySpec spec = new X509EncodedKeySpec(encodedBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    public static Boolean isTokenHeaderValid(HashMap<String, String> JWTHeader) {
        if (!JWTHeader.containsKey("alg"))
            return false;
        if (!JWTHeader.get("alg").equals(AlgorithmIdentifiers.RSA_USING_SHA256))
            return false;

        return true;
    }

    private static byte[] toEncodedBytes(final String pemEncoded) {
        final String normalizedPem = removeBeginEnd(pemEncoded);
        return Base64.getDecoder().decode(normalizedPem);
    }

    private static byte[] toEncodedBytesPublicKey(final String pemEncoded) {
        final String normalizedPem = removeBeginEndPublicKey(pemEncoded);
        return Base64.getDecoder().decode(normalizedPem);
    }

    private static String removeBeginEnd(String pem) {
        pem = pem.replaceAll("-----BEGIN (.*)-----", "");
        pem = pem.replaceAll("-----END (.*)----", "");
        pem = pem.replaceAll("\r\n", "");
        pem = pem.replaceAll("\n", "");
        return pem.trim();
    }

    private static String removeBeginEndPublicKey(String pem) {
        pem = pem.replaceAll("-----BEGIN PUBLIC KEY-----", "");
        pem = pem.replaceAll("-----END PUBLIC KEY-----", "");
        pem = pem.replaceAll("\r\n", "");
        pem = pem.replaceAll("\n", "");
        return pem.trim();
    }

}
