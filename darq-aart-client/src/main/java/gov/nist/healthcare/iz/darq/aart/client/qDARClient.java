package gov.nist.healthcare.iz.darq.aart.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.iz.darq.aart.client.domain.AuthorizationFailure;
import gov.nist.healthcare.iz.darq.aart.client.domain.FacilityIdentifier;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisReport;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.io.IOUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class qDARClient {

    private RestTemplate wire;
    private final String PREFIX = "/qdar/aart/client";
    private final String FACILITIES = "/facilities";
    private final String REPORT_BY_FACILITY_ID = "/report/facility/";
    private final String REPORT_BY_FACILITY_NAME = "/report/facility/name/";
    private final String ALL_REPORTS = "/report/all";

    private final PrivateKey privateKey;
    private String secret;
    private String protocol;
    private String authority;

    public qDARClient(String protocol, String authority, String secret, PrivateKey privateKey) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException {
        wire = SSLRestTemplateFactory.createSSLRestTemplate();
        this.protocol = protocol;
        this.authority = authority;
        this.secret = secret;
        this.privateKey = privateKey;
    }

    AuthorizationFailure handleError(HttpClientErrorException exception) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        OpAck ack = mapper.readValue(exception.getResponseBodyAsString(), OpAck.class);
        return new AuthorizationFailure(ack.getText());
    }

    public List<FacilityIdentifier> getFacilities() throws URISyntaxException, AuthorizationFailure, IOException {
        RequestEntity<Void> httpEntity = new RequestEntity<Void>(this.createToken(), HttpMethod.GET, this.getURL(FACILITIES, null));
        try {
            ResponseEntity<List<FacilityIdentifier>> response = wire.exchange(httpEntity, new ParameterizedTypeReference<List<FacilityIdentifier>>() {});
            return response.getBody();
        } catch (HttpClientErrorException exception) {
            throw handleError(exception);
        }
    }

    public List<AnalysisReport> getReportsByFacilityName(String facilityName) throws URISyntaxException {
        RequestEntity<Void> httpEntity = new RequestEntity<>(this.createToken(), HttpMethod.GET, this.getURL(REPORT_BY_FACILITY_NAME, facilityName));
        ResponseEntity<List<AnalysisReport>> response = wire.exchange(httpEntity, new ParameterizedTypeReference<List<AnalysisReport>>() {});
        return response.getBody();
    }

    public List<AnalysisReport> getReportsByFacilityId(String facilityId) throws URISyntaxException {
        RequestEntity<Void> httpEntity = new RequestEntity<>(this.createToken(), HttpMethod.GET, this.getURL(REPORT_BY_FACILITY_ID, facilityId));
        ResponseEntity<List<AnalysisReport>> response = wire.exchange(httpEntity, new ParameterizedTypeReference<List<AnalysisReport>>() {});
        return response.getBody();
    }

    public Map<String, List<AnalysisReport>> getAllReports() throws URISyntaxException {
        RequestEntity<Void> httpEntity = new RequestEntity<>(this.createToken(), HttpMethod.GET, this.getURL(ALL_REPORTS, null));
        ResponseEntity<Map<String, List<AnalysisReport>>> response = wire.exchange(httpEntity, new ParameterizedTypeReference<Map<String, List<AnalysisReport>>>() {});
        return response.getBody();
    }

    URI getURL(String path, String param) throws URISyntaxException {
        String fullPath = param == null || param.isEmpty() ? path : path + "/" + param;
        return new URI(this.protocol, this.authority, PREFIX + fullPath, null, null);
    }

    public HttpHeaders createToken() {
        HttpHeaders headers = new HttpHeaders();
        String token = Jwts.builder()
                .claim("secret", this.secret)
                .setSubject("AART")
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 1000))
                .signWith(SignatureAlgorithm.RS256, this.privateKey).compact();
        headers.add("Authentication", token);
        return headers;
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, URISyntaxException, AuthorizationFailure, InvalidKeySpecException {
        PrivateKey privateKey = privateKey();
        qDARClient qDARClient = new qDARClient("http", "localhost:8081", "_SECRET_" , privateKey);
        System.out.println(qDARClient.getFacilities());
        System.out.println(qDARClient.getAllReports());
        System.out.println(qDARClient.getReportsByFacilityId("_FACILITY_ID_"));
        System.out.println(qDARClient.getReportsByFacilityName("MI IIS"));
    }

    public static PrivateKey privateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] bytes = IOUtils.toByteArray(new FileInputStream("_PRIVATE_KEY_PATH_"));
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(ks);
    }

}
