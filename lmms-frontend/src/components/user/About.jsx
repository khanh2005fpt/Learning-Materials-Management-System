import { Container, Row, Col, Card } from "react-bootstrap";
import "./css/About.css";

export default function About() {
    return (
        <div className="about-page">
            <Container>
                {/* HEADER */}
                <div className="about-header text-center py-5">
                    <h1 className="display-4 fw-bold mb-3">📚 Giới thiệu hệ thống</h1>
                    <p className="lead text-muted">
                        Hệ thống quản lý tài liệu thông minh, nhanh chóng và tiện lợi
                    </p>
                </div>

                {/* GIỚI THIỆU CHÍNH */}
                <Row className="mb-5 align-items-center">
                    <Col md={6}>
                        <h2 className="mb-4">🎯 Mục tiêu của chúng tôi</h2>
                        <p className="lead">
                            Website giúp quản lý tài liệu một cách hiệu quả, hỗ trợ người dùng
                            tìm kiếm, xem và tải tài liệu nhanh chóng.
                        </p>
                        <p>
                            Hệ thống được thiết kế phù hợp cho sinh viên, giảng viên, 
                            thư viện và các doanh nghiệp cần quản lý tài liệu số.
                        </p>

                        <h3 className="mt-5 mb-3">🚀 Tính năng nổi bật</h3>
                        <ul className="feature-list">
                            <li>📂 Upload và quản lý tài liệu PDF, Word, Excel...</li>
                            <li>🔍 Tìm kiếm thông minh theo tiêu đề, tác giả, thể loại</li>
                            <li>👁️ Xem tài liệu trực tiếp trên trình duyệt</li>
                            <li>📥 Tải xuống dễ dàng</li>
                            <li>🛠️ Quản trị hệ thống mạnh mẽ (Admin Panel)</li>
                            <li>🔐 Bảo mật và phân quyền rõ ràng</li>
                        </ul>
                    </Col>

    
                </Row>

                {/* FEATURES CARDS */}
                <Row className="mb-5">
                    <Col md={4} className="mb-4">
                        <Card className="about-card h-100 text-center">
                            <Card.Body>
                                <div className="feature-icon mb-3">⚡</div>
                                <h5>Nhanh chóng & Tối ưu</h5>
                                <p className="text-muted">
                                    Tốc độ tải và tìm kiếm tài liệu cực nhanh, mang lại trải nghiệm người dùng tốt nhất.
                                </p>
                            </Card.Body>
                        </Card>
                    </Col>

                    <Col md={4} className="mb-4">
                        <Card className="about-card h-100 text-center">
                            <Card.Body>
                                <div className="feature-icon mb-3">🔒</div>
                                <h5>Bảo mật cao</h5>
                                <p className="text-muted">
                                    Hệ thống xác thực, phân quyền và bảo vệ dữ liệu an toàn.
                                </p>
                            </Card.Body>
                        </Card>
                    </Col>

                    <Col md={4} className="mb-4">
                        <Card className="about-card h-100 text-center">
                            <Card.Body>
                                <div className="feature-icon mb-3">📱</div>
                                <h5>Responsive hoàn hảo</h5>
                                <p className="text-muted">
                                    Hoạt động mượt mà trên mọi thiết bị: máy tính, máy tính bảng và điện thoại.
                                </p>
                            </Card.Body>
                        </Card>
                    </Col>
                </Row>

                {/* TECH STACK */}
                <div className="tech-section mb-5">
                    <h3 className="text-center mb-5">🛠️ Công nghệ sử dụng</h3>
                    <Row className="g-4 justify-content-center">
                        {["ReactJS", "Spring Boot", "MySQL", "Bootstrap 5", "JWT Auth"].map((tech, index) => (
                            <Col md={2} sm={4} xs={6} key={index}>
                                <div className="tech-box text-center">
                                    {tech}
                                </div>
                            </Col>
                        ))}
                    </Row>
                </div>

            </Container>
        </div>
    );
}