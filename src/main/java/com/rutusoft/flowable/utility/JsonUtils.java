package com.rutusoft.flowable.utility;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 🔹 Convert JSON String → Map
    public static Map<String, Object> toMap(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JSON to Map", e);
        }
    }

    // 🔹 Convert JSON String → List<Map>
    public static List<Map<String, Object>> toList(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JSON to List", e);
        }
    }

    // 🔹 Convert Object → Map (very useful for RestTemplate response)
    public static Map<String, Object> toMap(Object object) {
        return objectMapper.convertValue(object, new TypeReference<Map<String, Object>>() {});
    }

    // 🔹 Convert Object → List<Map>
    public static List<Map<String, Object>> toList(Object object) {
        return objectMapper.convertValue(object, new TypeReference<List<Map<String, Object>>>() {});
    }

    // 🔹 Safe getters
    public static String getString(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? val.toString() : null;
    }

    public static Long getLong(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? ((Number) val).longValue() : null;
    }

    public static Integer getInt(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? ((Number) val).intValue() : null;
    }

    public static Boolean getBoolean(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? (Boolean) val : null;
    }

    // 🔹 Nested Map
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getMap(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? (Map<String, Object>) val : Collections.emptyMap();
    }

    // 🔹 Nested List
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> getList(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? (List<Map<String, Object>>) val : Collections.emptyList();
    }

    public static LocalDateTime getLocalDateTime(Map<String, Object> map, String key) {
        Object val = map.get(key);

        if (val == null) return null;

        try {
            // ✅ Case 1: Already LocalDateTime
            if (val instanceof LocalDateTime) {
                return (LocalDateTime) val;
            }

            // ✅ Case 2: java.util.Date
            if (val instanceof Date) {
                return ((Date) val).toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
            }

            // ✅ Case 3: ISO String (your current case)
            if (val instanceof String) {
                String str = (String) val;

                // Handles: 2026-03-31T12:50:24.918824Z
                return Instant.parse(str)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
            }

            // ✅ Case 4: OffsetDateTime
            if (val instanceof OffsetDateTime) {
                return ((OffsetDateTime) val)
                        .atZoneSameInstant(ZoneId.systemDefault())
                        .toLocalDateTime();
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse LocalDateTime for key: " + key + ", value: " + val, e);
        }

        return null;
    }
}