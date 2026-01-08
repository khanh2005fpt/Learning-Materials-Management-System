import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Form, Button, Alert, Container } from "react-bootstrap";
import axios from "axios";
import "./css/Login.css";

export default function Login() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState("");

    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const res = await axios.post("/api/auth/login", { username, password });

            const { token, role } = res.data;
            // console.log("Login response:", res.data);
            // console.log("Token:", token);
            // console.log("Role:", role);
            if (!token || !role) {
                setMessage("Login failed: Invalid credentials");
                return;
            }

            // Lưu token và role vào localStorage
            localStorage.setItem("token", token);
            localStorage.setItem("role", role);
            // Thông báo cho Navbar biết token đã thay đổi
            window.dispatchEvent(new Event("token-changed"));
            // Redirect dựa theo role

            if (role === "ADMIN") {
                navigate("/admin");
            } else if (role === "USER") {
                navigate("/user");
            } else {
                setMessage("Role không hợp lệ");
            }
        } catch (err) {
            setMessage(err.response?.data?.message || "Username hoặc password không đúng");
        }
    };

    return (
      <div className="auth-page login-page">
        <div className="auth-card">
          <Form onSubmit={handleSubmit} className="auth-form">
            <h2 className="auth-title text-center mb-4">Đăng nhập</h2>

            {message && <Alert variant="danger">{message}</Alert>}

            <Form.Group className="mb-3">
                <Form.Label>Tên đăng nhập</Form.Label>
                <Form.Control
                    type="text"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    required
                />
            </Form.Group>

            <Form.Group className="mb-3">
                <Form.Label>Mật khẩu</Form.Label>
                <Form.Control
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />
            </Form.Group>

            <Button className="submit-btn" variant="primary" type="submit">Đăng nhập</Button>
          </Form>
        </div>
      </div>
    );
}
