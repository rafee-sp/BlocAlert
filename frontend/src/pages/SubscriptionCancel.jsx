import { useEffect, useState } from "react";
import { FaTimesCircle, FaArrowRight } from "react-icons/fa";
import { useNavigate } from "react-router-dom";

export default function SubscriptionCancel() {

    const navigate = useNavigate();
    const [secondsLeft, setSecondsLeft] = useState(8);

    useEffect(() => {
        if (secondsLeft <= 0) {
            navigate("/");
            return;
        }

        const timer = setTimeout(() => {
            setSecondsLeft(s => s - 1);
        }, 1000);

        return () => clearTimeout(timer);
    }, [secondsLeft, navigate]);

    return (
        <div className="min-h-screen flex items-center justify-center px-6 bg-gradient-to-br from-rose-50 to-pink-50 dark:from-gray-900 dark:to-gray-800 transition-colors duration-300">
            <div className="max-w-md w-full bg-white dark:bg-gray-900 rounded-3xl shadow-2xl p-8 text-center transition-colors duration-300">

                <div className="flex justify-center mb-6">
                    <div className="bg-rose-100 dark:bg-rose-900 rounded-full p-5">
                        <FaTimesCircle className="text-rose-600 dark:text-rose-400 w-14 h-14" />
                    </div>
                </div>

                <h1 className="text-2xl font-bold text-gray-900 dark:text-white mb-4">
                    Subscription Failed
                </h1>

                <p className="text-gray-600 dark:text-gray-400 mb-6">
                    We couldn’t process your payment. Please try again. You’ll be redirected to the homepage shortly.
                </p>

                <button
                    onClick={() => navigate("/")}
                    className="w-full bg-rose-600 text-white py-3 rounded-xl font-semibold hover:bg-rose-700 transition-colors flex items-center justify-center dark:hover:bg-rose-500"
                >
                    Go to Homepage
                    <FaArrowRight className="w-5 h-5 ml-2" />
                </button>

                <p className="mt-4 text-sm text-gray-500 dark:text-gray-400">
                    Redirecting in {secondsLeft}s...
                </p>

            </div>
        </div>
    );
}
