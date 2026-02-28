import { useState, useEffect } from "react";
import { Container, Table, Button, Form, Modal } from "react-bootstrap";
import axios from "axios";
import AddBook from "./AddBook";
import "./css/AdminDashboard.css";

export default function AdminDashboard() {
    const [books, setBooks] = useState([]);
    const [show, setShow] = useState(false);

    const fetchBooks = async () => {
    try {
      const res = await axios.get("/api/books");
      console.log("API books response:", res.data);
      setBooks(res.data)
    } catch (err) {
      console.error("Error fetching books:", err);
    }
  }

    useEffect(() => {
        fetchBooks();
    }, []);

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
            </div>
        </div>
    );
}
