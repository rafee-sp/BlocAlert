import { useState, useEffect } from "react";
import { FaTimes, FaBell, FaEnvelope, FaSms } from "react-icons/fa";
import { useAuth } from "../context/AuthContext";
import { formatPrice } from "../utils/format";
import { toast } from "react-toastify";
import ErrorToast from "./ErrorToast";
import { Roles } from "../utils/roles";
import PricingModal from "./PricingModal";
import SuccessToast from "./SuccessToast";
import authApi from "../api/authApi";

const formTemplate = {
    cryptoId: "",
    condition: "PRICE_ABOVE",
    thresholdValue: "",
    notificationWebsocket: true,
    notificationEmail: false,
    notificationSms: false,
    cryptoName: "",
    cryptoSymbol: "",
    cryptoImage: "",
    currentPrice: ""

};

const AlertModal = ({ alert, cryptoData, onClose }) => {

    const { user } = useAuth();

    const [alertData, setAlertData] = useState(formTemplate);
    const [isPricingModalOpen, setPricingModalOpen] = useState(false);
    const [isUpdate, setIsUpdate] = useState(false);
    const [isLoading, setLoading] = useState(false);
    const [errors, setErrors] = useState({});


    useEffect(() => {

        const fetchPrice = async (cryptoId) => {

            let cryptoPrice;
            try {
                const { data: { data: { price } } } = await authApi.get("/cryptos/price", {
                    params: {
                        cryptoId: cryptoId
                    }
                })
                cryptoPrice = price;
            } catch (err) {
                console.log("Error fetching crypto price ", err)
                toast(<ErrorToast />)
                return null;
            }
            return cryptoPrice;
        }

        const loadAlertData = async () => {

            if (alert) {
                setIsUpdate(true);

                const currentPrice = (alert.cryptoId !== cryptoData.id) ? await fetchPrice(alert.cryptoId) : cryptoData.current_price;

                setAlertData({
                    cryptoId: alert.cryptoId || "",
                    cryptoName: alert.cryptoName || "",
                    cryptoSymbol: alert.cryptoSymbol || "",
                    cryptoImage: alert.cryptoImage || "",
                    currentPrice: currentPrice || "",
                    condition: alert.condition || "PRICE_ABOVE",
                    thresholdValue: alert.thresholdValue || "",
                    notificationWebsocket: alert.notificationWebsocket ?? true,
                    notificationEmail: alert.notificationEmail ?? false,
                    notificationSms: alert.notificationSms ?? false,
                });
            } else {
                setAlertData({
                    ...formTemplate,
                    cryptoId: cryptoData.id || "",
                    cryptoName: cryptoData.name || "",
                    cryptoSymbol: cryptoData.symbol || "",
                    cryptoImage: cryptoData.image || "",
                    currentPrice: cryptoData.current_price || ""
                });
            }
        }

        if (alert || cryptoData) {
            loadAlertData();
        }

    }, [alert, cryptoData]);

    const handleChange = (e) => {
        const { name, value } = e.target;

        if (name === "thresholdValue") {

            if (value === "") {
                setAlertData((prev) => ({ ...prev, [name]: value }));
                return;
            }

            // Only digits + optional decimal part
            const numericRegex = /^\d{1,12}(\.\d{0,8})?$/;
            if (!numericRegex.test(value) || value.length > 15) return;

            // Trim leading zeros 
            let formattedValue = value;
            if (formattedValue.startsWith("0") && !formattedValue.startsWith("0.")) {
                formattedValue = formattedValue.replace(/^0+/, "");
                if (formattedValue === "") formattedValue = "0";
            }

            setAlertData((prev) => ({
                ...prev,
                [name]: formattedValue,
            }));
        } else {
            setAlertData((prev) => ({
                ...prev,
                [name]: value,
            }));
        }

        setErrors((prev) => ({
            ...prev,
            [name]: "",
        }));
    };

    const haveRequiredAccess = () => {

        if ((alertData.notificationEmail || alertData.notificationSms) &&
            user?.role === Roles.FREE_USER
        ) {
            setPricingModalOpen(true);
            return false;
        }

        if (alertData.notificationSms && !user.phoneNumber) {
            toast(<ErrorToast message={"Please update phone number in settings before using SMS alerts"} />)
            return false;
        }

        return true;
    }

    const validate = () => {
        const newErrors = {};
        const { thresholdValue, condition, currentPrice } = alertData;
        const value = Number(thresholdValue);

        if (!thresholdValue || isNaN(value) || value <= 0) {
            newErrors.thresholdValue = "Please enter a valid target price greater than 0.";
        } else if (condition === "PRICE_ABOVE" && value <= currentPrice) {
            newErrors.thresholdValue = "Target price must be higher than the current price.";
        } else if (condition === "PRICE_BELOW" && value >= currentPrice) {
            newErrors.thresholdValue = "Target price must be lower than the current price.";
        }

        if (!condition) {
            newErrors.condition = "Please select a condition.";
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };


    const handleSubmit = async (e) => {

        e.preventDefault();

        if (!validate()) return;

        if (!haveRequiredAccess()) return;

        setLoading(true);
        const payload = {
            cryptoId: alertData.cryptoId,
            condition: alertData.condition,
            thresholdValue: parseFloat(alertData.thresholdValue),
            notificationWebsocket: alertData.notificationWebsocket,
            notificationEmail: alertData.notificationEmail,
            notificationSms: alertData.notificationSms,
        };

        console.log("Submitting payload:", payload);

        try {

            if (isUpdate) {

                const id = Number(alert.id);

                await authApi.put(`/alerts/${id}`, payload);
                toast(<SuccessToast message={"Alert updated successfully"} />)

            } else {

                await authApi.post('/alerts', payload);

                toast(<SuccessToast message={"Alert added successfully"} />)
            }
            onClose();
        } catch (err) {
            console.error("Error:", err.response.status);
            let message = "";

            if (err?.response.status === 429) {
                message = "Free plan limit reached upgrade or remove an existing alert to continue";
            } else if (err?.response.status === 409) {
                message = "An alert with the same condition already exists";
            } else {
                message = "Something went wrong...Please try again";
            }

            toast(<ErrorToast message={message} />)
        } finally {
            setLoading(false);
        }
    };

    return (
        <div
            className="fixed inset-0 backdrop-blur-xs bg-black/40 flex items-center justify-center p-4 z-50"
            onClick={onClose}
        >
            <div
                className="bg-gray-100 dark:bg-gray-700 rounded-lg p-6 w-full max-w-md mx-4"
                onClick={(e) => e.stopPropagation()}
            >
                <div className="flex items-center justify-between mb-6">
                    <h2 className="text-xl font-semibold text-gray-800 dark:text-white">
                        {isUpdate ? "Edit Price Alert" : "Create Price Alert"}
                    </h2>
                    <button
                        className="bg-red-500 rounded-full p-1.5 text-white hover:bg-red-600 hover: transition-colors"
                        onClick={onClose}
                    >
                        <FaTimes size={20} />
                    </button>
                </div>
                <div className="space-y-4">
                    <div className="flex items-center gap-2">
                        {
                            alertData.cryptoImage && (
                                <img src={alertData.cryptoImage}
                                    alt={alertData.cryptoName}
                                    className="w-8 h-8 rounded-full"
                                />
                            )
                        }

                        <div className="flex items-baseline gap-2">
                            <h2 className="text-lg font-semibold text-gray-900 dark:text-white">{alertData.cryptoName}</h2>
                            <span className="text-sm font-semibold uppercase text-gray-600 dark:text-gray-300">
                                {alertData.cryptoSymbol}
                            </span>
                        </div>
                    </div>
                    <div>
                        <label className="block text-sm text-gray-700 dark:text-gray-300 mb-2 font-medium">
                            Condition
                        </label>
                        <select
                            name="condition"
                            value={alertData.condition}
                            onChange={handleChange}
                            className="w-full bg-gray-200 dark:bg-gray-800 border border-gray-600 rounded-lg px-3 py-2 text-gray-700 dark:text-white focus:outline-none focus:border-blue-500"
                        >
                            <option value="PRICE_ABOVE">Price rises above</option>
                            <option value="PRICE_BELOW">Price drops below</option>
                            <option value="PRICE_EQUALS">Price equals</option>
                        </select>
                        {errors.condition && (
                            <p className="text-red-400 text-sm mt-1">
                                {errors.condition}
                            </p>
                        )}
                    </div>
                    <div>
                        <label className="block text-sm text-gray-700 dark:text-gray-300 mb-2 font-medium">
                            Target Price
                        </label>
                        <div className="relative">
                            <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500">$</span>
                            <input
                                type="text"
                                name="thresholdValue"
                                value={alertData.thresholdValue}
                                onChange={handleChange}
                                className={`w-full pl-7 bg-gray-200 dark:bg-gray-800 border rounded-lg px-3 py-2 text-gray-700 dark:text-white focus:outline-none focus:border-blue-500 ${errors.thresholdValue ? "border-red-600" : "border-gray-600"}`}
                                placeholder="Enter price in USD"
                            />
                        </div>
                        {errors.thresholdValue && (
                            <p className="text-red-400 text-sm mt-1">{errors.thresholdValue}</p>
                        )}
                        {alertData.currentPrice && (
                            <div className="mt-2 bg-gray-200 dark:bg-gray-800 px-3 py-2 rounded-lg text-sm text-gray-700 dark:text-gray-400 font-medium">
                                Current Price:{" "}
                                <span className="text-blue-600 dark:text-blue-400 font-semibold">
                                    {formatPrice(alertData.currentPrice)}
                                </span>
                            </div>
                        )}
                    </div>
                    <div>
                        <label className="block text-sm text-gray-700 dark:text-gray-300 mb-2 font-medium">
                            Alert channels
                        </label>
                        <div className="space-y-2">
                            <label className="flex items-center gap-2 cursor-not-allowed">
                                <input
                                    type="checkbox"
                                    checked={alertData.notificationWebsocket}
                                    disabled
                                    className="w-4 h-4 cursor-pointer"
                                />
                                <FaBell className="text-gray-700 dark:text-gray-400" />
                                <span className="text-gray-700 dark:text-white">Toast Message</span>
                            </label>
                            <label className="flex items-center gap-2 cursor-pointer">
                                <input
                                    type="checkbox"
                                    checked={alertData.notificationEmail}
                                    onChange={(e) => setAlertData({ ...alertData, notificationEmail: e.target.checked })}
                                    className="w-4 h-4 cursor-pointer"
                                />
                                <FaEnvelope className="text-gray-700 dark:text-gray-400" />
                                <span className="text-gray-700 dark:text-white">Email</span>
                            </label>
                            <label className="flex items-center gap-2 cursor-pointer">
                                <input
                                    type="checkbox"
                                    checked={alertData.notificationSms}
                                    onChange={(e) => setAlertData({ ...alertData, notificationSms: e.target.checked })}
                                    className="w-4 h-4 cursor-pointer"
                                />
                                <FaSms className="text-gray-700 dark:text-gray-400" />
                                <span className="text-gray-700 dark:text-white">SMS</span>
                            </label>
                        </div>
                    </div>
                </div>
                <div className="flex gap-3 mt-6">
                    <button
                        onClick={onClose}
                        disabled={isLoading}
                        className="flex-1 px-2 py-2 border text-white dark:border-gray-600 bg-red-500 hover:bg-red-600 transition-colors disabled:bg-red-300 disabled:cursor-not-allowed"
                    >
                        Cancel
                    </button>
                    <button
                        onClick={handleSubmit}
                        disabled={isLoading}
                        className="flex-1 px-2 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors disabled:bg-blue-300 disabled:cursor-not-allowed"
                    >
                        {isLoading
                            ? (isUpdate ? "Updating..." : "Adding...")
                            : (isUpdate ? "Update Alert" : "Add Alert")
                        }
                    </button>
                </div>
            </div>

            {
                isPricingModalOpen &&
                <PricingModal onClose={() => setPricingModalOpen(!isPricingModalOpen)} />
            }
        </div>
    );
};

export default AlertModal;
