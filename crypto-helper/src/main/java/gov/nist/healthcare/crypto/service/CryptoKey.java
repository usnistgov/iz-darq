package gov.nist.healthcare.crypto.service;

import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;

public interface CryptoKey {
    default byte[] getPublicKeyHash() throws Exception {
        return MessageDigest.getInstance("MD5").digest(this.getPublicKey().getEncoded());
    }
    PrivateKey getPrivateKey() throws Exception;
    PublicKey getPublicKey() throws Exception;
}
