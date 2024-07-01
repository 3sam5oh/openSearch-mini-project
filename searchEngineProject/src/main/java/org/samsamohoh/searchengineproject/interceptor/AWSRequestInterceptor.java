package org.samsamohoh.searchengineproject.interceptor;

import com.amazonaws.DefaultRequest;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.Signer;
import com.amazonaws.http.HttpMethodName;
import org.apache.http.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static org.apache.http.protocol.HttpCoreContext.HTTP_TARGET_HOST;

public class AWSRequestInterceptor implements HttpRequestInterceptor {

    private final String service;
    private final Signer signer;
    private final AWSCredentialsProvider awsCredentialsProvider;

    public AWSRequestInterceptor(String service,
                                 Signer signer,
                                 AWSCredentialsProvider awsCredentialsProvider) {
        this.service = service;
        this.signer = signer;
        this.awsCredentialsProvider = awsCredentialsProvider;
    }

    @Override
    public void process(HttpRequest httpRequest, HttpContext httpContext) throws HttpException, IOException {

        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(httpRequest.getRequestLine().getUri());
        } catch ( URISyntaxException e ) {
            throw new IOException("Invalid URI", e);
        }

        DefaultRequest<?> defaultRequest = new DefaultRequest<>(service);

        HttpHost host = (HttpHost) httpContext.getAttribute(HTTP_TARGET_HOST);
        if (host != null) {
            defaultRequest.setEndpoint(URI.create(host.toURI()));
        }

        final HttpMethodName httpMethod =
                HttpMethodName.fromValue(httpRequest.getRequestLine().getMethod());
        defaultRequest.setHttpMethod(httpMethod);
        try {
            defaultRequest.setResourcePath(uriBuilder.build().getRawPath());
        } catch (URISyntaxException e) {
            throw new IOException("Invalid URI", e);
        }

        if (httpRequest instanceof HttpEntityEnclosingRequest httpEntityEnclosingRequest) {
            if (httpEntityEnclosingRequest.getEntity() == null) {
                defaultRequest.setContent(new ByteArrayInputStream(new byte[0]));
            } else {
                defaultRequest.setContent(httpEntityEnclosingRequest.getEntity().getContent());
            }
        }

        defaultRequest.setParameters(nvpToMapParams(uriBuilder.getQueryParams()));
        defaultRequest.setHeaders(headerArrayToMap(httpRequest.getAllHeaders()));

        signer.sign(defaultRequest, awsCredentialsProvider.getCredentials());

        httpRequest.setHeaders(mapToHeaderArray(defaultRequest.getHeaders()));
        if (httpRequest instanceof HttpEntityEnclosingRequest httpEntityEnclosingRequest) {
            if(httpEntityEnclosingRequest.getEntity() != null) {
                BasicHttpEntity basicHttpEntity = new BasicHttpEntity();
                basicHttpEntity.setContent(defaultRequest.getContent());
                httpEntityEnclosingRequest.setEntity(basicHttpEntity);
            }
        }
    }

    private static Map<String, List<String>> nvpToMapParams(List<NameValuePair> queryParams) {

        Map<String, List<String>> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (NameValuePair nvp : queryParams) {
            List<String> argsList =
                    map.computeIfAbsent(nvp.getName(), k -> new ArrayList<>());
            argsList.add(nvp.getValue());
        }
        return map;
    }

    private static Map<String, String> headerArrayToMap(Header[] allHeaders) {

        Map<String, String> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Header header : allHeaders) {
            if (!skipHeader(header)) {
                map.put(header.getName(), header.getValue());
            }
        }

        return map;
    }

    private static boolean skipHeader(Header header) {
        return ("content-length".equalsIgnoreCase(header.getName()))
                && "0".equals(header.getValue())
                || "host".equalsIgnoreCase(header.getName());
    }


    private Header[] mapToHeaderArray(Map<String, String> mapHeaders) {

        Header[] headers = new Header[mapHeaders.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : mapHeaders.entrySet()) {
            headers[i++] = new BasicHeader(entry.getKey(), entry.getValue());
        }

        return headers;
    }
}
