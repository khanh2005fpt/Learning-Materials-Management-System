import React from "react";
import { Container, Navbar, Nav, Button } from "react-bootstrap";

export default function AdminDashboard({ onLogout }) {
    return (
        <>
            <Navbar bg="dark" variant="dark" expand="lg">
                <Container>
                    <Navbar.Brand>Admin Dashboard</Navbar.Brand>
                    <Nav className="ms-auto">
                        <Button variant="outline-light" onClick={onLogout}>
                            Logout
                        </Button>
                    </Nav>
                </Container>
            </Navbar>

            <Container style={{ marginTop: "50px" }}>
                <h2>Welcome, Admin!</h2>
                <p>This is the admin dashboard. You can manage users, settings, and content here.</p>

                <div className="admin-actions">
                    <Button variant="primary" className="me-2">
                        Manage Users
                    </Button>
                    <Button variant="success" className="me-2">
                        Manage Content
                    </Button>
                    <Button variant="warning">System Settings</Button>
                </div>
            </Container>
        </>
    );
}
