import axios from "axios";
import * as Sentry from "@sentry/react";
import { generateRequestId } from "../utils/generateRequestId";
import { captureApiError } from "../utils/sentryUtils";


const authApi = axios.create({
    baseURL: import.meta.env.VITE_BACKEND_URL,
    withCredentials: true,
    timeout: 20000,
})

let getToken = null;

export const setToken = (token) => {
    getToken = token;
}

authApi.interceptors.request.use(

    async (config) => {
        try {
    
            config.headers["X-REQUEST-ID"] = generateRequestId();

            if (getToken) {
                const token = await getToken({
                    audience: import.meta.env.VITE_AUTH0_AUDIENCE,
                })
                if (token) {
                    config.headers.Authorization = `Bearer ${token}`
                }
            }
            return config;
        } catch (error) {
            Sentry.withScope((scope) => {
                scope.setContext("auth_request", {
                    audience: import.meta.env.VITE_AUTH0_AUDIENCE,
                    endpoint: config?.url || "unknown",
                    method: config?.method || "unknown",
                });
                scope.setTag("type", "token_fetch_failure");
                Sentry.captureException(error);
            });
            console.error("Failed to get access token : ", error);
            return Promise.reject(error);
        }
    },
    (error) => {
        Sentry.captureException(error);
        return Promise.reject(error);
    }
);



authApi.interceptors.response.use(
    (response) => response,
    (error) => {

        if (axios.isCancel(error)) {
            return Promise.reject(error);
        }

        const status = error.response?.status;
        if (status && [401, 403].includes(status)) {
            return Promise.reject(error);
        }

        captureApiError(error);

        return Promise.reject(error);
    }
);

export default authApi;