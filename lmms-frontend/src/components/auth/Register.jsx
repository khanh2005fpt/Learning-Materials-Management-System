import { useState } from "react";
import { Form, Button, Alert, Row, Col } from "react-bootstrap";
import axios from "axios";
import "./css/Register.css";

export default function Register() {
    const [fullname, setFullname] = useState("");
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [email, setEmail] = useState("");
    const [message, setMessage] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const res = await axios.post("/api/auth/register", {
                fullname, email, username, password
            });

            setMessage(res.data);
        } catch (err) {
            setMessage(err.response?.data || "Error");
        }
    };

    return (
      <div className="auth-page register-page">
        <div className="auth-card">
          <Form onSubmit={handleSubmit} className="register-form">
            <h2 className="auth-title">Đăng ký</h2>
            {message && <Alert variant="info">{message}</Alert>}
            <Form.Group className="mb-3">
                <Form.Label>Tên đầy đủ</Form.Label>
                <Form.Control
                    type="text"
                    value={fullname}
                    onChange={(e) => setFullname(e.target.value)}
                    required
                />
            </Form.Group>
            <Form.Group className="mb-3">
                <Form.Label>Email</Form.Label>
                <Form.Control
                    type="text"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                />
            </Form.Group>
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
            <Row className="d-flex justify-content-between">
                <Col>
                <Button className="submit-btn" variant="primary" type="submit">Đăng ký</Button>
                </Col>
                <Col>
                <Button className="submit-btn" variant="secondary" href="/">Quay lại trang chủ</Button>
                </Col>
            </Row>
          </Form>
        </div>
      </div>
    );
}
