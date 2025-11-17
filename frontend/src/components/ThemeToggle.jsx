import { useEffect, useState } from "react";
import { FaAdjust, FaMoon, FaSun } from "react-icons/fa";
import { useAuth } from "../context/AuthContext";
import authApi from "../api/authApi";
import LoadingSpinner from "./LoadingSpinner";

const ThemeToggle = () => {

    const { user, isAuthenticated, setUser } = useAuth();
    const [theme, setTheme] = useState(null);

    useEffect(() => {

        if (!isAuthenticated) {
            setTheme("dark");
            return;
        }

        if (user?.themePreference) {
            setTheme(user.themePreference);
        }

    }, []);


    useEffect(() => {

        if (!theme) return;

        document.documentElement.classList.toggle("dark", theme === "dark");

        if (!isAuthenticated) return;

        const timer = setTimeout(() => {
            if (user && theme !== user.themePreference) {
                authApi.patch("/users", { theme });
                setUser(prev => ({
                    ...prev,
                    themePreference: theme
                }));
            }
        }, 1500);

        return () => clearTimeout(timer);
    }, [theme]);

    const onThemeChange = () => {
        console.log("toggle on theme change called")
        setTheme(prev => (prev === "light" ? "dark" : "light"));
    };

    if (!theme) return <LoadingSpinner />

    return (

        <div className="relative w-12 h-6 bg-gray-300 dark:bg-gray-700 rounded-full flex items-center px-1 transition-colors duration-300"
            onClick={onThemeChange}
        >
            <div
                className={`bg-blue-500 w-5 h-5 rounded-full shadow-md transform transition-transform duration-300 ${theme === "dark" ? "translate-x-6" : "translate-x-0"
                    }`}
            >
                {theme === "light" ? (
                    <FaSun className="text-yellow-400 text-xs" />
                ) : (
                    <FaMoon className="text-gray-200 text-xs" />
                )}
            </div>
        </div>

    );

}

export default ThemeToggle;