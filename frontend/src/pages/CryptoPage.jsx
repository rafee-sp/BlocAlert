import { useEffect, useRef, useState, useCallback } from "react";
import CryptoDetail from "../components/CryptoDetail";
import { useParams } from "react-router-dom";
import CryptoChart from "../components/CryptoChart";
import Layout from "./Layout";
import { useAuth } from "../context/AuthContext";
import { captureWebSocketError } from "../utils/sentryUtils";
import LoadingSpinner from "../components/LoadingSpinner";
import AlertSection from "../components/AlertSection"

const CryptoPage = () => {

    const { id } = useParams();
    const { isAuthenticated, getAccessToken } = useAuth();
    const [isConnected, setIsConnected] = useState(false)
    const [cryptoData, setCryptoData] = useState({});
    const [loading, setLoading] = useState(false);
    const socketRef = useRef(null);
    const retryCountRef = useRef(0);
    const reconnectTimerRef = useRef(null);
    const MAX_RETRY = 3;

    const authRequest = useCallback(async () => {

        if (socketRef.current?.readyState === WebSocket.OPEN) {

            try {
                const token = await getAccessToken();
                socketRef.current.send(
                    JSON.stringify({
                        type: "AUTH_REQUEST",
                        token: token
                    })

                )
            } catch (error) {
                console.error("Failed to get access token for WebSocket auth:", error);
                captureWebSocketError(error, { eventType: "AUTH_REQUEST" }, socketRef.current, isConnected);

            }
        }
    }, [getAccessToken]);

     const subscribeCoinUpdates = (cryptoId) => {

        if (socketRef.current?.readyState === WebSocket.OPEN) {

            socketRef.current.send(

                JSON.stringify({
                    type: "SUBSCRIBE_CRYPTO",
                    cryptoId: cryptoId
                })
            )
        }
    }

    useEffect(() => {

        if (!id || !isAuthenticated) {
            setIsConnected(false);
            if (socketRef.current) {
                socketRef.current.close();
                socketRef.current = null;
            }
            return;
        }

        const connect = () => {

            if (socketRef.current && socketRef.current.readyState === WebSocket.OPEN) {
                console.warn("WebSocket already connected. Skipping reconnection.");
                return;
            }


            setLoading(true);

            const socket = new WebSocket(`${import.meta.env.VITE_WS_URL}/crypto`);
            socketRef.current = socket;

            socket.onopen = () => {
                retryCountRef.current = 0
                console.log("WebSocket connection established");
                authRequest();
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
                    case "AUTH_SUCCESS":
                        console.log("Authentication successful");
                        subscribeCoinUpdates(id);
                        setIsConnected(true);
                        break;
                    case "CRYPTO_DATA":
                        setCryptoData(msg.cryptoData);
                        setLoading(false);
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
                if (event.reason === "Unauthorized") {
                    console.warn("Authentication failed — will not retry");
                    setLoading(false);
                    return;
                }

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


        connect();

        return () => {
            clearTimeout(reconnectTimerRef.current);
            if (socketRef.current?.readyState === WebSocket.OPEN) {
                socketRef.current.close();
                socketRef.current = null;
            }
            document.removeEventListener("visibilitychange", handleVisibilityChange);
        }
    }, [id, isAuthenticated, authRequest]);

    const [isAlertExpanded, setIsAlertExpanded] = useState(true);

    if (loading) return <LoadingSpinner />

    return (
        <Layout>
            <div className="flex flex-1 overflow-hidden">
                {/* Sidebar - Fixed width */}
                {cryptoData && Object.keys(cryptoData).length > 0 && (
                    <>
                        <div className="w-1/4 border-r-2 border-gray-700 overflow-y-auto [&::-webkit-scrollbar]:hidden [-ms-overflow-style:none] [scrollbar-width:none]">
                            <CryptoDetail cryptoData={cryptoData} />

                        </div>

                        {/* Main content - Takes remaining space */}
                        <div className="flex flex-col flex-1 overflow-hidden">
                            {/* Chart: fills available space */}
                            <div className="flex-1 overflow-hidden">
                                <CryptoChart id={id} />
                            </div>

                            {/* Alerts: fixed height when expanded, minimal when collapsed */}
                            <div
                                className={`transition-all duration-300 flex-shrink-0 ${isAlertExpanded ? "h-[48vh] min-h-[320px] max-h-[550px]" : "h-[60px]"
                                    }`}
                            >
                                <AlertSection
                                    isExpanded={isAlertExpanded}
                                    setIsExpanded={setIsAlertExpanded}
                                    cryptoData={cryptoData}
                                />
                            </div>
                        </div>
                    </>
                )}
            </div>
        </Layout>
    );
};

export default CryptoPage;