import { useEffect, useState } from 'react';
import Layout from './Layout';
import { IoMailSharp } from "react-icons/io5";
import { FaAdjust, FaCheck, FaClock, FaCreditCard, FaEdit, FaKey, FaMoon, FaPhoneAlt, FaSave, FaSun, FaUser } from 'react-icons/fa';
import { HiCheckCircle } from 'react-icons/hi';
import ThemeToggle from '../components/ThemeToggle';
import { useAuth } from '../context/AuthContext';
import authApi from '../api/authApi';
import { toast } from 'react-toastify';
import ErrorToast from '../components/ErrorToast';
import { formatDateTime } from "../utils/format";
import SuccessToast from '../components/SuccessToast';
import PricingModal from "../components/PricingModal";
import LoadingSpinner from '../components/LoadingSpinner';

export default function Settings() {

    const { user } = useAuth();
    const [phoneNumber, setPhoneNumber] = useState('');
    const [subscription, setSubscription] = useState({})
    const [isEditing, setIsEditing] = useState(false);
    const [isLoading, setIsLoading] = useState(true);
    const [phoneError, setPhoneError] = useState(false);
    const [isPricingModalOpen, setPricingModalOpen] = useState(false);


    const getSubscriptionDetails = async () => {

        setIsLoading(true);

        try {
            const { data: { data } } = await authApi.get("/subscription/me")
            setSubscription(data)
        } catch (err) {
            console.log("Error fetching subscription details ", err)
            toast(<ErrorToast />)
        } finally {
            setIsLoading(false);
        }
    }

    useEffect(() => {

        if (!user) return;
        if (!phoneNumber) setPhoneNumber(user.phoneNumber);
        getSubscriptionDetails();
    }, [user])

    const handleSave = async () => {

        console.log("Updating phone number")
        try {

            if (!/^\d{10}$/.test(phoneNumber)) {
                setPhoneError(true);
                return;
            }

            const response = await authApi.patch("/users", {
                phoneNumber: phoneNumber
            });

            if (response.status === 200) {
                toast(<SuccessToast message={"Phone number updated successfully"} />)
            } else {
                throw new Error("Failed to update phone number");
            }

            setIsEditing(false);
        } catch (err) {
            console.log("Error while updating phone number ", err)
            toast(<ErrorToast />)
        }
    };

    const handleResetPassword = async () => {
        try {
            console.log("handleResetPassword called");
            const response = await authApi.post("/users/reset-password");

            if (response.status === 200) {
                toast(<SuccessToast message={"An email with the password reset link sent"} />)
            } else {
                throw new Error("Failed to send the reset password email");
            }

        } catch (err) {
            console.log("Error while sending reset password mail ", err)
            toast(<ErrorToast />)
        }
    }

    const handlePhoneNumberChange = (e) => {
        const value = e.target.value;
        if (/^[0-9]*$/.test(value) && value.length <= 10) {
            setPhoneNumber(value);
        }
        setPhoneError(false);
    };


    if (isLoading || !user) return <LoadingSpinner />

    return (
        <Layout>
            <div className="p-6">
                <div className="max-w-5xl mx-auto">
                    <h2 className="font-bold text-3xl text-center text-gray-800 dark:text-white mb-8">Settings</h2>

                    <div className="grid grid-cols-2 gap-4 bg-gray-200 dark:bg-gray-800 border border-gray-400 dark:border-gray-700 rounded-2xl p-6 mb-6 shadow-xl shadow-black/20">
                        <div className="flex items-center justify-between p-4 bg-gray-300 dark:bg-gray-900 rounded-xl border border-gray-400 dark:border-gray-700 hover:border-gray-700 dark:hover:border-gray-500 transition-all">
                            <div className="flex items-center gap-4">
                                <span className="text-blue-600 dark:text-blue-400 text-lg">
                                    <FaUser />
                                </span>
                                <div >
                                    <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">Username</p>
                                    <p className="text-gray-800 dark:text-white font-semibold">{user?.name}</p>
                                </div>
                            </div>
                        </div>
                        <div className="flex items-center justify-between p-4 bg-gray-300 dark:bg-gray-900 rounded-xl border border-gray-400 dark:border-gray-700 hover:border-gray-700 dark:hover:border-gray-500 transition-all">
                            <div className="flex items-center gap-4">
                                <span className="text-blue-600 dark:text-blue-400 text-lg">
                                    <IoMailSharp />
                                </span>
                                <div>
                                    <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">Email</p>
                                    <p className="text-gray-800 dark:text-white font-medium flex items-center gap-3">
                                        {user?.email}
                                        <span className="flex items-center gap-1 text-green-600 dark:text-green-200 text-xs bg-green-300 dark:bg-green-600 px-2 py-1 rounded-full">
                                            <HiCheckCircle /> Verified
                                        </span>
                                    </p>
                                </div>
                            </div>
                        </div>
                        <div className="flex items-center justify-between p-4 bg-gray-300 dark:bg-gray-900 rounded-xl border border-gray-400 dark:border-gray-700 hover:border-gray-700 dark:hover:border-gray-500 transition-all">
                            <div className="flex items-center gap-4 w-full">
                                <span className="text-blue-600 dark:text-blue-400 text-lg">
                                    <FaPhoneAlt />
                                </span>
                                <div className="flex-1">
                                    <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">Phone Number</p>

                                    {isEditing ? (
                                        <input
                                            type="text"
                                            value={phoneNumber}
                                            onChange={handlePhoneNumberChange}
                                            className={`bg-gray-200 dark:bg-gray-800 border text-gray-800 dark:text-white px-3 py-1 rounded-lg w-full focus:outline-none focus:ring-1  text-sm ${phoneError ? "dark:border-red-600 focus:ring-red-500" : "dark:border-gray-600 focus:ring-blue-500"}`}
                                        />
                                    ) : (
                                        <p className="text-gray-800 dark:text-white font-medium">{phoneNumber ?? "(xxx) xxx-xxxx"}</p>
                                    )}
                                </div>
                            </div>

                            {isEditing ? (
                                <button
                                    onClick={handleSave}
                                    className="ml-4 flex items-center gap-1 text-green-500 dark:text-green-400 hover:text-green-600 dark:hover:text-green-300 text-sm font-medium transition-colors"
                                >
                                    <FaSave title='save' className="text-xl inline-flex mt-4" />
                                </button>
                            ) : (
                                <button
                                    onClick={() => setIsEditing(true)}
                                    className="ml-4 flex items-center gap-1 text-blue-400 hover:text-blue-300 text-sm font-medium transition-colors"
                                >
                                    <FaEdit title='edit' className="text-lg text-blue-600 dark:text-blue-400" />
                                </button>
                            )}
                        </div>
                    </div>

                    <div className="bg-gray-200 dark:bg-gray-800 border border-gray-400 dark:border-gray-700 rounded-2xl p-4 mb-6 shadow-xl shadow-black/20">
                        <div className="flex items-center justify-between">
                            <div className="text-lg font-semibold text-gray-700 dark:text-white flex items-center gap-2">
                                <FaAdjust className="text-indigo-500 dark:text-indigo-400" />
                                <h3 className="text-lg font-semibold text-gray-800 dark:text-white mb-1">Theme Preference</h3>
                            </div>
                            <ThemeToggle />
                        </div>
                    </div>

                    <div className="bg-gray-200 dark:bg-gray-800 border border-gray-400 dark:border-gray-700 rounded-2xl p-4 mb-6 shadow-xl shadow-black/20">
                        <div className="flex items-center justify-between">
                            <div className="text-lg font-semibold text-gray-700 dark:text-white flex items-center gap-2">
                                <FaKey className="text-blue-500 dark:text-blue-400" />
                                <span>Forgot Password</span>
                            </div>
                            <button className="px-4 py-2 bg-red-600 hover:bg-red-700 text-white rounded-xl text-sm font-medium transition-colors"
                                onClick={handleResetPassword}
                            >
                                Reset
                            </button>
                        </div>
                    </div>
                    <SubscriptionCard subscription={subscription} setPricingModalOpen={setPricingModalOpen} getSubscriptionDetails={getSubscriptionDetails} />
                </div>
            </div>
            {
                isPricingModalOpen &&
                <PricingModal onClose={() => setPricingModalOpen(!isPricingModalOpen)} />
            }

        </Layout>
    );
}

