import React, { useEffect, useState } from 'react';
import { Navbar, Nav, Container, Button, NavDropdown } from "react-bootstrap";

export default function NavbarSystem() {

    const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem("token"));
    const role = localStorage.getItem("role");
    useEffect(() => {
        const updateLoginState = () => {
            setIsLoggedIn(!!localStorage.getItem("token"));
        };

        // Nghe sự kiện token-changed
        window.addEventListener("token-changed", updateLoginState);

        return () => {
            window.removeEventListener("token-changed", updateLoginState);
        };
    }, []);

    const handleLogout = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("role");

        // gửi event để Navbar update
        window.dispatchEvent(new Event("token-changed"));

        window.location.href = "/";
    };

    return (
        <Navbar bg="dark" variant="dark" expand="lg" sticky="top">
            <Container>
                {/* Logo */}
                <Navbar.Brand href="/" className="fw-bold fs-4 text-info">
                    📚 Bảo Khánh
                </Navbar.Brand>

                <Navbar.Toggle />
                <Navbar.Collapse>
                    {/* Menu bên trái */}
                    <Nav className="me-auto" style={{marginLeft: "350px"}}>
                        <Nav.Link href="/">Trang chủ</Nav.Link>
                        <Nav.Link href="/about">Giới thiệu</Nav.Link>
                    </Nav>

                    {/* Khu vực bên phải */}
                    <Nav className="ms-auto align-items-center">
                        {!isLoggedIn ? (
                            <>
                                <Button href="/login" variant="outline-light" className="me-2">
                                    Đăng nhập
                                </Button>
                                <Button href="/register" variant="info">
                                    Đăng ký
                                </Button>
                            </>
                        ) : (
                            <NavDropdown
                                title={
                                    <span className="text-info fw-semibold">
                                        👤 {role || "USER"}
                                    </span>
                                }
                                align="end"
                            >
                                <NavDropdown.Item href="/profile">
                                    Thông tin cá nhân
                                </NavDropdown.Item>

                                {role === "ADMIN" && (
                                    <NavDropdown.Item href="/admin">
                                        Quản trị hệ thống
                                    </NavDropdown.Item>
                                )}

                                <NavDropdown.Divider />
                                <NavDropdown.Item onClick={handleLogout} className="text-danger">
                                    Đăng xuất
                                </NavDropdown.Item>
                            </NavDropdown>
                        )}
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}
