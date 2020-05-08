package gov.nist.healthcare.auth.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

public interface CryptoKey {
    PrivateKey getPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException;
    PublicKey getPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException;
}
