import { useEffect, useState } from "react";
import { Modal, Button, Form } from "react-bootstrap";
import axios from "axios";

export default function UpdateBook({ show, onHide, onUpdated, book }) {
    const [file, setFile] = useState(null);
    const [title, setTitle] = useState("");
    const [author, setAuthor] = useState("");
    const [description, setDescription] = useState("");

    const [categories, setCategories] = useState([]);
    const [category, setCategory] = useState("");
    const [newCategory, setNewCategory] = useState("");

    // 🔥 Load categories
    useEffect(() => {
        if (show) {
            axios.get("/api/categories")
                .then(res => setCategories(res.data));
        }
    }, [show]);

    // 🔥 Đổ dữ liệu cũ vào form
    useEffect(() => {
        if (book) {
            setTitle(book.title || "");
            setAuthor(book.author || "");
            setDescription(book.description || "");
            setCategory(book.category?.name || "");
        }
    }, [book]);

    const handleUpdate = async (e) => {
        e.preventDefault();

        const categoryName =
            category === "__new__" ? newCategory : category;

        if (!categoryName) {
            alert("Vui lòng chọn hoặc nhập thể loại");
            return;
        }

        try {
            await axios.put(`/api/books/${book.id}`, {
                title,
                author,
                description,
                categoryName
            });
 
            alert("Cập nhật thành công!");
            onUpdated();
            onHide();
        } catch (error) {
            alert("Lỗi khi cập nhật!");
        }
    };

    return (
        <Modal show={show} onHide={onHide}>
            <Modal.Header closeButton>
                <Modal.Title>Cập nhật sách</Modal.Title>
            </Modal.Header>

            <Modal.Body>
                <Form onSubmit={handleUpdate}>

                    <Form.Group className="mb-3">
                        <Form.Label>Tiêu đề</Form.Label>
                        <Form.Control
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                        />
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label>Tác giả</Form.Label>
                        <Form.Control
                            value={author}
                            onChange={(e) => setAuthor(e.target.value)}
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

                    <Form.Group className="mb-3">
                        <Form.Label>Thể loại</Form.Label>
                        <Form.Select
                            value={category}
                            onChange={(e) => setCategory(e.target.value)}
                        >
                            <option value="">-- Chọn thể loại --</option>
                            {categories.map(c => (
                                <option key={c.id} value={c.name}>
                                    {c.name}
                                </option>
                            ))}
                        </Form.Select>
                    </Form.Group>

                    <Button type="submit">Cập nhật</Button>
                </Form>
            </Modal.Body>
        </Modal>
    );
}