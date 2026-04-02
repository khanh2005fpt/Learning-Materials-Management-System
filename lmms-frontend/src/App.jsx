import React from 'react';
import { Routes, Route, useLocation } from 'react-router-dom';
import Register from './components/auth/Register';
import Login from './components/auth/Login';
import AdminDashboard from './components/admin/AdminDashboard';
import PrivateRoute from './components/auth/PrivateRoute';
import NavbarSystem from './components/common/NavbarSystem';
import HomePage from './components/user/HomePage';
import Footer from './components/common/Footer';
import PublicRoute from './components/auth/PublicRoute';
import About from './components/user/About';

function App() {
  const location = useLocation();

  // Ẩn navbar ở login & register
  const hideNavbar = location.pathname === "/login" || location.pathname === "/register";

  return (
    <>
      {/* Chỉ render navbar khi không ở login/register */}
      {!hideNavbar && <NavbarSystem />}

      <Routes>
        <Route path="/register" element={
          <PublicRoute>
            <Register />
          </PublicRoute>
        } />
        <Route path="/login" element={
          <PublicRoute>
            <Login />
          </PublicRoute>
        } />

        <Route
          path="/admin"
          element={
            <PrivateRoute allowedRoles={["ADMIN"]}>
              <AdminDashboard />
            </PrivateRoute>
          }
        />
 
        <Route path="/" element={<HomePage />} />
        <Route path="/about" element={<About />} />
         <Route
          path="/user"
          element={
            <PrivateRoute allowedRoles={["USER"]}>
              {<HomePage />}
            </PrivateRoute>
          }
        />
      </Routes>

      {!hideNavbar && <Footer />}
    </>
  );
}

export default App;
