import { useEffect, useState, useCallback } from "react";
import {
  Container,
  Row,
  Col,
  Card,
  Button,
  Alert,
  InputGroup,
  FormControl,
  Spinner,
  ListGroup
} from "react-bootstrap";
import axios from "axios";
import AIAnswer from "./AIAnswer.jsx";
import "./css/HomePage.css";
import "./css/Categories.css";

export default function HomePage() {
  const [books, setBooks] = useState([]);
  const [error, setError] = useState(null);
  const [question, setQuestion] = useState("");
  const [categories, setCategories] = useState([]);
  const [selectedCategoryId, setSelectedCategoryId] = useState("ALL");
  // States cho AI
  const [aiAnswer, setAiAnswer] = useState("");
  const [typedAnswer, setTypedAnswer] = useState("");
  const [aiSources, setAiSources] = useState([]);
  const [loading, setLoading] = useState(false);

  const shouldHideBooks = loading || aiAnswer !== "";

  const fetchCategories = useCallback(async () => {
    try {
      const res = await axios.get("/api/categories");
      setCategories([{ id: "ALL", name: "Tất cả" }, ...res.data]);
    } catch (err) {
      console.error("Lỗi tải category", err);
    }
  }, []);


  // Fetch danh sách sách ban đầu
  const fetchBooks = useCallback(async (categoryId = null) => {
    setError(null);
    try {
      const url =
        categoryId && categoryId !== "ALL"
          ? `/api/categories/${categoryId}`
          : `/api/books`;

      const res = await axios.get(url);
      setBooks(Array.isArray(res.data) ? res.data : []);
    } catch (err) {
      setError("Không thể tải danh sách sách. Vui lòng thử lại.");
      console.error("Lỗi tải sách:", err);
    }
  }, []);

  useEffect(() => {
    fetchCategories();
    fetchBooks();
  }, [fetchCategories, fetchBooks]);

  // Xử lý tìm kiếm và hỏi AI
  const handleSearch = async () => {
    const token = localStorage.getItem("token");

    if (!token) {
      return;
    }
    if (!question.trim()) return;

    setLoading(true);
    setError(null);
    setAiAnswer(""); // Reset câu trả lời cũ
    setTypedAnswer("");
    try {
      const resAi = await axios.post("/api/auth/ask", { question });
      setAiAnswer(resAi.data.answer || "Không tìm thấy câu trả lời.");
      setAiSources(resAi.data.aiResponseDTOListPage || []);
    } catch (err) {
      console.error(err);
      setError("Có lỗi xảy ra khi kết nối với AI. Vui lòng thử lại.");
    } finally {
      setLoading(false);
    }
  };

  // Hiệu ứng Typewriter (Gõ chữ)
  useEffect(() => {
    if (!aiAnswer) return;
    setTypedAnswer("");
    let index = 0;
    const interval = setInterval(() => {
     setTypedAnswer(aiAnswer.substring(0, index));
      index++;
      if (index >= aiAnswer.length) {
        clearInterval(interval);
      }
    }, 15); // Tăng tốc độ gõ một chút cho trải nghiệm mượt hơn

    return () => clearInterval(interval);
  }, [aiAnswer]);

  const handleDownload = async (filepath) => {
    try {
      const token = localStorage.getItem("token");

      const response = await axios.get(
        `http://localhost:8080/api/books/download/${filepath}`,
        {
          responseType: "blob",
          headers: token
            ? { Authorization: `Bearer ${token}` }
            : {},
        }
      );

      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", filepath);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (error) {
      console.error("Download error:", error);
    }
  };


  return (
    <Container className="home-page" >
      {/* Header Section */}
      <header className="home-hero text-center">
        <h1 className="display-4 fw-bold home-title">
          Thư viện Sách & Tài liệu
        </h1>
        <p className="lead text-muted mx-auto" style={{ maxWidth: "600px" }}>
          Tra cứu thông tin thông minh với AI và khám phá hàng ngàn tài liệu học thuật miễn phí.
        </p>
      </header>

      {/* Search Bar Section */}
      <Row className="justify-content-center">
        <Col lg={8}>
          <InputGroup size="lg" className="shadow-sm position-relative home-search">
            <FormControl
              placeholder={localStorage.getItem("token") ? "Nhập câu hỏi về tài liệu..." : "Đăng nhập để đặt câu hỏi cho AI..."}
              value={question}
              onChange={(e) => setQuestion(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && handleSearch()}
            // Vô hiệu hóa input nếu muốn cưỡng ép đăng nhập
            // disabled={!localStorage.getItem("token")} 
            />
            <Button
              variant={localStorage.getItem("token") ? "primary" : "secondary"}
              onClick={handleSearch}
              disabled={loading}
              className="px-4"
            >
              {loading ? <Spinner animation="border" size="sm" /> : "Hỏi AI"}
            </Button>
          </InputGroup>

          {/* Hiển thị nút đăng nhập nhanh nếu chưa có token */}
          {!localStorage.getItem("token") && (
            <p className="text-center mt-2 small text-muted">
              Bạn chưa đăng nhập? <a href="/login" className="text-primary fw-bold">Đăng nhập ngay</a> để sử dụng AI.
            </p>
          )}
        </Col>
      </Row>

      {/* AI Response Display Area */}
      <Row className="justify-content-center mb-5">
        <Col lg={10} className="ai-area">
          {loading && !typedAnswer && (
            <div className="text-center my-4">
              <Spinner animation="grow" variant="primary" />
              <p className="text-muted mt-2">AI đang tìm kiếm...</p>
            </div>
          )}
          <div>
            <AIAnswer answer={typedAnswer} sources={aiSources} />
          </div>
        </Col>
      </Row>

      {/* Error Alert */}
      {error && (
        <Alert variant="danger" className="d-flex align-items-center justify-content-between shadow-sm">
          <div>
            <i className="bi bi-exclamation-triangle-fill me-2"></i>
            {error}
          </div>
          <Button variant="outline-danger" size="sm" onClick={fetchBooks}>Thử lại</Button>
        </Alert>
      )}

      {/* Books Gallery */}

      {!shouldHideBooks && (
        <section className="mt-5 books-section">
          <div className="d-flex align-items-center mb-4">
            <h2 className="h4 fw-bold mb-0">📚 Tài liệu phổ biến</h2>
            <hr className="flex-grow-1 ms-3 opacity-25" />
          </div>

          <Row>
            <Col md={3}>
              <div className="categories-section">
                <h3 className="categories-title">Thể loại</h3>
                <div className="categories-list">
                  {categories.map((cat) => (
                    <button
                      key={cat.id}
                      className={`category-item ${selectedCategoryId === cat.id ? 'active' : ''}`}
                      onClick={() => {
                        setSelectedCategoryId(cat.id);
                        fetchBooks(cat.id);
                      }}
                    >
                      {cat.name}
                    </button>
                  ))}
                </div>
              </div>
            </Col>
            <Col md={9}>
              <Row className="g-4">
                {books.length > 0 ? (
                  books.map((book) => (
                    <Col md={6} lg={4} key={book.id} className="mb-4">
                      <Card className="border-0 shadow-sm hover-shadow transition book-card">
                        <Card.Body className="d-flex flex-column">
                          <div className="mb-2">
                            <span className="badge bg-light text-primary border">PDF</span>
                          </div>
                          <Card.Title className="h5 fw-bold">{book.title}</Card.Title>
                          <Card.Text className="text-muted mb-4 small">
                            Tác giả: {book.author || "Đang cập nhật"}
                          </Card.Text>
                          <div className="mt-auto d-flex gap-2">
                            <Button
                              variant="outline-primary"
                              className="w-50"
                              onClick={() =>
                                window.open(`http://localhost:8080/uploads/${book.filepath}`, "_blank")
                              }
                            >
                              Đọc
                            </Button>

                            <Button
                              variant="primary"
                              className="w-50"
                              onClick={() => handleDownload(book.filepath)}
                            >
                              Tải về
                            </Button>
                          </div>
                        </Card.Body>
                      </Card>
                    </Col>
                  ))
                ) : (
                  !loading && <p className="text-center text-muted">Chưa có sách nào trong thư viện.</p>
                )}
              </Row>
            </Col>

          </Row>

        </section>
      )}
    </Container>
  );
}