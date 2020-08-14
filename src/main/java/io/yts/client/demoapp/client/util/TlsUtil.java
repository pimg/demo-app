package io.yts.client.demoapp.client.util;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.yts.client.demoapp.client.YtsWebClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Component
public class TlsUtil {

	@Bean
	public SslContext createSslContext() throws InvalidKeySpecException, IOException, NoSuchAlgorithmException, URISyntaxException, CertificateException {
		return SslContextBuilder
				.forClient()
				.keyManager(createPrivateKey("/tls/tls-private-key.pem"), createCertificate("/tls/tls-certificate.pem"))
				.build();
	}

	private PrivateKey createPrivateKey(String keyFileLocation) throws URISyntaxException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		String privateKeyContent = new String(Files.readAllBytes(Paths.get(TokenUtil.class.getResource(keyFileLocation).toURI())));
		privateKeyContent = privateKeyContent.replaceAll("\\n", "").replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "");

		KeyFactory kf = KeyFactory.getInstance("RSA");

		PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent));
		return kf.generatePrivate(keySpecPKCS8);
	}

	private X509Certificate createCertificate(String certificateFileLocation) throws CertificateException, FileNotFoundException {
		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
		InputStream certificateFileInputStream = YtsWebClient.class.getResourceAsStream(certificateFileLocation);
		return (X509Certificate) certificateFactory.generateCertificate(certificateFileInputStream);

	}

}
