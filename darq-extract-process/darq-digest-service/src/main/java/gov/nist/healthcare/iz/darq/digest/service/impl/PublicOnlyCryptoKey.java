package gov.nist.healthcare.iz.darq.digest.service.impl;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.common.base.Strings;
import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.iz.darq.adf.utils.crypto.impl.CryptoUtilsImpl;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class PublicOnlyCryptoKey implements CryptoKey {

    PublicKey publicKey;

    public PublicOnlyCryptoKey() throws Exception {
    }

    public void setPublicKeyFromResource() throws Exception {
        InputStream key = PublicOnlyCryptoKey.class.getResourceAsStream("/" + CryptoUtilsImpl.PUB_KEY_RESOURCE_NAME);
        if(key == null) {
            throw new Exception("Public Key in resource at location " + CryptoUtilsImpl.PUB_KEY_RESOURCE_NAME + " not found");
        } else {
            this.publicKey = getPublicKeyFromStream(key);
        }
    }

    public void setPublicKeyFromLocation(String publicKeyLocation) throws Exception {
        if(!Strings.isNullOrEmpty(publicKeyLocation)) {
            Path path = Paths.get(publicKeyLocation);
            if(path.toFile().exists()) {
                this.publicKey = getPublicKeyFromPEMStream(new FileInputStream(path.toFile()));
            } else {
                throw new Exception("Public Key at location " + publicKeyLocation + "not found");
            }
        } else {
            throw new Exception("Empty location is invalid ");
        }
    }

    private PublicKey getPublicKeyFromPEMStream(InputStream pkeyStream) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] bytes = IOUtils.toByteArray(pkeyStream);
        String key = new String(bytes, Charset.defaultCharset());

        String publicKeyPEM = key
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PUBLIC KEY-----", "");

        byte[] encoded = Base64.decodeBase64(publicKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return keyFactory.generatePublic(keySpec);
    }

    private PublicKey getPublicKeyFromStream(InputStream pkeyStream) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] bytes = IOUtils.toByteArray(pkeyStream);
        X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(ks);
    }

    @Override
    public PrivateKey getPrivateKey() throws Exception {
        throw new Exception("Unsupported Private Key");
    }

    @Override
    public PublicKey getPublicKey() {
        return publicKey;
    }
}
