// StatsBar.jsx
import React, { useMemo } from "react";
import DataPulse from "./DataPulse";
import PercentagePulse from "./PercentagePulse";
import { formatPercentage, formatNumberCompact } from "../utils/format";

const StatsBar = ({ marketData, loading }) => {

  const stats = useMemo(() => [

    { key: "coins", label: "Coins", value: marketData?.totalCoins?.toLocaleString() ?? '-'},
    { key: "exchanges", label: "Exchanges", value: marketData?.totalExchanges?.toLocaleString() ?? '-' },
    { key: "marketCap", label: "Market Cap", value: formatNumberCompact(marketData?.totalMarketCap) ?? '-' , change: formatPercentage(marketData?.marketCapChange24h) ?? '-'},
    { key: "volume24h", label: "24h Vol", value: formatNumberCompact(marketData?.volume24h) ?? '-'},
    { key: "btcDominance", label: "BTC Dominance", value: formatPercentage(marketData?.btcDominance) ?? '-'},
    { key: "etcDominance", label: "ETH Dominance", value: formatPercentage(marketData?.ethDominance) ?? '-'}

  ], [marketData]);


  return (
    <div className="w-full bg-gray-100 dark:bg-gray-900 text-black dark:text-white p-3 mt-2 shadow-md">
      <div className="overflow-x-auto no-scrollbar">
        {loading ? (
          <div className="flex gap-3 items-center min-w-max md:min-win-0 animate-pulse">
            {Array(6).fill(0).map((_, idx) => (

              <div
                key={idx}
                className="flex-shrink-0 w-40 md:flex-1 flex flex-col gap-1 px1"
              >
                <div className="h-4 bg-gray-300 dark:bg-gray-700 rounded w-16 mb-2" />
                <div className="h-5 bg-gray-200 dark:bg-gray-800 rounded w-24" />
              </div>

            ))}
          </div>

        ) : (

          <div className="flex gap-3 items-center min-w-max md:min-w-0">
            {stats?.map((stat) => (
              <div
                key={stat.label}
                className="flex-shrink-0 w-40 md:flex-1 md:min-w-0 flex flex-col gap-1 px-1"
              >
                <span className="text-xs md:text-lg font-semibold text-gray-500 dark:text-gray-400">
                  {stat.label}
                </span>

                <div className="flex items-center gap-2 font-semibold">
                  <DataPulse value={stat.value}>{stat.value}</DataPulse>

                  {(stat?.change && stat?.change !== "-") && (
                    <PercentagePulse value={stat.change}>{stat.change}</PercentagePulse>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default React.memo(StatsBar);
