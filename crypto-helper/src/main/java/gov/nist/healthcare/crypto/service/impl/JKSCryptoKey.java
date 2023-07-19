package gov.nist.healthcare.crypto.service.impl;

import gov.nist.healthcare.crypto.service.CryptoKey;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.security.*;
import java.security.cert.Certificate;

public class JKSCryptoKey implements CryptoKey {

    PrivateKey privateKey;
    PublicKey publicKey;

    public JKSCryptoKey(String jks, String keyAlias, String storePassword, String keyPassword) throws Exception {
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(Files.newInputStream(new File(jks).toPath()), storePassword.toCharArray());

        Certificate cert = keystore.getCertificate(keyAlias);
        // Get public key
        this.publicKey = cert.getPublicKey();

        Key key = keystore.getKey(keyAlias, keyPassword.toCharArray());
        if (key instanceof PrivateKey) {
            this.privateKey = (PrivateKey) key;
        } else {
            throw new Exception("Key "+ keyAlias + " in JKS " + jks + " is not an RSA private key");
        }
    }

    @Override
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    @Override
    public PublicKey getPublicKey() {
        return publicKey;
    }
}
