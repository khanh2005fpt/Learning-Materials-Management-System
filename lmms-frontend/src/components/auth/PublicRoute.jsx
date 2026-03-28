import { Navigate } from "react-router-dom";

export default function PublicRoute({ children }) {
    const role = localStorage.getItem("role");
    const token = localStorage.getItem("token");

    if(role === "ADMIN" && token) {
        return <Navigate to="/admin" replace/>;
    }
    else if(role === "USER" && token) {
        return <Navigate to="/user" replace/>;
    }


    return children;
}
 