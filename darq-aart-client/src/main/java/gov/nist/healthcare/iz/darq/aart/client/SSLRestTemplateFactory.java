package gov.nist.healthcare.iz.darq.aart.client;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class SSLRestTemplateFactory {

	@SuppressWarnings("deprecation")
	public static RestTemplate createSSLRestTemplate() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {

		SSLConnectionSocketFactory socketFactory =
				new SSLConnectionSocketFactory(new SSLContextBuilder().loadTrustMaterial(null, new TrustAllStrategy()).build(), SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).setHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).build();
		HttpComponentsClientHttpRequestFactory fct = new HttpComponentsClientHttpRequestFactory(httpClient);
		RestTemplate tmpl = new RestTemplate(fct);
		List<ClientHttpRequestInterceptor> interceptors = tmpl.getInterceptors();
		if (CollectionUtils.isEmpty(interceptors)) {
			interceptors = new ArrayList<>();
		}
		interceptors.add(new RestTemplateHeaderModifierInterceptor());
		tmpl.setInterceptors(interceptors);
		return tmpl;

	}
}