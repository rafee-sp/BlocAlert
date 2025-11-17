import { FaRegCircleXmark } from "react-icons/fa6";
import { BsLightningFill } from "react-icons/bs";
import { FaCheck } from "react-icons/fa";
import api from "../api/api";
import { useAuth0 } from "@auth0/auth0-react";
import { useState } from "react";
import { FaSpinner } from "react-icons/fa";

const PricingModal = ({ onClose }) => {

  const { getAccessTokenSilently } = useAuth0();

  const [isLoading, setLoading] = useState(false);

  const handleUpgradePlan = async () => {
    console.log("handleUpgradePlan called");

    setLoading(true);

    const token = await getAccessTokenSilently({
      audience: "https://globalert-api.com",
    });

    console.log("Access token for subscription:", token);

    const { data: { data: url } } = await api.post("/subscription/subscribe", {}, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log("Stripe response:", url);
    //   const url = response.data;
    window.location.href = url;

    setLoading(false);
    // https://checkout.stripe.com/c/pay/cs_test_a1drqw3qzPihgp3Jfaw3zZkq231PPXwMy0M3HNhfwr9XRKsDopONldJ8LR#fidnandhYHdWcXxpYCc%2FJ2FgY2RwaXEnKSdkdWxOYHwnPyd1blpxYHZxWjA0Vk10bFZGQDVgdX9fcUNOdURySEpDbFIwZ1VMNGR2SHRGM11paX9MfzZqQnVWczFnNjB8f04yTXJEf2RDV29CSXE8ajBUdn1jZnVIYGNnbnJVM11yQXw8NTVWN2x3dWxnVCcpJ2N3amhWYHdzYHcnP3F3cGApJ2dkZm5id2pwa2FGamlqdyc%2FJyZjY2NjY2MnKSdpZHxqcHFRfHVgJz8ndmxrYmlgWmxxYGgnKSdga2RnaWBVaWRmYG1qaWFgd3YnP3F3cGB4JSUl
  }

  return (
    <div className="fixed inset-0 backdrop-blur-xs bg-black/40 flex items-center justify-center p-4 z-50">
      <div className="bg-white dark:bg-gray-900 rounded-2xl shadow-2xl max-w-4xl w-full overflow-y-auto"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="relative p-6 border-b border-gray-200 dark:border-gray-700">
          <button
            className="absolute top-6 right-6 text-red-400 hover:text-red-500 dark:text-red-500 dark:hover:text-red-400 transition-colors text-2xl leading-none"
            onClick={onClose}
          >
            <FaRegCircleXmark className="h-6 w-6" />
          </button>
          <h2 className="text-3xl font-bold text-gray-900 dark:text-white text-center">
            Choose Your plan
          </h2>
          <p className="text-gray-600 dark:text-gray-300 text-center mt-2">
            Upgrade to unlock unlimited alerts and notifications
          </p>
        </div>

        {/* Plans */}
        <div className="p-8 flex flex-col md:flex-row gap-6 justify-center">
          {/* Free Plan */}
          <div className="border-2 border-gray-200 dark:border-gray-700 rounded-xl p-6 bg-white dark:bg-gray-800 flex-1 max-w-sm">
            <div className="text-center mb-6">
              <h3 className="text-2xl font-bold text-gray-900 dark:text-white mb-2">Free</h3>
              <div className="mb-4">
                <span className="text-4xl font-bold text-gray-900 dark:text-white">$0</span>
                <span className="text-gray-600 dark:text-gray-300">/month</span>
              </div>
              <button
                disabled
                className="w-full py-3 px-6 bg-gray-100 dark:bg-gray-700 text-gray-500 rounded-lg font-semibold cursor-not-allowed"
              >
                Current Plan
              </button>
            </div>
            <div className="space-y-3">
              <div className="flex items-center justify-between">
                <span className="text-gray-700 dark:text-gray-300">Alerts</span>
                <span className="text-sm font-medium text-gray-900 dark:text-white">Up to 5</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-gray-400 dark:text-gray-500">SMS alerts</span>
                <span className="text-red-400 text-xl"><FaRegCircleXmark className="h-6 w-6" /></span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-gray-400 dark:text-gray-500">Email alerts</span>
                <span className="text-red-400 text-xl"><FaRegCircleXmark className="h-6 w-6" /></span>
              </div>
            </div>
          </div>

          {/* Pro Plan */}
          <div className="border-2 border-blue-500 rounded-xl p-6 bg-gradient-to-br from-blue-50 to-white dark:from-gray-800 dark:to-gray-900 shadow-lg relative flex-1 max-w-sm">
            <div className="absolute -top-4 left-1/2 transform -translate-x-1/2">
              <span className="bg-blue-500 text-white dark:bg-blue-600 px-4 py-1 rounded-full text-sm font-semibold flex items-center">
                <BsLightningFill className="h-4 w-4 mr-1" />
                Most Popular
              </span>
            </div>
            <div className="text-center mb-6">
              <h3 className="text-2xl font-bold text-gray-900 dark:text-white mb-2">Pro</h3>
              <div className="mb-4">
                <span className="text-4xl font-bold text-blue-600 dark:text-blue-400">$9.99</span>
                <span className="text-gray-600 dark:text-gray-300">/month</span>
              </div>
              <button
                onClick={handleUpgradePlan}
                disabled={isLoading}
                className={`w-full py-3 px-6 rounded-lg font-semibold shadow-md 
        flex items-center justify-center gap-2 transition-all duration-200
        ${isLoading
                    ? "bg-blue-400 dark:bg-blue-600 cursor-not-allowed"
                    : "bg-blue-600 dark:bg-blue-500 hover:bg-blue-700 dark:hover:bg-blue-600"
                  }
        text-white`}
              >
                {isLoading ? (
                  <>
                    <FaSpinner className="h-5 w-5 animate-spin text-white" />
                    <span className="text-sm font-medium tracking-wide">Processing...</span>
                  </>
                ) : (
                  <>
                    <span className="text-base font-semibold tracking-wide">Upgrade Now</span>
                  </>
                )}
              </button>
            </div>
            <div className="space-y-3">
              <div className="flex items-center justify-between">
                <span className="text-gray-900 dark:text-white font-medium">Alerts</span>
                <span className="text-sm font-semibold text-blue-600 dark:text-blue-400">Unlimited</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-gray-900 dark:text-white font-medium">SMS alerts</span>
                <span className="text-blue-600 dark:text-blue-400 text-xl"><FaCheck className="h-4 w-4" /></span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-gray-900 dark:text-white font-medium">Email alerts</span>
                <span className="text-blue-600 dark:text-blue-400 text-xl"><FaCheck className="h-4 w-4" /></span>
              </div>
            </div>
          </div>
        </div>

        {/* Footer */}
        <div className="px-8 pb-6 text-center text-sm text-gray-500 dark:text-gray-400">
          Cancel anytime. No hidden fees
        </div>
      </div>
    </div>
  )
}

export default PricingModal;
