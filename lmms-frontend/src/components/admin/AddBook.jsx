import { useState } from "react";
import { Modal, Button, Form } from "react-bootstrap";
import axios from "axios";

export default function AddBook({ show, onHide, onUploaded }) {
    const [file, setFile] = useState(null);
    const [title, setTitle] = useState("");
    const [author, setAuthor] = useState("");
    const [description, setDescription] = useState("");

    const handleAdd = async (e) => {
        e.preventDefault();
        if (!file) {
            alert("Vui lòng chọn file PDF");
            return;
        }

        const formData = new FormData();
        formData.append("file", file);
        formData.append("title", title);
        formData.append("author", author);
        formData.append("description", description);

        try {
            await axios.post("/api/books/upload", formData, {
                headers: { "Content-Type": "multipart/form-data" }
            });

            alert("Upload thành công!");
            onUploaded();     // load lại danh sách sách
            onHide();         // đóng modal
        } catch (error) {
            alert(error.response?.data?.message || "Lỗi khi upload sách. Vui lòng thử lại.");
        }
    };

    return (
        <Modal show={show} onHide={onHide}>
            <Modal.Header closeButton>
                <Modal.Title>Thêm sách mới</Modal.Title>
            </Modal.Header>

            <Modal.Body>
                <Form onSubmit={handleAdd}>
                    <Form.Group className="mb-3">
                        <Form.Label>Chọn file PDF</Form.Label>
                        <Form.Control
                            type="file"
                            accept=".pdf"
                            onChange={(e) => setFile(e.target.files[0])}
                        />
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label>Tiêu đề</Form.Label>
                        <Form.Control
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                            required
                        />
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label>Tác giả</Form.Label>
                        <Form.Control
                            value={author}
                            onChange={(e) => setAuthor(e.target.value)}
                            required
                        />
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label>Mô tả</Form.Label>
                        <Form.Control
                            as="textarea"
                            rows={3}
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                        />
                    </Form.Group>

                    <Button type="submit">Thêm</Button>
                </Form>
            </Modal.Body>
        </Modal>
    );
}
