package io.yts.client.demoapp.client.util;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

@Component
public class TlsUtil {

	private final String keystorePassword;
	private final String alias;

	public TlsUtil(@Value("${client.tls.keystore.password}") String keystorePassword, @Value("${client.tls.keystore.alias}") String alias) {
		this.keystorePassword = keystorePassword;
		this.alias = alias;
	}

	@Bean
	public SslContext createSslContext() throws IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, KeyStoreException {
		return SslContextBuilder
				.forClient()
				.keyManager(CertificateManager.getPrivateKey("tls/keystore.p12", keystorePassword, alias), CertificateManager.getCertificate("tls/keystore.p12", keystorePassword, alias))
				.build();
	}


}
