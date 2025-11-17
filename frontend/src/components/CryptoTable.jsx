import { formatPercentage, formatPrice, formatSupply } from "../utils/format";
import PricePulse from "./PricePulse";
import PercentagePulse from "./PercentagePulse";
import DataPulse from "./DataPulse";
import { useNavigate } from "react-router-dom";
import SkeletonRow from "./SkeletenRow";
import { useEffect, useRef } from "react";
import { useAuth } from "../context/AuthContext";
import useMediaQuery from "../hooks/useMediaQuery";

const CryptoTable = ({ data, loading }) => {

    const { isAuthenticated, login } = useAuth();
    const topRef = useRef(null);
    const navigate = useNavigate();
    const isMobile = useMediaQuery("(max-width: 768px)");

    useEffect(() => {
        if (!loading && data?.length > 0 && isMobile) {
            topRef.current?.scrollIntoView({ behavior: "smooth", block: "start" });
        }
    }, [data, loading, isMobile]);

    const handleCryptoSelect = (cryptoId) => {
        navigate(`/crypto/${cryptoId}`);
    }

    return (

        <div className="p-2 w-full" ref={topRef}>
            <div className="flex-1 min-h-0 overflow-auto hidden md:block">
                <div className="overflow-x-auto">
                    <table className="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
                        <thead className="bg-gray-200 dark:bg-gray-900 text-gray-700 dark:text-gray-200 uppercase text-lg">
                            <tr>
                                <th className="px-2 py-1 text-center w-[5%]">#</th>
                                <th className="px-2 py-1 text-center w-[10%]">Name</th>
                                <th className="px-2 py-1 text-center w-[15%]">Price</th>
                                <th className="px-2 py-1 text-center w-[15%]">24h Change %</th>
                                <th className="px-2 py-1 text-center w-[20%]">Market Cap</th>
                                <th className="px-2 py-1 text-center w-[20%]">Circulating Supply</th>
                            </tr>
                        </thead>
                        <tbody className="bg-white dark:bg-gray-800 text-md font-base divide-y divide-gray-200 dark:divide-gray-700 text-gray-800 dark:text-white">
                            {loading ?
                                Array(10).fill(0).map((_, idx) => <SkeletonRow key={idx} isMobile={false} />)
                                : data && data.length > 0 ? (data?.map((crypto) => (
                                    <tr key={crypto.id}
                                        className="hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors"
                                        title={isAuthenticated ? "View details" : "Login to view details"}
                                        {...(isAuthenticated ? { onClick: () => handleCryptoSelect(crypto.id) } : { onClick: () => login() })}
                                    >
                                        <td className="px-2 py-[6px] text-center">{crypto.market_cap_rank}</td>
                                        <td className="px-2 py-[6px] flex items-center gap-2 ">
                                            <img src={crypto.image} alt={crypto.name} className="w-6 h-6 rounded-full" />
                                            <div className="min-w-[80px] max-w-[120px]">
                                                <div className="font-semibold truncate" title={crypto.name}>{crypto.name}</div>
                                                <div className="text-gray-500 dark:text-gray-400 text-xs uppercase">{crypto.symbol}</div>
                                            </div>
                                        </td>

                                        <td className="px-2 py-[6px] font-semibold text-center">
                                            <PricePulse value={crypto.current_price}>
                                                {formatPrice(crypto.current_price)}
                                            </PricePulse>
                                        </td>

                                        <td className="px-2 py-[6px] text-center">
                                            <PercentagePulse value={crypto.price_change_percentage_24h} children={formatPercentage(crypto.price_change_percentage_24h)} />
                                        </td>
                                        <td className="px-2 py-[6px] text-center">
                                            <DataPulse value={crypto.market_cap}>${crypto.market_cap.toLocaleString()}</DataPulse>
                                        </td>
                                        <td className="px-2 py-[6px] text-center">
                                            <DataPulse value={crypto.circulating_supply}>{formatSupply(crypto.circulating_supply, crypto.symbol)}</DataPulse>
                                        </td>
                                    </tr>
                                ))
                                ) : (
                                    <tr>
                                        <td colSpan={6} className="text-center py-6 text-gray-500 dark:text-gray-400">
                                            No data available.
                                        </td>
                                    </tr>
                                )}
                        </tbody>
                    </table>
                </div>
            </div>

            <div className="md:hidden flex flex-col gap-4">
                {loading ?
                    Array(10).fill(0).map((_, idx) => <SkeletonRow key={idx} isMobile={true} />)
                    : data && data.length > 0 ?
                        (data?.map((crypto) => (
                            <div key={crypto.id}
                                onClick={() => isAuthenticated ? handleCryptoSelect(crypto.id) : login()}
                                className="bg-white dark:bg-gray-800 p-4 rounded-lg shadow transition active:scale-95">
                                <div className="flex items-center gap-3 mb-3">
                                    <span className="font-semibold text-gray-700 dark:text-gray-200">{crypto.market_cap_rank}.</span>
                                    <img src={crypto.image} alt={crypto.name} className="w-8 h-8 rounded-full" />
                                    <div className="flex flex-col">
                                        <span className="font-bold text-gray-900 dark:text-white">{crypto.name}</span>
                                        <span className="text-xs text-gray-500 dark:text-gray-400 uppercase">{crypto.symbol}</span>
                                    </div>
                                </div>
                                <div className="flex flex-col gap-1 text-sm">
                                    <div className="flex justify-between"><span className="text-gray-500 dark:text-gray-300">Price:</span> <PricePulse value={crypto.current_price}>{formatPrice(crypto.current_price)}</PricePulse></div>
                                    <div className="flex justify-between">
                                        <span className="text-gray-500 dark:text-gray-300">24h:</span>
                                        <PercentagePulse value={crypto.price_change_percentage_24h} className={crypto.price_change_percentage_24h >= 0 ? 'text-green-500' : 'text-red-500'}>
                                            {formatPercentage(crypto.price_change_percentage_24h)}
                                        </PercentagePulse>
                                    </div>
                                    <div className="flex justify-between"><span className="text-gray-500 dark:text-gray-300">Market Cap:</span> <DataPulse value={crypto.market_cap}>${crypto.market_cap.toLocaleString()}</DataPulse></div>
                                    <div className="flex justify-between"><span className="text-gray-500 dark:text-gray-300">Circulating Supply:</span> <DataPulse value={crypto.circulating_supply}>{formatSupply(crypto.circulating_supply, crypto.symbol)}</DataPulse></div>
                                </div>
                            </div>
                        ))
                        ) : (
                            <div className="text-center text-gray-500 dark:text-gray-400 py-6">
                                No data available.
                            </div>
                        )}
            </div>
        </div>
    );
};

export default CryptoTable;
