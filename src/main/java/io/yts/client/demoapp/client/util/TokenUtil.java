package io.yts.client.demoapp.client.util;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

public class TokenUtil {

	public static String createToken() throws NoSuchAlgorithmException, URISyntaxException, IOException, InvalidKeySpecException, JOSEException, NoSuchProviderException {
		String privateKeyContent = new String(Files.readAllBytes(Paths.get(TokenUtil.class.getResource("/tls/private-key.pem").toURI())));
		privateKeyContent = privateKeyContent.replaceAll("\\n", "").replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "");

		KeyFactory kf = KeyFactory.getInstance("RSA");

		PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent));
		PrivateKey privKey = kf.generatePrivate(keySpecPKCS8);

		JWSSigner signer = new RSASSASigner(privKey);
		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
				.issuer("30f57515-1045-45a8-8efd-54f32a46153a")
				.jwtID(UUID.randomUUID().toString())
				.issueTime(new Date())
				.notBeforeTime(new Date())
				.build();
		SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS512).keyID("affbe29f-f195-4f45-8515-52454ee6b4a0").type(JOSEObjectType.JWT).build(), claimsSet);
		signedJWT.sign(signer);
		return signedJWT.serialize();
	}
}
