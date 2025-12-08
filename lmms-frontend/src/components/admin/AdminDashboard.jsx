import { useState, useEffect } from "react";
import { Container, Table, Button, Form, Modal } from "react-bootstrap";
import axios from "axios";

export default function AdminDashboard() {
    const [books, setBooks] = useState([]);
    const [show, setShow] = useState(false);
    const [title, setTitle] = useState("");
    const [author, setAuthor] = useState("");
    const [file, setFile] = useState(null);

    const fetchBooks = async () => {
        const res = await axios.get("/api/books");
        setBooks(res.data);
    };

    useEffect(() => {
        fetchBooks();
    }, []);

    const handleAdd = async (e) => {
        e.preventDefault();
        const formData = new FormData();
        formData.append("title", title);
        formData.append("author", author);
        formData.append("file", file);

        await axios.post("/api/books", formData, {
            headers: { "Content-Type": "multipart/form-data" }
        });

        setShow(false);
        setTitle("");
        setAuthor("");
        setFile(null);
        fetchBooks();
    };

    return (
        <Container className="mt-4 text-light bg-dark p-4 rounded">
            <h2 className="mb-3 text-center">Book Management</h2>
            <Button variant="primary" className="mb-3" onClick={() => setShow(true)}>
                + Add Book
            </Button>

            <Table striped bordered hover variant="dark">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Title</th>
                        <th>Author</th>
                    </tr>
                </thead>
                <tbody>
                    {books.map((b) => (
                        <tr key={b.id}>
                            <td>{b.id}</td>
                            <td>{b.title}</td>
                            <td>{b.author}</td>
                        </tr>
                    ))}
                </tbody>
            </Table>

            <Modal show={show} onHide={() => setShow(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Add New Book</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form onSubmit={handleAdd}>
                        <Form.Group className="mb-3">
                            <Form.Label>Title</Form.Label>
                            <Form.Control type="text" value={title} onChange={(e) => setTitle(e.target.value)} required />
                        </Form.Group>

                        <Form.Group className="mb-3">
                            <Form.Label>Author</Form.Label>
                            <Form.Control type="text" value={author} onChange={(e) => setAuthor(e.target.value)} required />
                        </Form.Group>

                        <Form.Group className="mb-3">
                            <Form.Label>PDF File</Form.Label>
                            <Form.Control type="file" onChange={(e) => setFile(e.target.files[0])} required />
                        </Form.Group>

                        <Button variant="primary" type="submit">Save</Button>
                    </Form>
                </Modal.Body>
            </Modal>
        </Container>
    );
}
