package com.sirv;

import java.util.List;
import java.util.Map;

public interface RestClient {
    <T> T doPost(String url, Object request, Class<T> responseType, Map<String, String> headers);

    <T> T doPost(String url, Object request, Class<T> responseType, Map<String, String> headers, Map<String, String> queryParams);

    <T> T doGet(String url, Class<T> responseType, Map<String, String> headers);

    <T> T doGet(String url, Class<T> responseType, Map<String, String> headers, Map<String, String> queryParams);

    <K, V> Map<K, V> doGetForMap(String url, Map<String, String> headers);

    <K, V> Map<K, V> doGetForMap(String url, Map<String, String> headers, Map<String, String> queryParams);

    <T> List<T> doGetForList(String url, Map<String, String> headers);

    <T> List<T> doGetForList(String url, Map<String, String> headers, Map<String, String> queryParams);

    <T> T doDelete(String url, Object request, Class<T> responseType, Map<String, String> headers);

    <T> T doDelete(String url, Object request, Class<T> responseType, Map<String, String> headers, Map<String, String> queryParams);
}
