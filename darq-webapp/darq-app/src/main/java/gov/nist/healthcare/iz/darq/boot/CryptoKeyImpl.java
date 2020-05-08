package gov.nist.healthcare.iz.darq.boot;

import gov.nist.healthcare.auth.service.CryptoKey;
import gov.nist.healthcare.iz.darq.adf.utils.crypto.CryptoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

@Service
public class CryptoKeyImpl implements CryptoKey {

    @Autowired
    CryptoUtils cryptoUtils;

    @Override
    public PrivateKey getPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        return cryptoUtils.privateKey();
    }

    @Override
    public PublicKey getPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        return cryptoUtils.publicKey();
    }
}
