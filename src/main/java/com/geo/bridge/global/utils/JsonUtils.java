package com.geo.bridge.global.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtils {
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // JavaTimeModule 등록
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);  // 타임스탬프 형식 방지
        // TODO jackson 설정 추가
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static ObjectMapper getInstance(){
        return objectMapper;
    }

    /**
     * 객체를 JSON 문자열로 변환
     *
     * @param object 변환할 객체
     * @return JSON 문자열
     */
    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch(JsonProcessingException e) {
            log.warn("WARN: ", e);
            return null;
        }
    }

    /**
     * JSON 문자열을 객체로 변환
     *
     * @param json  JSON 문자열
     * @param clazz 변환할 클래스 타입
     * @param <T>   클래스 타입
     * @return 변환된 객체
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch(JsonProcessingException e) {
            log.warn("WARN: ", e);
            return null;
        }
    }

    /**
     * JSON 문자열을 객체로 변환
     *
     * @param json  JSON 문자열
     * @param clazz 변환할 클래스 타입
     * @param <T>   클래스 타입
     * @return 변환된 객체
     */
    public static <T> T fromJson(String json, TypeReference<T> valueTypeRef) {
        try {
            return objectMapper.readValue(json, valueTypeRef);
        } catch(JsonProcessingException e) {
            log.warn("WARN: ", e);
            return null;
        }
    }

    public static JsonNode toJsonNode(String json) {
        try {
            return objectMapper.readTree(json);
        } catch(JsonProcessingException e) {
            log.warn("WARN: ", e);
            return null;
        }
    }
}