const SubscriptionCard = ({ subscription, setPricingModalOpen, getSubscriptionDetails }) => {
    const isActive = subscription?.subscriptionStatus === "ACTIVE";
    const isCancelling = subscription?.subscriptionStatus === "CANCELLING";
    const isPremium = isActive || isCancelling;

    const handleCancelSubscription = async () => {

        try {
            console.log("handleCancelSubscription called");

            const response = await authApi.post("/subscription/cancel");
            if (response.status == 200) {
                toast(<SuccessToast message={"Your subscription will be cancelled at the end of your current billing period."} />)
                await getSubscriptionDetails();
            } else {
                throw new Error("Failed to cancel the subscription");
            }


        } catch (err) {
            console.log("Error while cancelling subscription ", err)
            toast(<ErrorToast />)
        }
    }


    const statusLabel = isActive
        ? "Active"
        : isCancelling
            ? "Cancelling"
            : "Inactive";

    const statusColor = isActive
        ? "bg-blue-300 dark:bg-blue-400 text-blue-800"
        : isCancelling
            ? "bg-yellow-300 dark:bg-yellow-400 text-yellow-800"
            : "bg-red-500 text-white";

    const renewalText =
        isActive || isCancelling
            ? `${isActive ? "Renews on" : "Cancels on"} ${subscription?.currentSubscriptionEnd
                ? formatDateTime(subscription.currentSubscriptionEnd)
                : ""
            }`
            : "";

    return (
        <div className="bg-gray-200 dark:bg-gray-800 border border-gray-400 dark:border-gray-700 rounded-2xl p-5 mb-6 shadow-xl shadow-black/20">
            {/* Header */}
            <div className="flex items-center gap-2 mb-4">
                <FaCreditCard className="text-blue-500 dark:text-blue-400 text-lg" />
                <h3 className="text-lg font-semibold text-gray-700 dark:text-white">
                    Subscription
                </h3>
            </div>

            <div className="bg-gradient-to-br from-blue-100 dark:from-blue-500/10 to-blue-200 dark:to-purple-500/10 border border-blue-400 dark:border-blue-500/30 rounded-xl p-5">
                <div className="flex items-center justify-between mb-4">
                    <div>
                        <p className="text-gray-900 dark:text-gray-400 text-sm mb-1">
                            Current Plan
                        </p>
                        <p className="text-2xl font-bold text-gray-700 dark:text-white">
                            {isPremium ? "PREMIUM" : "FREE"}
                        </p>
                    </div>

                    <span
                        className={`px-3 py-1 rounded-full text-xs font-medium ${statusColor}`}
                    >
                        {statusLabel}
                    </span>
                </div>

                {renewalText && (
                    <div className="flex items-center gap-2 text-slate-400 text-sm mb-4">
                        <FaClock className="text-gray-600 dark:text-gray-300" />
                        <span className="text-gray-600 dark:text-gray-300">{renewalText}</span>
                    </div>
                )}

                <div className="flex gap-3">
                    {!isPremium && (
                        <button
                            onClick={() => setPricingModalOpen(true)}
                            className="px-4 py-2 bg-blue-600 hover:bg-blue-700 dark:hover:bg-blue-500 text-white rounded-lg text-sm font-medium transition-colors"
                        >
                            Upgrade Plan
                        </button>
                    )}

                    {isActive && (
                        <button className="px-4 py-2 bg-red-600 hover:bg-red-700 dark:hover:bg-red-500 text-white rounded-lg text-sm font-medium transition-colors"
                            onClick={handleCancelSubscription}>
                            Cancel Subscription
                        </button>
                    )}
                </div>
            </div>
        </div>
    );
};