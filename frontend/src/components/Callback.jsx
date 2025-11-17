import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { HiOutlineExclamationCircle } from "react-icons/hi";
import LoadingSpinner from "./LoadingSpinner";
import { useAuth } from "../context/AuthContext";

export default function Callback() {
    const location = useLocation();
    const navigate = useNavigate();
    const { isAuthenticated, isLoading } = useAuth();
    const [hasError, setHasError] = useState(false);

    // Check query params for email verification error
    useEffect(() => {
        const params = new URLSearchParams(location.search);
        const errorType = params.get("error");
        if (errorType) {
            if (errorType === "access_denied") {
                setHasError(true);
            }
        }
    }, [location.search]);

    // Redirect to home after successful login
    useEffect(() => {
        if (!isLoading && isAuthenticated && !hasError) {
            const timer = setTimeout(() => {
                navigate("/", { replace: true });
            }, 100);

            return () => clearTimeout(timer);
        }
    }, [isLoading, isAuthenticated, hasError, navigate]);

    const handleGoHome = () => navigate("/", { replace: true });

    return (
        <>
            {
                hasError ? (
                    <div className="min-h-screen flex items-center justify-center bg-gray-50 dark:bg-gray-900 px-4 transition-colors duration-300" >
                        <div className="bg-white dark:bg-gray-800 rounded-2xl shadow-lg p-6 md:p-12 max-w-md w-full text-center transition-colors duration-300">
                            <div className="mb-4">
                                <HiOutlineExclamationCircle className="mx-auto h-12 w-12 text-yellow-500" />
                            </div>
                            <h1 className="text-2xl md:text-3xl font-bold text-gray-900 dark:text-gray-100 mb-4">
                                Verify Your Email
                            </h1>
                            <p className="text-gray-700 dark:text-gray-300 mb-6">
                                We’ve sent a verification link to your email. Please check your inbox and click the link before logging in.
                            </p>
                            <button
                                className="bg-blue-600 hover:bg-blue-500 text-white font-semibold py-2 px-6 rounded-lg transition-colors duration-200"
                                onClick={handleGoHome}
                            >
                                Go to Home
                            </button>
                        </div>
                    </div>

                ) : (
                    <LoadingSpinner />
                )
            }
        </>

    );
}
