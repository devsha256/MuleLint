package com.devsha256.mulelint.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MulePropertyExtractor {

    private static final Pattern PROPERTY_PATTERN = Pattern.compile("(?:Mule::)?p\\(['\"]([^'\"]+)['\"]\\)");
    private static final Pattern XML_PROPERTY_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");

    public static class PropertyReference {
        public final String key;
        public final int lineNumber;

        public PropertyReference(String key, int lineNumber) {
            this.key = key;
            this.lineNumber = lineNumber;
        }
    }

    public static List<PropertyReference> extractProperties(String content) {
        List<PropertyReference> references = new ArrayList<>();
        String[] lines = content.split("\\r?\\n");
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            
            Matcher matcher = PROPERTY_PATTERN.matcher(line);
            while (matcher.find()) {
                references.add(new PropertyReference(matcher.group(1), i + 1));
            }
            
            Matcher xmlMatcher = XML_PROPERTY_PATTERN.matcher(line);
            while (xmlMatcher.find()) {
                references.add(new PropertyReference(xmlMatcher.group(1), i + 1));
            }
        }
        
        return references;
    }
}
