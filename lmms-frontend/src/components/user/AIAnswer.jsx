import React from "react";
import ReactMarkdown from "react-markdown";
import remarkMath from "remark-math";
import rehypeKatex from "rehype-katex";
import "katex/dist/katex.min.css";
export default function AIAnswer({ answer, sources = [] }) {
  if (!answer) return null;

  return (
    <div className="ai-answer-container mt-4">
      <div className="ai-header mb-2">
        <h3>📘 Câu trả lời từ AI</h3>
      </div>

      {/* Nội dung AI */}
      <div className="ai-content mb-3">
        <ReactMarkdown
          remarkPlugins={[remarkMath]}
          rehypePlugins={[rehypeKatex]}
        >
          {answer}
        </ReactMarkdown>
      </div>

      {/* Nguồn trích dẫn */}
      {sources.length > 0 && (
        <div className="ai-sources">
          <h5>📚 Nguồn tham khảo</h5>
          {sources.map((s, i) => (
            <div
              key={i}
              className="p-2 mb-2 bg-light rounded"
              style={{ cursor: "pointer" }}
              onClick={() =>
                window.open(
                  `http://localhost:8080/uploads/${s.filepath}#page=${s.pageNumber}`,
                  "_blank"
                )
              }
            >
              <strong>{s.bookTitle}</strong> – Trang {s.pageNumber} Xem nội dung liên quan
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

