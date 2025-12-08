import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Form, Button, Alert, Container } from "react-bootstrap";
import axios from "axios";

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
        <Container style={{ maxWidth: "500px", marginTop: "50px" }}>
            <Form onSubmit={handleSubmit}>
                <h2 className="text-center mb-4">Login</h2>

                {message && <Alert variant="danger">{message}</Alert>}

                <Form.Group className="mb-3">
                    <Form.Label>Username</Form.Label>
                    <Form.Control
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>Password</Form.Label>
                    <Form.Control
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </Form.Group>

                <Button variant="primary" type="submit" className="w-100">
                    Login
                </Button>
            </Form>
        </Container>
    );
}
