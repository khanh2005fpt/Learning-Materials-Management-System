import React, { useEffect, useState } from 'react';
import { Navbar, Nav, Container, Button } from "react-bootstrap";

export default function NavbarSystem() {

    const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem("token"));

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

        window.location.href = "/login";
    };

    return (
        <Navbar bg="dark" variant="dark" expand="lg" sticky="top">
            <Container>
                <Navbar.Brand href="/">Bảo Khánh</Navbar.Brand>

                <Nav className="ms-auto">
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
                        <Button variant="danger" onClick={handleLogout}>
                            Đăng xuất
                        </Button>
                    )}
                </Nav>
            </Container>
        </Navbar>
    );
}
