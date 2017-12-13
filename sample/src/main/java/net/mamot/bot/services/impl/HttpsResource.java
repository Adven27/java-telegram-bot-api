package net.mamot.bot.services.impl;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.apache.http.impl.client.HttpClients.*;
import static org.springframework.http.HttpMethod.GET;

public class HttpsResource implements Resource {
    public String from(String url) throws IOException {
        CloseableHttpClient client = custom().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(client);

        ResponseEntity<String> response = new RestTemplate(requestFactory).exchange(url, GET, null, String.class);

        return response.getBody();
    }
}
