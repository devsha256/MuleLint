package com.devsha256.mulelint.parser;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.yaml.snakeyaml.Yaml;

public class YamlParser {

    public static class YamlResult {
        public java.util.Set<String> leafKeys = new java.util.HashSet<>();
        public java.util.Set<String> parentKeys = new java.util.HashSet<>();
    }

    public static YamlResult extractFlattenedKeys(InputStream inputStream) {
        YamlResult result = new YamlResult();
        Yaml yaml = new Yaml();
        try {
            Iterable<Object> documents = yaml.loadAll(inputStream);
            for (Object doc : documents) {
                if (doc instanceof Map) {
                    flattenKeys("", (Map<?, ?>) doc, result);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static void flattenKeys(String prefix, Map<?, ?> map, YamlResult result) {
        if (map == null) return;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (entry.getKey() == null) continue;
            String key = entry.getKey().toString();
            String fullKey = prefix.isEmpty() ? key : prefix + "." + key;
            
            if (entry.getValue() instanceof Map) {
                result.parentKeys.add(fullKey);
                flattenKeys(fullKey, (Map<?, ?>) entry.getValue(), result);
            } else {
                result.leafKeys.add(fullKey);
            }
        }
    }
}
