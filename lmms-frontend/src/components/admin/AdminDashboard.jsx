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
      setBooks(res.data)
    } catch (err) {
      console.error("Error fetching books:", err);
    }
  }

    useEffect(() => {
        fetchBooks();
    }, []);


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
                    </tr>
                </thead>
                <tbody>
                    {books.map((b) => (
                        <tr key={b.id}>
                            <td>{b.title}</td>
                            <td>{b.author}</td>
                            <td>{b.description}</td>
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
