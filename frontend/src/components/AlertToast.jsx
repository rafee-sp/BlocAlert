import { FaArrowDown, FaArrowUp, FaTimes } from "react-icons/fa";
import { formatPrice } from "../utils/format";
import { PRICE_CONDITIONS } from "../utils/alertConditions";


export default function AlertToast({ alert, closeToast }) {

    if (!alert) return null;

    const isPriceBelow = alert.alertCondition === "PRICE_BELOW";
    const isPriceAbove = alert.alertCondition === "PRICE_ABOVE";

    return (
        <div className="relative flex items-start gap-3 p-4 rounded-2xl shadow-lg
                bg-white dark:bg-gray-700
                border border-gray-200 dark:border-gray-700
                min-w-[250px] max-w-xs">

            {closeToast && (
                <button
                    onClick={closeToast}
                    className="absolute top-2 right-2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-200 transition-colors"
                >
                    <FaTimes size={14} />
                </button>
            )}

            {alert.cryptoImage ? (
                <img
                    src={alert.cryptoImage}
                    alt={alert.cryptoName}
                    className="w-8 h-8 rounded-full flex-shrink-0"
                />
            ) : (
                <div className="w-8 h-8 rounded-full bg-gray-300 dark:bg-gray-700 flex-shrink-0" />
            )}

            <div className="flex-1 pr-4">
                <div className="flex justify-between items-center mb-2">
                    <span className="font-semibold text-gray-900 dark:text-white">
                        {alert.cryptoName}
                    </span>
                    {isPriceBelow && <FaArrowDown className="text-red-500 flex-shrink-0 ml-2" />}
                    {isPriceAbove && <FaArrowUp className="text-green-500 flex-shrink-0 ml-2" />}
                </div>

                <div className="text-sm text-gray-700 dark:text-gray-300 space-y-1">
                    <div>
                        Condition:{" "}
                        <span
                            className={`font-medium ${isPriceBelow
                                    ? "text-red-500"
                                    : isPriceAbove
                                        ? "text-green-500"
                                        : "text-blue-500"
                                }`}
                        >
                            {PRICE_CONDITIONS[alert?.alertCondition] || "Price alert triggered"}
                        </span>
                    </div>
                    <div>
                        Target: <span className="font-semibold">{formatPrice(alert.thresholdValue)}</span>
                    </div>
                    <div>
                        Current: <span className="font-semibold">{formatPrice(alert.currentPrice)}</span>
                    </div>
                </div>
            </div>
        </div>
    );
}