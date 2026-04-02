import React from "react";
import { Container } from "react-bootstrap";
import "./css/Footer.css";

export default function Footer() {
  return (
    <footer className="simple-footer">
      <Container className="text-center py-3">
        <p className="mb-1">
          © 2026 - Hệ thống quản lý tài liệu | Developed by Bảo Khánh 🚀
        </p>
        <p className="mb-0">
          Thư viện sách & tài liệu học tập trực tuyến
        </p>
      </Container>
    </footer>
  );
}
