import { Route, Routes } from "react-router-dom";
import { lazy } from "react"
import ProtectedRoute from "./ProtectedRoute";

const HomePage = lazy(() => import("../pages/Homepage"))
const CryptoPage = lazy(() => import("../pages/CryptoPage"))
const SubscriptionSuccess = lazy(() => import("../pages/SubscriptionSuccess"))
const SubscriptionCancel = lazy(() => import("../pages/SubscriptionCancel"))
const Settings = lazy(() => import("../pages/Settings"))
const Callback = lazy(() => import("../components/Callback"))

const AppRoutes = () => {

    return (
        <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path='/callback' element={<Callback />} />
            <Route path="/crypto/:id" element={
                <ProtectedRoute>
                    <CryptoPage />
                </ProtectedRoute>
            } />
            <Route path="/settings" element={
                <ProtectedRoute>
                    <Settings />
                </ProtectedRoute>
            } />
            <Route path="/subscription/success" element={
                <ProtectedRoute>
                    <SubscriptionSuccess />
                </ProtectedRoute>}
            />
            <Route path="/subscription/cancel" element={
                <ProtectedRoute>
                    <SubscriptionCancel />
                </ProtectedRoute>} />
            <Route path="*" element={<HomePage />} />
        </Routes>
    )
}

export default AppRoutes;