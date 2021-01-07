package com.sirv.spring;

import com.sirv.RestClient;
import com.sirv.exception.UnauthorizedRestClientException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;


public class RestTemplateAdapter implements RestClient {
    private RestTemplate restTemplate;

    public RestTemplateAdapter() {
        restTemplate = new RestTemplate();
    }

    public RestTemplateAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public <T> T doPost(String url, Object request, Class<T> responseType, Map<String, String> headers) {
        return doPost(url, request, responseType, headers, null);
    }

    public <T> T doPost(String url, Object request, Class<T> responseType, Map<String, String> headers, Map<String, String> queryParams) {
        HttpEntity httpEntity = new HttpEntity<>(request, toMultiValueMap(headers));
        return doRequest(url, HttpMethod.POST, httpEntity, responseType, queryParams);
    }

    @Override
    public <T> T doGet(String url, Class<T> responseType, Map<String, String> headers) {
        return doGet(url, responseType, headers, null);
    }

    @Override
    public <T> T doGet(String url, Class<T> responseType, Map<String, String> headers , Map<String, String> queryParams) {
        HttpEntity httpEntity = new HttpEntity(toMultiValueMap(headers));
        return doRequest(url, HttpMethod.GET, httpEntity, responseType, queryParams);
    }

    @Override
    public <K, V> Map<K, V> doGetForMap(String url, Map<String, String> headers) {
        return doGetForMap(url, headers, null);
    }

    @Override
    public <K, V> Map<K, V> doGetForMap(String url, Map<String, String> headers, Map<String, String> queryParams) {
        HttpEntity httpEntity = new HttpEntity(toMultiValueMap(headers));
        return doRequest(url, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<Map<K, V>>() {}, queryParams);
    }

    @Override
    public <T> List<T> doGetForList(String url, Map<String, String> headers) {
        return doGetForList(url, headers, null);
    }

    @Override
    public <T> List<T> doGetForList(String url, Map<String, String> headers, Map<String, String> queryParams) {
        HttpEntity httpEntity = new HttpEntity(toMultiValueMap(headers));
        return doRequest(url, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<T>>() {}, queryParams);
    }

    @Override
    public <T> T doDelete(String url, Object request, Class<T> responseType, Map<String, String> headers) {
        return doDelete(url, request, responseType, headers, null);
    }

    @Override
    public <T> T doDelete(String url, Object request, Class<T> responseType, Map<String, String> headers, Map<String, String> queryParams) {
        HttpEntity httpEntity = new HttpEntity<>(request, toMultiValueMap(headers));
        return doRequest(url, HttpMethod.DELETE, httpEntity, responseType, queryParams);
    }

    private <T> T doRequest(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType, Map<String, String> queryParams) {
        try {
            ResponseEntity<T> responseEntity = restTemplate.exchange(buildWithQueryParams(url, queryParams), method, requestEntity, responseType);
            return responseEntity.getBody();
        } catch (RestClientResponseException e) {
            if (e.getRawStatusCode() == 401) {
                throw new UnauthorizedRestClientException(e.getResponseBodyAsString(), e, e.getRawStatusCode());
            } else {
                throw new com.sirv.exception.RestClientException(e.getResponseBodyAsString(), e, e.getRawStatusCode());
            }
        } catch (RestClientException e) {
            throw new com.sirv.exception.RestClientException(e);
        }
    }

    private <T> T doRequest(String url, HttpMethod method, HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType, Map<String, String> queryParams) {
        try {
            ResponseEntity<T> responseEntity = restTemplate.exchange(buildWithQueryParams(url, queryParams), method, requestEntity, responseType);
            return responseEntity.getBody();
        } catch (RestClientResponseException e) {
            if (e.getRawStatusCode() == 401) {
                throw new UnauthorizedRestClientException(e.getResponseBodyAsString(), e, e.getRawStatusCode());
            } else {
                throw new com.sirv.exception.RestClientException(e.getResponseBodyAsString(), e, e.getRawStatusCode());
            }
        } catch (RestClientException e) {
            throw new com.sirv.exception.RestClientException(e);
        }
    }

    private String buildWithQueryParams(String url, Map<String, String> queryParams) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url);
        if (queryParams != null) {
            queryParams.forEach(uriComponentsBuilder::queryParam);
        }
        return uriComponentsBuilder.build().toString();
    }

    private <K, V> MultiValueMap<K, V> toMultiValueMap(Map<K, V> map) {
        MultiValueMap<K, V> multiValueMap = new LinkedMultiValueMap<>();

        map.forEach(multiValueMap::add);

        return multiValueMap;
    }
}
