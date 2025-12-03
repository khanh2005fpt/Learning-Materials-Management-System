import { useState } from "react";
import { Form, Button, Alert } from "react-bootstrap";
import axios from "axios";

export default function Login() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const res = await axios.post(
                "/api/auth/login",
                { username, password },
                { withCredentials: true }
            );
            setMessage(res.data);
        } catch (err) {
            setMessage(err.response?.data || "Error");
        }
    };

    return (
        <Form onSubmit={handleSubmit} style={{ maxWidth: "500px", margin: "auto" }}>
            <h2>Login</h2>
            {message && <Alert variant="info">{message}</Alert>}
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
            <Button variant="primary" type="submit">Login</Button>
        </Form>
    );
}
