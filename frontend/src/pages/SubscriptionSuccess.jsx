import React, { useEffect, useRef, useState } from "react";
import {
  FaCheckCircle,
  FaEnvelope,
  FaCalendarAlt,
  FaCreditCard,
  FaArrowRight,
  FaExclamationCircle,
  FaSpinner,
} from "react-icons/fa";
import { useNavigate } from "react-router-dom";
import LoadingSpinner from "../components/LoadingSpinner";
import { formatDate } from "../utils/format";
import { useAuth } from "../context/AuthContext";
import authApi from "../api/authApi";

export default function SubscriptionSuccess() {

  const [subscriptionData, setSubscriptionData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [status, setStatus] = useState("activating");
  const [error, setError] = useState("");
  const navigate = useNavigate();
  const pollingRef = useRef();

  const { refreshUser } = useAuth();
  const MAX_ATTEMPTS = 12; // 1 min

  useEffect(() => {

    let attempts = 0;

    const params = new URLSearchParams(window.location.search);
    const sessionId = params.get("session_id");

    if (!sessionId) {
      setError("Subscription session not found. Please try again.");
      setLoading(false);
      setStatus("error");
      return;
    }

    const getSubscriptionDetails = async () => {
      try {
        attempts++;

        const { data: { data: subscriptionData } } = await authApi.post(
          `/subscription/session/verify`,
          {
            sessionId: sessionId,
          }
        );

        console.log("Subscription response:", subscriptionData);

        // invalid session → stop and redirect
        if (!subscriptionData?.sessionValid) {
          console.log("!subscriptionData?.isSessionValid...", !subscriptionData?.sessionValid);
          stopPolling();
          navigate("/subscription/cancel");
          return;
        }

        // payment success but subscription not yet active (waiting webhook)
        if (
          subscriptionData?.paymentCheckedOut &&
          !subscriptionData?.subscriptionSuccess
        ) {
          console.log("Waiting for subscription activation...");
          setStatus("activating");
          if (attempts >= MAX_ATTEMPTS) {
            setError(
              "Subscription activation is taking longer than expected. Please check after a short time."
            );
            setStatus("error");
            stopPolling();
            setLoading(false);
          }

          return; // keep polling
        }

        // subscription active → success
        if (subscriptionData?.subscriptionSuccess) {
          console.log("Subscription activated successfully!");
          stopPolling();
          setSubscriptionData(subscriptionData);
          setStatus("success");
          setLoading(false);
          refreshUser();
          return;
        }

        if (attempts >= MAX_ATTEMPTS) {
          setError("Subscription activation timed out. Please check again later.");
          setStatus("error");
          stopPolling();
          setLoading(false);
          return;
        }


      } catch (err) {
        console.error("Error verifying subscription:", err);
        setError("Failed to retrieve subscription details. Please try again later.");
        setStatus("error");
        stopPolling();
        setLoading(false);
      }
    };

    // call immediately
    getSubscriptionDetails();
    // poll every 5 seconds
    pollingRef.current = setInterval(getSubscriptionDetails, 5000);

    const stopPolling = () => {
      if (pollingRef.current) {
        clearInterval(pollingRef.current);
        pollingRef.current = null;
      }
    };

    // cleanup
    return () => stopPolling();
  }, [navigate, refreshUser]);


  if (status === "activating") {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center bg-gradient-to-br from-emerald-50 to-blue-50 dark:from-gray-900 dark:to-gray-800">
        <FaSpinner className="animate-spin text-emerald-600 dark:text-emerald-400 w-12 h-12 mb-6" />
        <h2 className="text-2xl font-semibold text-gray-900 dark:text-white">
          Activating your subscription…
        </h2>
        <p className="text-gray-600 dark:text-gray-400 mt-2">
          This may take a few seconds while we confirm your payment.
        </p>
      </div>
    );
  }

  if (status === "error") {
    return (
      <div className="min-h-screen flex items-center justify-center px-6 bg-gradient-to-br from-rose-50 to-red-50 dark:from-gray-900 dark:to-gray-800 transition-colors duration-300">
        <div className="max-w-lg w-full bg-white dark:bg-gray-900 rounded-3xl shadow-2xl p-8 text-center">
          <div className="flex justify-center mb-4">
            <div className="bg-rose-100 dark:bg-rose-900 rounded-full p-4">
              <FaExclamationCircle className="text-rose-600 dark:text-rose-400 w-14 h-14" />
            </div>
          </div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white mb-2">
            Oops!
          </h1>
          <p className="text-gray-600 dark:text-gray-400 mb-6">{error}</p>
          <button
            onClick={() => navigate("/")}
            className="w-full bg-rose-600 text-white py-3 rounded-xl font-semibold hover:bg-rose-700 transition-colors flex items-center justify-center dark:hover:bg-rose-500"
          >
            Go to Homepage
            <FaArrowRight className="w-5 h-5 ml-2" />
          </button>
        </div>
      </div>
    );
  }

  if (loading) return <LoadingSpinner />;

  return (
    <div className="min-h-screen flex items-center justify-center px-6 bg-gradient-to-br from-emerald-50 to-blue-50 dark:from-gray-900 dark:to-gray-800 transition-colors duration-300">
      <div className="max-w-lg w-full bg-white dark:bg-gray-900 rounded-3xl shadow-2xl p-8 text-center transition-colors duration-300">
        <div className="flex justify-center mb-4">
          <div className="bg-emerald-100 dark:bg-emerald-900 rounded-full p-4">
            <FaCheckCircle className="text-emerald-600 dark:text-emerald-400 w-14 h-14" />
          </div>
        </div>

        <h1 className="text-3xl font-bold text-gray-900 dark:text-white mb-2">
          Payment Successful!
        </h1>
        <p className="text-gray-600 dark:text-gray-400 text-lg mb-8">
          Welcome to{" "}
          <span className="font-semibold text-gray-900 dark:text-gray-100">
            Premium Plan
          </span>
          .
        </p>

        <div className="bg-gray-50 dark:bg-gray-800 rounded-2xl p-6 text-left mb-8 transition-colors duration-300">
          <div className="space-y-4">
            <div className="flex items-start">
              <FaCreditCard className="text-gray-400 mr-3 mt-0.5" />
              <div>
                <p className="text-sm text-gray-500 dark:text-gray-400">Amount</p>
                <p className="text-base font-medium text-gray-900 dark:text-gray-100">
                  ${subscriptionData?.amount} / monthly
                </p>
              </div>
            </div>

            <div className="flex items-start">
              <FaCalendarAlt className="text-gray-400 mr-3 mt-0.5" />
              <div>
                <p className="text-sm text-gray-500 dark:text-gray-400">
                  Subscription Date
                </p>
                <p className="text-base font-medium text-gray-900 dark:text-gray-100">
                  {formatDate(subscriptionData?.subscriptionStart)}
                </p>
              </div>
            </div>

            <div className="flex items-start">
              <FaCalendarAlt className="text-gray-400 mr-3 mt-0.5" />
              <div>
                <p className="text-sm text-gray-500 dark:text-gray-400">
                  Next Billing Date
                </p>
                <p className="text-base font-medium text-gray-900 dark:text-gray-100">
                  {formatDate(subscriptionData?.subscriptionEnd)}
                </p>
              </div>
            </div>

            <div className="flex items-start">
              <FaEnvelope className="text-gray-400 mr-3 mt-0.5" />
              <div>
                <p className="text-sm text-gray-500 dark:text-gray-400">
                  Confirmation Email
                </p>
                <p className="text-base font-medium text-gray-900 dark:text-gray-100">
                  {subscriptionData?.customerEmail}
                </p>
              </div>
            </div>
          </div>
        </div>

        <div className="bg-emerald-50 dark:bg-emerald-900/50 border border-emerald-200 dark:border-emerald-800 rounded-xl p-4 mb-8 text-sm text-emerald-800 dark:text-emerald-300">
          A confirmation email has been sent to{" "}
          <span className="font-medium">{subscriptionData?.customerEmail}</span>.
        </div>

        <button
          onClick={() => navigate("/")}
          className="w-full bg-emerald-600 text-white py-4 rounded-xl font-semibold hover:bg-emerald-700 transition-colors flex items-center justify-center dark:hover:bg-emerald-500"
        >
          Go to Dashboard
          <FaArrowRight className="w-5 h-5 ml-2" />
        </button>
      </div>
    </div>
  );
}
