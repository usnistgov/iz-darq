package gov.nist.healthcare.iz.darq.adf.merge.model;

import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.crypto.service.impl.JKSCryptoKey;

import java.security.PrivateKey;
import java.security.PublicKey;

public class LoadableCryptoKey implements CryptoKey {

    JKSCryptoKey cryptoKey;

    public void load(String jks, String keyAlias, String storePassword, String keyPassword) throws Exception {
        this.cryptoKey = new JKSCryptoKey(jks, keyAlias, storePassword, keyPassword);
    }

    @Override
    public PrivateKey getPrivateKey() throws Exception {
        if(cryptoKey.getPrivateKey() == null)
            throw new Exception("No Private Key Loaded");
        return cryptoKey.getPrivateKey();
    }

    @Override
    public PublicKey getPublicKey() throws Exception {
        if(cryptoKey.getPublicKey() == null)
            throw new Exception("No Public Key Loaded");
        return cryptoKey.getPublicKey();
    }
}
