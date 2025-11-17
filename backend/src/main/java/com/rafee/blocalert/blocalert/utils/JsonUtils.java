package com.rafee.blocalert.blocalert.utils;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.util.StringUtils;

public class JsonUtils {

    public static String getStringField(JsonNode node, String field) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            throw new IllegalStateException("Json node is null or missing");
        }

        JsonNode valueNode = node.path(field);
        if (valueNode.isMissingNode() || valueNode.isNull() || !StringUtils.hasText(valueNode.asText())) {
            throw new IllegalStateException("Missing or empty field '" + field + "' in JSON node");
        }

        return valueNode.asText();
    }

    public static JsonNode getJsonNode(JsonNode node, String field) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            throw new IllegalStateException("Json node is null or missing for parent node");
        }

        JsonNode childNode = node.path(field);
        if (childNode.isMissingNode() || childNode.isNull()) {
            throw new IllegalStateException("Required field '" + field + "' is missing or null in JSON");
        }

        return childNode;
    }

}
