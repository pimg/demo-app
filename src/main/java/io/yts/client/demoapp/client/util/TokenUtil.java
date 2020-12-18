package io.yts.client.demoapp.client.util;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.UUID;

@Component
public class TokenUtil {

	public String createToken(String keystorePassword, String alias) throws NoSuchAlgorithmException, IOException, JOSEException, KeyStoreException, CertificateException, UnrecoverableKeyException {

		PrivateKey key = CertificateManager.getPrivateKey("tls/signing-keystore.p12", keystorePassword, alias);

		JWSSigner signer = new RSASSASigner(key);
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
