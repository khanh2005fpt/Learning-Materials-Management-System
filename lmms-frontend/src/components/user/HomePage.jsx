import { useEffect, useState } from "react";
import {
  Container,
  Row,
  Col,
  Card,
  Badge,
  Button,
  Alert,
} from "react-bootstrap";
import axios from "axios";

export default function HomePage() {
  const [books, setBooks] = useState([]);
  const [error, setError] = useState(null);

  const fetchBooks = async () => {
    setError(null);
    try {
      const res = await axios.get("/api/books");
      setBooks(res.data);
    } catch (err) {
      setError("Không thể tải danh sách sách. Vui lòng thử lại.");
      console.error("Lỗi tải sách:", err);
    } 
  };

  useEffect(() => {
    fetchBooks();
  }, []);


  return (
    <Container className="my-5">
      {/* Tiêu đề */}
      <div className="text-center mb-5">
        <h1 className="display-5 fw-bold text-primary">Thư viện Sách & Tài liệu</h1>
        <p className="lead text-muted">Khám phá và đọc hàng ngàn tài liệu miễn phí</p>
      </div>


      {/* Thông báo lỗi */}
      {error && (
        <Alert variant="danger" className="d-flex justify-content-between align-items-center">
          <span>{error}</span>
          <Button variant="outline-danger" size="sm" onClick={fetchBooks}>
            <i className="bi bi-arrow-clockwise me-2"></i>
            Thử lại
          </Button>
        </Alert>
      )}
      

      {books.length === 0 ? (
        <div className="text-center py-5">
          <div className="display-1 text-muted mb-4">📚</div>
          <h4 className="text-muted">Không tìm thấy sách nào</h4>
        </div>
      ) : (
        <>
          <Row>
            {books.map((book) => (
              <Col key={book.id} md={6} lg={4} className="mb-4">
                <Card
                  className="h-100 shadow-sm border-0 overflow-hidden"
                  style={{ transition: "all 0.3s ease" }}
                >
                  {book.coverImage ? (
                    <Card.Img
                      variant="top"
                      src={`/uploads/covers/${book.coverImage}`}
                      alt={book.title}
                      style={{ height: "220px", objectFit: "cover" }}
                    />
                  ) : (
                    <div
                      className="d-flex align-items-center justify-content-center bg-light"
                      style={{ height: "220px" }}
                    >
                      <i className="bi bi-book display-4 text-muted opacity-50"></i>
                    </div>
                  )}

                  <Card.Body className="d-flex flex-column">
                    <Card.Title className="fw-bold text-primary mb-2">
                      {book.title}
                    </Card.Title>

                    <Card.Subtitle className="mb-3 text-muted small">
                      <i className="bi bi-person me-1"></i>
                      {book.author || "Không rõ tác giả"}
                    </Card.Subtitle>

                    {book.category && (
                      <Badge bg="info" className="mb-3 align-self-start">
                        {book.category}
                      </Badge>
                    )}

                    <Card.Text
                      className="text-muted flex-grow-1"
                    >
                      {book.description || "Không có mô tả."}
                    </Card.Text>

                    <div className="mt-auto">
                      <Button
                        as="a"
                        href={`http://localhost:8080/uploads/${book.filepath}`}
                        target="_blank"
                        rel="noreferrer"
                        variant="outline-primary"
                        className="w-100"
                      >
                        <i className="bi bi-file-earmark-text me-2"></i>
                        Xem tài liệu
                      </Button>
                    </div>
                  </Card.Body>
                </Card>
              </Col>
            ))}
          </Row>
        </>
      )}
    </Container>
  );
}