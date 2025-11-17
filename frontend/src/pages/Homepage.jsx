import { useEffect, useRef, useState } from "react";
import CryptoTable from "../components/CryptoTable";
import StatsBar from "../components/StatsBar";
import Pagination from "../components/Pagination";
import CryptoSearch from "../components/CryptoSearch";
import Layout from "./Layout";
import { captureWebSocketError } from "../utils/sentryUtils";

const HomePage = () => {

    const [marketData, setMarketData] = useState({});
    const [cryptoData, setCryptoData] = useState([]);
    const [pageable, setPageable] = useState({});
    const [loading, setLoading] = useState(false);
    const [isConnected, setIsConnected] = useState(false);
    const socketRef = useRef(null);
    const retryCountRef = useRef(0);
    const reconnectTimerRef = useRef(null);
    const DEFAULT_PAGE_NO = 1;
    const DEFAULT_PAGE_SIZE = 10; //change to dynamic
    const MAX_RETRY = 3;

    useEffect(() => {

        const connect = () => {

            if (socketRef.current && socketRef.current.readyState === WebSocket.OPEN) {
                console.warn("WebSocket already connected. Skipping reconnection.");
                return;
            }

            setLoading(true);

            const socket = new WebSocket(`${import.meta.env.VITE_WS_URL}/homepage`);

            socketRef.current = socket;

            socket.onopen = () => {
                retryCountRef.current = 0
                console.log("WebSocket connection established");
                subscribePage(DEFAULT_PAGE_NO, DEFAULT_PAGE_SIZE);
                subscribeMarketData();
                setIsConnected(true)
            }

            socket.onmessage = (event) => {

                let msg;
                try {
                    msg = JSON.parse(event.data);
                } catch (err) {
                    console.error("Invalid WebSocket message:", event.data);
                    captureWebSocketError(err, { eventType: "MESSAGE_PARSE", messageData: event.data }, socketRef.current, isConnected);
                    return;
                }

                switch (msg?.type) {
                    case "CRYPTO_DATA":
                        setCryptoData(msg.data.cryptoList);
                        setPageable(msg.data.pagination);
                        setLoading(false);
                        break;

                    case "MARKET_DATA":
                        setMarketData(msg.data);
                        break;
                    default:
                        console.warn("Unknown message:", msg);
                        captureWebSocketError(new Error("Unknown WebSocket message type"), {
                            eventType: "UNKNOWN_ALERT_MESSAGE",
                            messageData: msg,
                        }, socketRef.current, isConnected);

                }
            }

            socket.onclose = (event) => {
                console.log("WebSocket connection closed : ", event.reason || "No reason provided");
                setIsConnected(false);
                if (retryCountRef.current < MAX_RETRY) {

                    const delay = Math.min(5000 * (retryCountRef.current + 1), 10000);
                    reconnectTimerRef.current = setTimeout(connect, delay);
                    retryCountRef.current++;
                } else {
                    setLoading(false);
                    captureWebSocketError(new Error("Max reconnect attempts reached"), {
                        eventType: "WS_RECONNECT_FAIL",
                        retryCount: retryCountRef.current,
                    }, socketRef.current, isConnected);
                }
            }

            socket.onerror = (error) => {
                console.error("WebSocket error:", error);
                captureWebSocketError(error, { eventType: "WS_ERROR" }, socketRef.current, isConnected);
            }

        }

        const handleVisibilityChange = () => {  // save resource
            if (document.hidden && socketRef.current) {
                socketRef.current.close();
            } else if (!document.hidden && socketRef.current?.readyState !== WebSocket.OPEN) {
                connect();
            }
        };

        document.addEventListener("visibilitychange", handleVisibilityChange);

        connect()

        return () => {
            clearTimeout(reconnectTimerRef.current);
            if (socketRef.current?.readyState === WebSocket.OPEN) {
                socketRef.current.close();
                socketRef.current = null;
            }
            document.removeEventListener("visibilitychange", handleVisibilityChange);
        }

    }, [])

    const subscribePage = (page, size) => {

        if (socketRef.current?.readyState === WebSocket.OPEN) {
            socketRef.current.send(

                JSON.stringify({

                    type: "SUBSCRIBE_CRYPTO_PAGE",
                    page: page,
                    size: size

                })
            )
        }
    }

    const subscribeMarketData = () => {

        if (socketRef.current?.readyState === WebSocket.OPEN) {
            socketRef.current.send(

                JSON.stringify({
                    type: "SUBSCRIBE_MARKET_DATA",
                })
            )
        }
    }

    const handlePagination = (page = 1) => {
        subscribePage(page, DEFAULT_PAGE_SIZE)
    }

    return (
        <Layout>
            <div className="flex flex-col flex-1 min-h-0">

                <div className="flex-none md:pb-6">
                    <StatsBar marketData={marketData} loading={loading} />
                </div>

                <div className="flex-none mb-2">
                    <CryptoSearch />
                </div>

                <div className="flex-1 overflow-auto min-h-0">
                    <CryptoTable data={cryptoData} loading={loading} />
                </div>

                {pageable?.page && (
                    <div className="flex-none pb-4 md:pb-6">
                        <Pagination
                            onPageChange={(page) => handlePagination(page)}
                            page={pageable.page}
                            totalPages={pageable.totalPages}
                        />
                    </div>
                )}
            </div>
        </Layout>

    )

}

export default HomePage;