package io.yts.client.demoapp.client.util;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class CertificateManager {

	public static PrivateKey getPrivateKey(String keystoreFile, String keystorePassword, String alias) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException {
		KeyStore keystore = KeyStore.getInstance("PKCS12");
		keystore.load(CertificateManager.class.getClassLoader().getResourceAsStream(keystoreFile), keystorePassword.toCharArray());
		PrivateKey key = (PrivateKey) keystore.getKey(alias, keystorePassword.toCharArray());
		return key;
	}

	public static X509Certificate getCertificate(String keystoreFile, String keystorePassword, String alias) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
		KeyStore keystore = KeyStore.getInstance("PKCS12");
		keystore.load(CertificateManager.class.getClassLoader().getResourceAsStream(keystoreFile), keystorePassword.toCharArray());
		X509Certificate certificate = (X509Certificate) keystore.getCertificate(alias);
		return certificate;
	}
}
