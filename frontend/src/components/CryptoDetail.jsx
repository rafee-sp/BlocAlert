import React from "react";
import { formatDateWithText, formatNumberCompact, formatPercentage, formatPrice, formatSupply } from "../utils/format";
import PercentagePulse from "./PercentagePulse";
import PricePulse from "./PricePulse";

const CryptoDetail = React.memo(({ cryptoData }) => {

    if (!cryptoData) {
        return (
            <div className="p-6 dark:bg-gray-800 rounded-lg h-screen shadow-md flex items-center justify-center">
                <p className="text-gray-500 dark:text-gray-400">No data available</p>
            </div>
        );
    }

    const priceRangePercentage24h = ((cryptoData.current_price - cryptoData.low_24h) / (cryptoData.high_24h - cryptoData.low_24h)) * 100;

    return (
        <div className="p-6 dark:bg-gray-800 rounded-lg h-screen shadow-md flex flex-col gap-4">
            {/* coin name, price, rank */}
            <div className="flex items-center gap-4">
                <img
                    src={cryptoData.image}
                    alt={cryptoData.name}
                    className="w-14 h-14 rounded-full"
                />
                <div className="flex flex-col">
                    <div className="flex items-baseline gap-2">
                        <h2 className="text-2xl font-bold text-gray-900 dark:text-white">{cryptoData.name}</h2>
                        <span className="text-lg font-semibold uppercase text-gray-600 dark:text-gray-300">
                            {cryptoData.symbol}
                        </span>
                    </div>
                    <span className="mt-1 self-start text-sm font-medium text-gray-800 bg-gray-300 px-2 py-0.5 rounded-md inline-block">
                        #{cryptoData.market_cap_rank}
                    </span>
                </div>
            </div>
            {/* Price + Percentage */}
            <div className="flex items-baseline gap-3 font-semibold text-3xl text-gray-900 dark:text-gray-50">
                <PricePulse value={cryptoData.current_price}>
                    {formatPrice(cryptoData.current_price)}
                </PricePulse>
                <span className="text-lg font-semibold">
                    <PercentagePulse value={cryptoData.price_change_percentage_24h}>
                        {formatPercentage(cryptoData.price_change_percentage_24h)}
                    </PercentagePulse>
                </span>
            </div>
            {/*Market cap*/}
            <div className="w-full border border-gray-700 rounded-lg p-4 flex justify-center">
                <div className="flex flex-col items-center gap-2">
                    {/* Label */}
                    <span className="text-sm text-gray-800 dark:text-gray-300">Market Cap</span>

                    {/* Amount + Percentage */}
                    <div className="flex items-baseline gap-2">
                        <span className="text-lg font-bold text-gray-700 dark:text-white">
                            ${formatNumberCompact(cryptoData.market_cap)}
                        </span>
                        <PercentagePulse value={cryptoData.market_cap_change_percentage_24h}>
                            <span>
                                {formatPercentage(cryptoData.market_cap_change_percentage_24h)}
                            </span>
                        </PercentagePulse>
                    </div>
                </div>
            </div>
            <div className="grid grid-cols-2 gap-3">
                <div className="border border-gray-700 rounded-lg p-4 flex flex-col items-center gap-2">
                    <span className="text-sm text-gray-800 dark:text-gray-300">FDV</span>
                    <span className="text-lg font-bold text-gray-700 dark:text-white">${formatNumberCompact(cryptoData.fully_diluted_valuation)}</span>
                </div>
                <div className="border border-gray-700 rounded-lg p-4 flex flex-col items-center gap-2">
                    <span className="text-sm text-gray-800 dark:text-gray-300">Max Supply</span>
                    <span className="text-lg font-bold text-gray-700 dark:text-white">{formatSupply(cryptoData.max_supply, cryptoData.symbol)}</span>
                </div>
                <div className="border border-gray-700 rounded-lg p-4 flex flex-col items-center gap-2">
                    <span className="text-sm text-gray-800 dark:text-gray-300">Circulating Supply</span>
                    <div className="flex items-center gap-2 relative group">
                        <span className="text-lg font-bold text-gray-700 dark:text-white">
                            {formatSupply(cryptoData.circulating_supply, cryptoData.symbol)}
                        </span>
                    </div>
                </div>
                <div className="border border-gray-700 rounded-lg p-4 flex flex-col items-center gap-2">
                    <span className="text-sm text-gray-800 dark:text-gray-300">Total Supply</span>
                    <span className="text-lg font-bold text-gray-700 dark:text-white">{formatSupply(cryptoData.total_supply, cryptoData.symbol)}</span>
                </div>
            </div>
            {/*24 hr low/high */}
            <div className="w-full rounded-lg p-3 flex flex-col gap-2">
                <div className="flex justify-between text-md text-gray-300">
                    <div className="flex flex-col items-start">
                        <span className="text-gray-700 dark:text-gray-400">24h Low</span>
                        <span className="font-semibold text-gray-700 dark:text-gray-300">{formatPrice(cryptoData.low_24h)}</span>
                    </div>
                    <div className="flex flex-col items-end">
                        <span className="text-gray-700 dark:text-gray-400">24h High</span>
                        <span className="font-semibold text-gray-700 dark:text-gray-300">{formatPrice(cryptoData.high_24h)}</span>
                    </div>
                </div>
                <div className="relative w-full h-2 bg-gray-600 rounded-full">
                    <div className="h-2 bg-blue-500 rounded-full" style={{ width: `${priceRangePercentage24h}%` }}></div>
                    <div className="absolute top-1/2 -translate-y-1/2 -translate-x-1/2 w-1 h-4 bg-gray-800 dark:bg-white rounded"
                        style={{ left: `${priceRangePercentage24h}%` }}
                    ></div>
                </div>

            </div>
            {/*all time low/high */}
            <div className="w-full flex flex-col gap-3">
                <div className="flex justify-between items-center  rounded-lg p-4">
                    <div className="flex flex-col">
                        <span className="text-md font-semibold text-gray-800 dark:text-gray-400">All time high</span>
                        <span className="text-sm text-gray-700 dark:text-gray-300">{formatDateWithText(cryptoData.ath_date)}</span>
                    </div>
                    <span className="text-lg font-semibold text-gray-700 dark:text-white">{formatPrice(cryptoData.ath)}</span>
                </div>
                <div className="flex justify-between items-center rounded-lg p-4">
                    <div className="flex flex-col">
                        <span className="text-md font-semibold text-gray-700 dark:text-gray-400">All time low</span>
                        <span className="text-xs text-gray-700 dark:text-gray-300">{formatDateWithText(cryptoData.atl_date)}</span>
                    </div>
                    <span className="text-lg font-semibold text-gray-700 dark:text-white">{formatPrice(cryptoData.atl)}</span>
                </div>
            </div>

        </div>
    );
});

export default CryptoDetail;
