import { useEffect, useRef, useState } from "react";
import { FaSearch } from "react-icons/fa";
import { formatPercentage, formatPrice } from "../utils/format";
import PricePulse from "./PricePulse";
import PercentagePulse from "./PercentagePulse";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { useAuth } from "../context/AuthContext";
import ErrorToast from "./ErrorToast";
import api from "../api/api";


const CryptoSearch = () => {

    const { isAuthenticated, login } = useAuth();
    const [searchTerm, setSearchTerm] = useState("");
    const [showDropDown, setShowDropDown] = useState(false);
    const [cryptoData, setCryptoData] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(false);
    const searchRef = useRef(null)

    const navigate = useNavigate();

    useEffect(() => {

        const handleClickOutside = (event) => {

            if (searchRef.current && !searchRef.current.contains(event.target)) {
                setShowDropDown(false);
            }
        }

        document.addEventListener("mousedown", handleClickOutside);

        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        }
    }, [])

    const validate = () => {
        if (!searchTerm || searchTerm.trim() === "") {
            setError(true);
            return false;
        };
        return true;

    }

    const handleSearch = async () => {
        try {

            if (!validate()) return;
            setLoading(true);
            const { data : {data} } = await api.get("/cryptos/search", {
                params: {
                    searchTerm: searchTerm
                }
            })
            setCryptoData(data);
            setShowDropDown(true);
        } catch (err) {
            console.error("Error during search:", err);
            toast(<ErrorToast />)
        } finally {
            setLoading(false);
        }
    }

    const handleCryptoSelect = (cryptoId) => {
        navigate(`/crypto/${cryptoId}`);
    }

    const handleKeyDown = (e) => {
        if (e.key === 'Enter') {
            e.preventDefault();
            handleSearch();
        }
    };


    return (
        <div ref={searchRef} className="flex justify-end ms-2">
            <div className="mt-2 me-4 relative w-full md:w-2xl">
                <input
                    type="text"
                    placeholder="Search crypto..."
                    value={searchTerm}
                    onChange={(e) => { setSearchTerm(e.target.value); setError(false); }}
                    onKeyDown={handleKeyDown}
                    disabled={loading}
                    className={`w-full pr-10 pl-3 py-2 rounded-md border ${error ? "border-red-500 dark:border-red-500" : "border-gray-300 dark:border-gray-700"} bg-gray-200 dark:bg-gray-800 dark:text-white focus:outline-none focus:ring-2 ${error ? "focus:ring-red-500" : "focus:ring-blue-500"}`}
                />
                <button
                    type="button"
                    onClick={handleSearch}
                    className="absolute inset-y-0 right-0 flex items-center pr-3"
                >
                    <FaSearch
                        className="h-5 w-5 text-blue-500 dark:text-blue-500 hover:text-blue-600 transform transition-transform duration-200 hover:scale-125 cursor-pointer"
                    />

                </button>


                {showDropDown && (
                    <ul
                        className="absolute z-20 w-full bg-white dark:bg-gray-800 
       border border-gray-200 dark:border-gray-700 
       rounded-xl mt-2 max-h-96 overflow-auto shadow-2xl 
       backdrop-blur-md"
                    >
                        {loading ? (
                            Array(5).fill(0).map((_, idx) => (
                                <li
                                    key={idx}
                                    className="flex items-center justify-between gap-3 px-4 py-3 
            animate-pulse bg-gray-100 dark:bg-gray-700 rounded-lg mb-1"
                                >
                                    <div className="flex items-center gap-3">
                                        <div className="w-8 h-8 rounded-full bg-gray-300 dark:bg-gray-600" />
                                        <div className="flex flex-col gap-1">
                                            <div className="h-3 w-20 bg-gray-300 dark:bg-gray-600 rounded" />
                                            <div className="h-2 w-12 bg-gray-200 dark:bg-gray-500 rounded" />
                                        </div>
                                    </div>
                                    <div className="flex flex-col items-end gap-1">
                                        <div className="h-3 w-16 bg-gray-300 dark:bg-gray-600 rounded" />
                                        <div className="h-2 w-12 bg-gray-200 dark:bg-gray-500 rounded" />
                                    </div>
                                </li>
                            ))
                        ) : cryptoData.length > 0 ? (
                            cryptoData.map((crypto) => (
                                <li
                                    key={crypto.id}
                                    className="flex items-center justify-between gap-3 px-4 py-3 
            hover:bg-gray-100 dark:hover:bg-gray-700 
            cursor-pointer transition-colors duration-200"
                                    {...(isAuthenticated ? { onClick: () => handleCryptoSelect(crypto.id) } : { onClick: () => login() })}

                                >
                                    <div className="flex items-center gap-3">
                                        <img
                                            src={crypto.image}
                                            alt={crypto.name}
                                            className="w-8 h-8 rounded-full border border-gray-300 dark:border-gray-600"
                                        />
                                        <div className="flex flex-col">
                                            <span className="font-medium text-gray-800 dark:text-gray-100">
                                                {crypto.name}
                                            </span>
                                            <span className="text-xs text-gray-500 dark:text-gray-400 uppercase">
                                                {crypto.symbol}
                                            </span>
                                        </div>
                                    </div>
                                    <div className="flex flex-col items-end text-right min-w-[90px]">
                                        <PricePulse value={crypto.current_price}>
                                            <span className="font-semibold text-gray-900 dark:text-gray-50">
                                                ${formatPrice(crypto.current_price)}
                                            </span>
                                        </PricePulse>
                                        <PercentagePulse value={crypto.price_change_percentage_24h}>
                                            <span>{formatPercentage(crypto.price_change_percentage_24h)}</span>
                                        </PercentagePulse>
                                    </div>
                                </li>
                            ))
                        ) : (
                            <li className="px-4 py-6 text-center text-md md:text-lg text-gray-500 dark:text-gray-300">
                                No cryptos found
                            </li>
                        )}
                    </ul>
                )}


            </div>
        </div >
    )

}

export default CryptoSearch;