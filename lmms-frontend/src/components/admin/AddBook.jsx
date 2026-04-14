import { useEffect, useState } from "react";
import { Modal, Button, Form } from "react-bootstrap";
import axios from "axios";

export default function AddBook({ show, onHide, onUploaded }) {
    const [file, setFile] = useState(null);
    const [title, setTitle] = useState("");
    const [author, setAuthor] = useState("");
    const [description, setDescription] = useState("");

    const [categories, setCategories] = useState([]);
    const [category, setCategory] = useState("");
    const [newCategory, setNewCategory] = useState(""); 

    useEffect(() => {
        if (show) {
            axios.get("/api/categories")
                .then(res => setCategories(res.data));
        }
    }, [show]);

    const handleAdd = async (e) => {
        e.preventDefault();
        if (!file) {
            alert("Vui lòng chọn file PDF");
            return;
        }
        const categoryName =
            category === "__new__" ? newCategory : category;

        if (!categoryName) {
            alert("Vui lòng chọn hoặc nhập thể loại");
            return;
        }



        const formData = new FormData();
        formData.append("file", file);
        formData.append("title", title);
        formData.append("author", author);
        formData.append("description", description);
        formData.append("categoryName", categoryName);

        try {
            await axios.post("/api/books/upload", formData, {
                headers: { "Content-Type": "multipart/form-data" }   //Lưu dưới dạng khi có upload file
            });
            
            alert("Upload thành công!");
            onUploaded();     // load lại danh sách sách
            onHide();         // đóng modal
            // reset
            setFile(null);
            setTitle("");
            setAuthor("");
            setDescription("");
            setCategory("");
            setNewCategory("");
        } catch (error) {
            console.error("Lỗi khi upload sách:", error.response.data);
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

                    <Form.Group className="mb-3">
                        <Form.Label>Thể loại</Form.Label>
                        <Form.Select
                            value={category}
                            onChange={(e) => setCategory(e.target.value)}
                        >
                            <option value="">-- Chọn thể loại --</option>
                            {Array.isArray(categories) && categories.map(c => (
                                <option key={c.id} value={c.name}>
                                    {c.name}
                                </option>
                            ))}
                            <option value="__new__">➕ Thêm thể loại mới</option>
                        </Form.Select>
                    </Form.Group>

                    {category === "__new__" && (
                        <Form.Group className="mb-3">
                            <Form.Label>Thể loại mới</Form.Label>
                            <Form.Control
                                placeholder="Nhập tên thể loại"
                                value={newCategory}
                                onChange={(e) => setNewCategory(e.target.value)}
                                required
                            />
                        </Form.Group>
                    )}

                    <Button type="submit">Thêm</Button>
                </Form>
            </Modal.Body>
        </Modal>
    );
}
