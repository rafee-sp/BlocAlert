import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useAuth } from "./AuthContext";
import { AlertWebsocketContext } from "./AlertWebsocketContext";
import { toast } from "react-toastify";
import AlertToast from "../components/AlertToast";
import { captureWebSocketError } from "../utils/sentryUtils";

export const AlertWebsocketProvider = ({ children }) => {

    const { isAuthenticated, getAccessToken } = useAuth();

    const [isConnected, setIsConnected] = useState(false);
    const socketRef = useRef(null);
    const retryCountRef = useRef(0);
    const reconnectTimerRef = useRef(null);
    const audioRef = useRef(null);
    const MAX_RETRY = 3;

    const toastOptions = useMemo(() => ({
        autoClose: 10000,
        hideProgressBar: true,
        closeOnClick: false,
        pauseOnHover: false,
        draggable: false,
        closeButton: false,
    }), []);

    const authRequest = useCallback(async () => {
        if (socketRef.current?.readyState === WebSocket.OPEN) {
            try {
                const token = await getAccessToken();
                socketRef.current.send(JSON.stringify({
                    type: "AUTH_REQUEST",
                    token: token
                }));

            } catch (error) {
                console.error("Failed to get access token for Alert websocket auth:", error);
                captureWebSocketError(error, { eventType: "AUTH_REQUEST" }, socketRef.current, isConnected);
            }
        }
    }, [getAccessToken]);


    useEffect(() => {
        audioRef.current = new Audio("/alert-notification.wav");
        audioRef.current.volume = 0.8;
    }, [])

    useEffect(() => {

        if (!isAuthenticated) {
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

            const socket = new WebSocket(`${import.meta.env.VITE_WS_URL}/alerts`);

            socketRef.current = socket;

            socket.onopen = () => {
                retryCountRef.current = 0
                console.log("Websocket connection established");
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
                        console.log("Authentication successfull");
                        setIsConnected(true);                        
                        break;
                    case "ALERTS":
                        try {
                            console.log("message ", msg);

                            if (audioRef.current) {
                                audioRef.current.currentTime = 0;
                                audioRef.current.play().catch(() => { });
                            }

                            if (Array.isArray(msg.alertData)) {
                                console.log("array method called");
                                msg.alertData.forEach((alert) => {
                                    toast(
                                        <AlertToast alert={alert} />, toastOptions);
                                });
                            } else {

                                console.log("object method called");

                                toast(
                                    <AlertToast alert={msg.alertData} />, toastOptions
                                );
                            }
                        } catch (err) {
                            captureWebSocketError(err, { eventType: "ALERTS_TOAST", messageData: msg, }, socketRef.current, isConnected);
                        }
                        break;
                    default:
                        console.warn("Unknown message : ", msg);
                        captureWebSocketError(new Error("Unknown WebSocket message type"), {
                            eventType: "UNKNOWN_ALERT_MESSAGE",
                            messageData: msg,
                        }, socketRef.current, isConnected);
                }
            }

            socket.onclose = (event) => {

                console.log("Websocket connection closed : ", event.reason || "Unknown reason");
                setIsConnected(false);
                if (event.reason === "Unauthorized") {
                    console.warn("Authentication failed - will not retry");
                    captureWebSocketError(new Error("Unauthorized WebSocket closure"), {
                        eventType: "WS_ALERT_CLOSE",
                        reason: event.reason,
                    }, socketRef.current, isConnected);
                    return;
                }

                if (retryCountRef.current < MAX_RETRY) {
                    const delay = Math.min(5000 * (retryCountRef.current + 1), 10000);
                    reconnectTimerRef.current = setTimeout(connect, delay);
                    retryCountRef.current++;
                } else {
                    captureWebSocketError(new Error("Max reconnect attempts reached"), {
                        eventType: "WS_RECONNECT_FAIL",
                        retryCount: retryCountRef.current,
                    }, socketRef.current, isConnected);
                }
            }

            socket.onerror = (error) => {
                console.error("Websocket error : ", error);
                captureWebSocketError(error, { eventType: "WS_ERROR" }, socketRef.current, isConnected);
            }
        }

        const handleVisibilityChange = () => {  // save resource
            if (document.hidden && socketRef.current) {
                socketRef.current.close();
            } else if (!document.hidden && !isConnected) {
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

    }, [isAuthenticated, getAccessToken, toastOptions]);

    return (
        <AlertWebsocketContext.Provider value={{ isConnected }}>
            {children}
        </AlertWebsocketContext.Provider>
    )
}