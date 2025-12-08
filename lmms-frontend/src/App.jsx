import React from 'react';
import { Routes, Route, useLocation } from 'react-router-dom';
import Register from './components/auth/Register';
import Login from './components/auth/Login';
import AdminDashboard from './components/admin/AdminDashboard';
import UserDashboard from './components/user/UserDashboard';
import PrivateRoute from './components/auth/PrivateRoute';
import NavbarSystem from './components/common/NavbarSystem';

function App() {
  const location = useLocation();

  // Ẩn navbar ở login & register
  const hideNavbar = location.pathname === "/login" || location.pathname === "/register";

  return (
    <>
      {/* Chỉ render navbar khi không ở login/register */}
      {!hideNavbar && <NavbarSystem />}

      <Routes>
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />

        <Route
          path="/admin"
          element={
            <PrivateRoute allowedRoles={["ADMIN"]}>
              <AdminDashboard />
            </PrivateRoute>
          }
        />

        <Route
          path="/user"
          element={
            <PrivateRoute allowedRoles={["USER", "ADMIN"]}>
              <UserDashboard />
            </PrivateRoute>
          }
        />
      </Routes>
    </>
  );
}

export default App;
