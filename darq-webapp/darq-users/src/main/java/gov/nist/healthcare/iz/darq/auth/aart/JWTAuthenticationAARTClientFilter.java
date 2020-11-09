package gov.nist.healthcare.iz.darq.auth.aart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.iz.darq.access.service.ConfigurableService;
import gov.nist.healthcare.iz.darq.model.ToolConfigurationKey;
import gov.nist.healthcare.iz.darq.model.ToolConfigurationKeyValue;
import gov.nist.healthcare.iz.darq.model.ToolConfigurationProperty;

import gov.nist.healthcare.iz.darq.users.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.apache.commons.io.IOUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

public class JWTAuthenticationAARTClientFilter extends GenericFilterBean implements ConfigurableService {

    private final RequestMatcher pathMatcher;
    private final Environment environment;
    private String secret;
    private final String SECRET_KEY = "aart.client.secret";
    private final String PUBLIC_KEY = "aart.connector.publicKey.location";
    private PublicKey publicKey;

    public JWTAuthenticationAARTClientFilter(Environment environment, String path) {
        this.environment = environment;
        this.pathMatcher = new AntPathRequestMatcher(path);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if(!this.pathMatcher.matches(httpRequest)) {
            filterChain.doFilter(request, response);
            return;
        }

        if(Strings.isNullOrEmpty(secret)) {
            this.writeFailure(httpResponse, "No client secret found");
        } else {
            String token = httpRequest.getHeader("Authentication");
            if (!Strings.isNullOrEmpty(token)) {
                try {
                    Jws<Claims> jwt = Jwts.parser()
                            .setSigningKey(this.publicKey)
                            .parseClaimsJws(token);

                    String clientSecret = jwt.getBody().get("secret", String.class);
                    if(secret.equals(clientSecret)) {
                        filterChain.doFilter(request, response);
                    } else {
                        this.writeFailure(httpResponse, "Invalid client secret");
                    }
                } catch (Exception e) {
                    this.writeFailure(httpResponse, e.getMessage());
                }
            } else {
                this.writeFailure(httpResponse, "No authentication token provided");
            }
        }
    }

    void writeFailure(HttpServletResponse response, String message) throws IOException {
        response.setStatus(401);
        OpAck<Void> userOpAck = new OpAck<>(OpAck.AckStatus.FAILED, message, null, "AART_CLIENT_AUTHENTICATION");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getWriter(), userOpAck);
    }

    @Override
    public boolean shouldReloadOnUpdate(Set<ToolConfigurationKeyValue> keyValueSet) {
        return keyValueSet.stream().anyMatch(k -> k.getKey().equals(SECRET_KEY) || k.getKey().equals(PUBLIC_KEY));
    }

    @Override
    public void configure(Properties properties) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        this.secret = properties.getProperty(SECRET_KEY);
        byte[] cert_bytes = IOUtils.toByteArray(new FileInputStream(properties.getProperty(PUBLIC_KEY)));
        X509EncodedKeySpec ks = new X509EncodedKeySpec(cert_bytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        this.publicKey = kf.generatePublic(ks);
    }

    @Override
    public Set<ToolConfigurationProperty> initialize() {
        return new HashSet<>(
                Arrays.asList(
                        new ToolConfigurationProperty(SECRET_KEY, this.environment.getProperty(SECRET_KEY), false),
                        new ToolConfigurationProperty(PUBLIC_KEY, this.environment.getProperty(PUBLIC_KEY), true)
                )
        );
    }

    @Override
    public Set<ToolConfigurationKey> getConfigurationKeys() {
        return new HashSet<>(
                Arrays.asList(
                        new ToolConfigurationKey(SECRET_KEY, false),
                        new ToolConfigurationKey(PUBLIC_KEY, true)
                )
        );
    }

    @Override
    public OpAck<Void> checkServiceStatus() {
        if(secret == null) {
            return new OpAck<>(OpAck.AckStatus.FAILED, "AART client secret is not set", null, "AART CLIENT AUTHENTICATION");
        }

        if(publicKey == null) {
            return new OpAck<>(OpAck.AckStatus.FAILED, "AART public key not found or invalid", null, "AART CONNECTOR");
        }

        return new OpAck<>(OpAck.AckStatus.SUCCESS, "AART client authentication is valid", null, "AART CLIENT AUTHENTICATION");
    }

    @Override
    public String getServiceDisplayName() {
        return "AART CLIENT AUTHENTICATION";
    }

}
