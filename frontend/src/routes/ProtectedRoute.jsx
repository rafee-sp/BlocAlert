import { useAuth0 } from "@auth0/auth0-react";
import { Navigate } from "react-router-dom";
import LoadingSpinner from "../components/LoadingSpinner";

const ProtectedRoute = ({children}) => {

    const {isAuthenticated, isLoading} = useAuth0()

    if(isLoading){
        return <LoadingSpinner />
    }

    console.log("isAuthenticated in ProtectedRoute: ", isAuthenticated);
    if(!isAuthenticated){
        return <Navigate to="/" replace />
    }

    return children;
}

export default ProtectedRoute;