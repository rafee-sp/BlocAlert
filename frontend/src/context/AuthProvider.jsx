import { useCallback, useEffect, useMemo, useState } from "react";
import { AuthContext } from "./AuthContext";
import { useAuth0 } from "@auth0/auth0-react";
import authApi, { setToken } from "../api/authApi";
import LoadingSpinner from "../components/LoadingSpinner";
import { toggleTheme } from "../utils/toggleTheme";

export const AuthProvider = ({ children }) => {
  const {
    isLoading: auth0Loading,
    isAuthenticated,
    getAccessTokenSilently,
    loginWithRedirect,
    logout: auth0Logout,
  } = useAuth0();

  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const savedTheme = localStorage.getItem("theme") || "dark";
    toggleTheme(savedTheme);
  }, []);

  const authInit = useCallback(async () => {
    if (!isAuthenticated) {
      setIsLoading(false);
      return;
    }

    try {
      setIsLoading(true);
      setToken(getAccessTokenSilently);

      const { data : {data} } = await authApi.get("/users/me");
      setUser(data);

      const theme = data?.themePreference || "dark";
      localStorage.setItem("theme", theme);
      toggleTheme(theme);

    } catch (err) {
      console.error("Error fetching user:", err);
      setUser(null);
    } finally {
      setIsLoading(false);
    }
  }, [isAuthenticated, getAccessTokenSilently]);

  useEffect(() => {
    if (!auth0Loading) {
      authInit();
    }
  }, [auth0Loading, authInit]);

  const value = useMemo(
    () => ({
      user,
      isAuthenticated,
      isLoading,
      refreshUser: authInit,
      getAccessToken: getAccessTokenSilently,
      login: loginWithRedirect,
      logout: auth0Logout,
      setUser,
    }),
    [user, isAuthenticated, isLoading, authInit, getAccessTokenSilently, loginWithRedirect, auth0Logout, setUser]
  );

  if (auth0Loading || isLoading) {
    return <LoadingSpinner />;
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};