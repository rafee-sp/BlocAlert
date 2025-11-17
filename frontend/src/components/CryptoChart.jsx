import { useEffect, useMemo, useState } from "react";
import { Tooltip, XAxis, YAxis, ResponsiveContainer, AreaChart, Area } from "recharts";
import LoadingSpinner from "./LoadingSpinner";
import authApi from "../api/authApi";
import { toast } from "react-toastify";
import ErrorToast from "./ErrorToast";

const CryptoChart = ({ id }) => {
    const [chartData, setChartData] = useState([]);
    const [timeFrame, setTimeFrame] = useState("1D");
    const [loading, setLoading] = useState(false);

    const timeFrameOptions = ["1D", "7D", "1M", "6M", "1Y"];

    useEffect(() => {

        if (!timeFrame || !id) return;

        const fetchData = async () => {
            try {
                setLoading(true);
                const {data : {data}} = await authApi.get(`/cryptos/${id}/chart`, {
                    params: { timeframe: timeFrame },
                });

                const formattedData = convertToChartData(data, timeFrame);
                setChartData(formattedData);
            } catch (error) {
                console.error("Failed to fetch chart data:", error);
                setChartData([]); // fallback to empty array
                toast(<ErrorToast />)
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [timeFrame, id]);


    const { minPrice, maxPrice } = useMemo(() => {
        if (!chartData.length) return { minPrice: 0, maxPrice: 0 };

        const prices = chartData.map(d => d.price);
        let min = Math.min(...prices);
        let max = Math.max(...prices);
        const padding = (max - min) * 0.1;

        return {
            minPrice: min - padding,
            maxPrice: max + padding
        };
    }, [chartData]);


    if (loading) return <LoadingSpinner compact />;
    if (!chartData.length) return (
        <div className="text-gray-500 dark:text-gray-400 text-center py-10">
            No data available for this timeframe.
        </div>
    )

    return (
        <div className="w-full h-full flex flex-col p-4 bg-white dark:bg-gray-800">
            {/* Time frame buttons */}
            <div className="flex items-center justify-end w-full mb-4 flex-shrink-0 mt-2">
                <div className="inline-flex dark:border rounded-lg overflow-hidden shadow-sm">
                    {timeFrameOptions.map((option, idx) => (
                        <button
                            key={option}
                            onClick={() => setTimeFrame(option)}
                            className={`px-4 py-2 border border-gray-500 transition-colors duration-200 dark:font-medium text-sm
                                ${timeFrame === option
                                    ? "bg-blue-500 text-white"
                                    : "bg-white dark:bg-gray-600 text-gray-700 dark:text-gray-300 hover:bg-gray-100"
                                }
                                ${idx === 0 ? "rounded-l-lg" : ""} 
                                ${idx === timeFrameOptions.length - 1 ? "rounded-r-lg" : ""}`}
                        >
                            {option}
                        </button>
                    ))}
                </div>
            </div>

            {/* Chart container - takes remaining space */}
            <div className="flex-1 w-full min-h-0">
                <ResponsiveContainer width="100%" height="100%">
                    <AreaChart data={chartData} margin={{ top: 20, bottom: 20, left: 20, right: 40 }}>
                        <defs>
                            <linearGradient id="blueGradient" x1="0" y1="0" x2="0" y2="1">
                                <stop offset="0%" stopColor="#4f9aff" stopOpacity={0.8} />
                                <stop offset="100%" stopColor="#4f9aff" stopOpacity={0.2} />
                            </linearGradient>
                        </defs>

                        <XAxis
                            dataKey="timestamp"
                            type="number"
                            ticks={getXTicks(chartData, timeFrame)}
                            tickFormatter={(ts) => formatXAxis(ts, timeFrame)}
                        />

                        <YAxis
                            domain={[minPrice, maxPrice]}
                            orientation="right"
                            tickFormatter={(val) => `$${val.toLocaleString()}`}
                            tick={{ fontSize: 12 }}
                            axisLine={false}
                            tickLine={false}
                        />

                        <Tooltip
                            contentStyle={{ backgroundColor: "#1f2937", color: "#f3f4f6", borderRadius: "8px" }}
                            itemStyle={{ color: "#93c5fd" }}
                            labelFormatter={(ts) => {
                                const date = new Date(ts);
                                if (timeFrame === "1D") return date.toLocaleTimeString([], { hour: "numeric", minute: "2-digit" });
                                if (timeFrame === "7D") return date.toLocaleDateString([], { weekday: "short" });
                                if (["1M", "6M", "1Y"].includes(timeFrame)) return date.toLocaleString("default", { month: "short", year: "numeric" });
                                return date.toLocaleDateString();
                            }}
                            formatter={(value, name) => {
                                if (name === "price") return [`$${value.toLocaleString()}`, "Price"];
                                if (name === "volume") return [value.toLocaleString(), "Volume"];
                                if (name === "marketCap") return [value.toLocaleString(), "Market Cap"];
                                return [value, name];
                            }}
                        />

                        <Area
                            type="monotone"
                            dataKey="price"
                            stroke="#4f9aff"
                            fill="url(#blueGradient)"
                            strokeWidth={2}
                            dot={false}
                        />
                    </AreaChart>
                </ResponsiveContainer>
            </div>
        </div>
    );
};

export default CryptoChart;

const convertToChartData = (rawData, timeFrame) => {
    const { prices, market_caps, total_volumes } = rawData;
    const roundedMins = getRoundedTime(timeFrame);
    const dataMap = new Map();

    for (let i = 0; i < prices.length; i++) {
        const priceTS = prices[i][0];
        const price = prices[i][1];
        const marketCap = market_caps[i][1];
        const volume = total_volumes[i][1];

        const date = new Date(priceTS);
        let roundedTS;

        if (roundedMins >= 60) {
            date.setMinutes(0, 0, 0);
            if (roundedMins === 24 * 60) date.setHours(0, 0, 0, 0);
            roundedTS = date.getTime();
        } else {
            const mins = date.getMinutes();
            const roundedMin = Math.floor(mins / roundedMins) * roundedMins;
            date.setMinutes(roundedMin, 0, 0);
            roundedTS = date.getTime();
        }

        dataMap.set(roundedTS, { timestamp: roundedTS, price, marketCap, volume });
    }

    return Array.from(dataMap.values()).sort((a, b) => a.timestamp - b.timestamp);
};

const getRoundedTime = (timeFrame) => {
    if (timeFrame === "7D") return 30;
    if (timeFrame === "1M" || timeFrame === "6M") return 60;
    if (timeFrame === "1Y") return 24 * 60;
    return 5;
};

const getXTicks = (data, timeFrame) => {
    if (!data.length) return [];

    const startTime = data[0].timestamp;
    const endTime = data[data.length - 1].timestamp;
    const ticks = [];

    // Default interval
    let interval = 3 * 60 * 60 * 1000; // 3 hours

    // Set interval based on timeframe
    if (timeFrame === "1D") interval = 3 * 60 * 60 * 1000; // 3 hours
    else if (timeFrame === "7D") interval = 24 * 60 * 60 * 1000; // 1 day
    else if (timeFrame === "1M") interval = 3 * 24 * 60 * 60 * 1000; // 3 days
    else if (["6M", "1Y"].includes(timeFrame)) interval = 30 * 24 * 60 * 60 * 1000; // 30 days

    // If data is sparse, recalc interval to fit at least 5 ticks
    const totalDuration = endTime - startTime;
    const minTicks = 5;
    if (totalDuration / interval < minTicks) {
        interval = Math.max(Math.floor(totalDuration / (minTicks - 1)), 1);
    }

    for (let t = startTime; t <= endTime; t += interval) {
        ticks.push(t);
    }

    return ticks;
};


const formatXAxis = (ts, timeFrame) => {
    const date = new Date(ts);
    if (timeFrame === "1D") {
        let hours = date.getHours();
        const ampm = hours >= 12 ? "PM" : "AM";
        hours = ((hours + 11) % 12) + 1;
        return `${hours} ${ampm}`;
    } else if (["6M", "1Y"].includes(timeFrame)) {
        return date.toLocaleString("default", { month: "short", year: "numeric" });
    } else {
        return date.toLocaleDateString();
    }
};
