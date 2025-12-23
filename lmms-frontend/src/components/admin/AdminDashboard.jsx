import { useState, useEffect } from "react";
import { Container, Table, Button, Form, Modal } from "react-bootstrap";
import axios from "axios";
import AddBook from "./AddBook";

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
        <Container className="mt-4 text-light bg-dark p-4 rounded">
            <h2 className="mb-3 text-center">Quản lý sách</h2>
            <Button variant="primary" className="mb-3" onClick={() => setShow(true)}>
                + Thêm sách
            </Button>

            <Table striped bordered hover variant="dark">
                <thead>
                    <tr>
                        <th>Tiêu đề</th>
                        <th>Tác giả</th>
                        <th>Mô tả</th>
                        <th style={{ width: "120px" }}>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    {books.map((b) => (
                        <tr key={b.id}>
                            <td>{b.title}</td>
                            <td>{b.author}</td>
                            <td>{b.description}</td>
                            <td className="text-center">
                                <Button
                                    variant="danger"
                                    size="sm"
                                    onClick={() => handleDelete(b.id)}
                                >
                                    Xóa
                                </Button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </Table>

             <AddBook 
                show={show} 
                onHide={() => setShow(false)} 
                onUploaded={fetchBooks} 
            />
        </Container>
    );
}
