import { BrowserRouter, Route, Routes } from "react-router-dom"
import { ToastContainer } from "react-toastify"
import "react-toastify/dist/ReactToastify.css"
import { AuthProvider } from "./context/AuthProvider"
import { AlertWebsocketProvider } from "./context/AlertWebsocketProvider"
import { Suspense } from "react"
import ErrorBoundary from "./components/ErrorBoundary"
import AppRoutes from "./routes/AppRoutes"
import LoadingSpinner from "./components/LoadingSpinner"

/*
import HomePage from './Pages/Homepage'
import CryptoPage from "./pages/CryptoPage"
import ProtectedRoute from "./components/ProtectedRoute"
import Callback from "./components/Callback"
import SubscriptionSucccess from "./pages/SubscriptionSuccess"
import SubscriptionCancel from "./pages/SubscriptionCancel"
import Settings from "./pages/Settings"
*/


const App = () => {

  return (

    <ErrorBoundary>
      <Suspense fallback={<LoadingSpinner />}>
        <BrowserRouter>
          <AuthProvider>
            <AlertWebsocketProvider>
              <AppRoutes />
            </AlertWebsocketProvider>
          </AuthProvider>
        </BrowserRouter>
      </Suspense>
      <ToastContainer
        position="bottom-right"
        autoClose={5000}
        hideProgressBar={true}
        newestOnTop
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
      />
    </ErrorBoundary>
  )
}

export default App
