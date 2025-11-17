import * as Sentry from "@sentry/react";

export const captureWebSocketError = (error, extra = {}, socketRef = null, isConnected = false) => {
    Sentry.withScope((scope) => {
        const wsContext = {
            url: socketRef?.url || "unknown",
            readyState: socketRef?.readyState ?? "no-state",
            isConnected,
            eventType: extra.eventType || "unknown",
            messageData: extra.messageData
                ? JSON.stringify(extra.messageData).slice(0, 500)
                : undefined,
            reason: extra.reason,
            retryCount: extra.retryCount,
            timestamp: new Date().toISOString(),
        };

        scope.setContext("websocket", wsContext);
        Sentry.captureException(
            error instanceof Error ? error : new Error(String(error))
        );
    });
};

export const captureApiError = (error) => {
    try {
        const status = error.response?.status || "no-status";
        const sanitizedResponse = sanitizeData(error.response?.data);

        const requestId =
            error.response?.headers?.["x-request-id"] ||
            error.config?.headers?.["x-request-id"] ||
            "unknown";

        Sentry.withScope((scope) => {
            scope.setContext("axios", {
                url: error.config?.url || "unknown",
                method: error.config?.method?.toUpperCase?.() || "unknown",
                status,
                response: JSON.stringify(sanitizedResponse || "no-response"),
                params: error.config?.params || null,
                data: error.config?.data || null,
                requestId,
            });

            if (requestId && requestId !== "unknown") {
                scope.setTag("request_id", requestId);
            }

            Sentry.captureException(error);
        });
    } catch (captureErr) {
        console.error("Failed to capture error in Sentry:", captureErr);
    }
}


const sanitizeData = (data, depth = 0) => {
    const sensitive = ["email", "phoneNumber"];
    const MAX_STRING_LENGTH = 500;
    const MAX_ARRAY_LENGTH = 50;
    const MAX_DEPTH = 10;

    if (depth > MAX_DEPTH) {
        return "[Max depth reached]";
    }

    if (!data || typeof data !== "object") return data;

    const sanitizeValue = (value) => {
        if (Array.isArray(value)) {
            if (value.length > MAX_ARRAY_LENGTH) {
                return [
                    ...value.slice(0, MAX_ARRAY_LENGTH).map(sanitizeValue),
                    `[${value.length - MAX_ARRAY_LENGTH} more items truncated]`,
                ];
            }
            return value.map(sanitizeValue);
        }

        if (value && typeof value === "object") {
            return sanitizeData(value, depth + 1);
        }

        if (typeof value === "string" && value.length > MAX_STRING_LENGTH) {
            return value.slice(0, MAX_STRING_LENGTH) + "… [truncated]";
        }

        return value;
    };

    const sanitized = {};

    for (const key in data) {
        const lowerKey = key.toLowerCase();

        if (sensitive.some((s) => lowerKey.includes(s))) {
            sanitized[key] = "[REDACTED]";
        } else {
            sanitized[key] = sanitizeValue(data[key]);
        }
    }

    return sanitized;
};