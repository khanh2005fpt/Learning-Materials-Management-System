import { useState, useEffect, useCallback } from "react";
import { Container, Table, Button, Form, Modal } from "react-bootstrap";
import axios from "axios";
import AddBook from "./AddBook";
import "./css/AdminDashboard.css";
import UpdateBook from "./UpdateBook";

export default function AdminDashboard() {
    const [books, setBooks] = useState([]);
    const [show, setShow] = useState(false);
    const [error, setError] = useState(null);
    const [categories, setCategories] = useState([]);
    const [selectedCategoryId, setSelectedCategoryId] = useState("ALL");
    const [showUpdate, setShowUpdate] = useState(false);
    const [selectedBook, setSelectedBook] = useState(null);

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

    const handleDelete = async (id) => {
        const confirmDelete = window.confirm("Bạn có chắc chắn muốn xóa sách này?");
        if (!confirmDelete) return;

        try {
            await axios.delete(`/api/books/${id}`);
            alert("Xóa sách thành công!");
            fetchBooks(); // load lại danh sách
        } catch (err) {
            console.error("Lỗi khi xóa sách:", err);
            alert("Xóa sách thất bại!");
        }
    };
    return (
        <div className="admin-page">
            <div className="admin-wrapper">
                <div className="admin-header">
                    <h2 className="admin-title">📚 Quản lý Sách</h2>
                    <p className="admin-subtitle">Quản lý toàn bộ sách trong thư viện</p>
                </div>

                <div className="admin-actions">
                    <button className="btn-add-book" onClick={() => setShow(true)}>
                        + Thêm sách mới
                    </button>

                    <Form.Select
                        id="category-select"
                        name="category"
                        value={selectedCategoryId}
                        onChange={(e) => {
                            const value = e.target.value;
                            setSelectedCategoryId(value);
                            fetchBooks(value);
                        }}
                    >
                        {categories.map((c) => (
                            <option key={c.id} value={c.id}>
                                {c.name}
                            </option>
                        ))}
                    </Form.Select>
                </div>

                <div className="admin-table-container">
                    {books.length > 0 ? (
                        <div className="admin-table-wrapper">
                            <Table className="admin-table">
                                <thead>
                                    <tr>
                                        <th>Tiêu đề</th>
                                        <th>Tác giả</th>
                                        <th>Mô tả</th>
                                        <th>Thể loại</th>
                                        <th style={{ width: "100px" }}>Hành động</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {books.map((b) => (
                                        <tr key={b.id}>
                                            <td>{b.title}</td>
                                            <td>{b.author}</td>
                                            <td>{b.description}</td>
                                            <td>{b.category.name}</td>
                                            <td>
                                                <div className="table-actions">

                                                    <button
                                                        className="btn-view"
                                                        onClick={() =>
                                                            window.open(`http://localhost:8080/uploads/${b.filepath}?t=${Date.now()}#page=1`, "_blank")
                                                        }
                                                    >
                                                        👁️ Xem
                                                    </button>

                                                    <button
                                                        className="btn-edit"
                                                        onClick={() => {
                                                            setSelectedBook(b);
                                                            setShowUpdate(true);
                                                        }}
                                                    >
                                                        ✏️ Sửa
                                                    </button>

                                                    <button
                                                        className="btn-delete"
                                                        onClick={() => handleDelete(b.id)}
                                                    >
                                                        🗑️ Xóa
                                                    </button>

                                                </div>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </Table>
                        </div>
                    ) : (
                        <div className="empty-state">
                            <div className="empty-state-icon">📭</div>
                            <p className="empty-state-text">Chưa có sách nào</p>
                            <p className="empty-state-subtext">Hãy bắt đầu bằng cách thêm sách mới</p>
                        </div>
                    )}
                </div>

                <AddBook
                    show={show}
                    onHide={() => setShow(false)}
                    onUploaded={fetchBooks}
                />
                <UpdateBook
                    show={showUpdate}
                    onHide={() => setShowUpdate(false)}
                    onUpdated={fetchBooks}
                    book={selectedBook}
                />

            </div>
        </div>
    );
}
