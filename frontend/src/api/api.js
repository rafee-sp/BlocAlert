import axios from "axios";
import * as Sentry from "@sentry/react";
import { captureApiError } from "../utils/sentryUtils";

const api = axios.create({
  baseURL: import.meta.env.VITE_BACKEND_URL,
  withCredentials: true,
  timeout: 20000,
});

api.interceptors.response.use(
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

export default api;
