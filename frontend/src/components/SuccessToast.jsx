import { FaTimes } from "react-icons/fa";

const SuccessToast = ({ message, closeToast }) => {

    return (
        <div className="relative flex items-center gap-3 p-4 rounded-2xl shadow-lg bg-red-50 dark:bg-green-700 border border-green-200 dark:border-green-600 min-w-[250px] max-w-xs">
            {closeToast && (
                <button
                    onClick={closeToast}
                    className="absolute top-2 right-2 text-gray-400 hover:text-gray-600 dark:hover:text-gray-200 transition-colors"
                >
                    <FaTimes size={14} />
                </button>
            )}

            <div className="flex-1 text-green-700 dark:text-green-100 font-medium">
                {message || "Success"}
            </div>
        </div>

    )
}

export default SuccessToast;