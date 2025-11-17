import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import { Auth0Provider } from '@auth0/auth0-react'
import * as Sentry from "@sentry/react";

/*
Sentry.init({
  dsn: import.meta.env.VITE_SENTRY_DSN,
  integrations: [
    Sentry.browserTracingIntegration(),
    Sentry.replayIntegration({
      maskAllText: false,
      blockAllMedia: false,
    }), 
  ],
  tracesSampleRate: 1.0,
  replaysSessionSampleRate: 0.1, 
  replaysOnErrorSampleRate: 1.0,
  sendDefaultPii: true,
}) */


const onRedirectCallback = (appState) => {
  try {
    const returnTo = appState?.returnTo || window.location.pathname || "/";
    window.history.replaceState({}, document.title, returnTo);
  } catch (error) {
    console.error("Redirect callback error:", error);
    Sentry.captureException(error);
    window.location.href = "/";
  }
};

createRoot(document.getElementById('root')).render(
  
    <Auth0Provider
      domain={import.meta.env.VITE_AUTH0_DOMAIN}
      clientId={import.meta.env.VITE_AUTH0_CLIENT_ID}
      authorizationParams={{
        redirect_uri: `${window.location.origin}/callback`,
        audience: `${import.meta.env.VITE_AUTH0_AUDIENCE}`,
        scope: "openid profile email offline_access",
      }}
      useRefreshTokens={true}
      cacheLocation="memory"
      useRefreshTokensFallback={true}
      onRedirectCallback={onRedirectCallback}
    >
      <App />
    </Auth0Provider>
  ,
)
