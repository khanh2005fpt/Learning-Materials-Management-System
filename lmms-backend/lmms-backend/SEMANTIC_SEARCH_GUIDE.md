# Semantic Search Implementation Guide

## Tổng Quan
Dự án đã được cập nhật để hỗ trợ **Semantic Search** - một phương pháp tìm kiếm hiện đại dựa trên ý nghĩa của nội dung thay vì chỉ từ khóa.

## Architecture

### 1. **Vector Embeddings**
- **Model**: Google's `text-embedding-004`
- **Dimension**: 768
- **Tính năng**: Chuyển đổi text thành vector số học đại diện ý nghĩa

### 2. **Elasticsearch Dense Vector Search**
- Lưu trữ embeddings trong Elasticsearch
- Sử dụng **Cosine Similarity** để tìm kết quả tương tự
- Hỗ trợ kNN (k-Nearest Neighbors) search

### 3. **RAG Pipeline** (Retrieval Augmented Generation)
```
User Question
    ↓
Generate Embedding
    ↓
Vector Search in Elasticsearch
    ↓
Retrieve Relevant Documents
    ↓
Generate Answer using Gemini API
    ↓
Response with Sources
```

## Các Components Chính

### A. EmbeddingService
**File**: [src/main/java/net/javaguides/lmms/service/EmbeddingService.java](../src/main/java/net/javaguides/lmms/service/EmbeddingService.java)

```java
// Generate embedding từ text
float[] embedding = embeddingService.generateEmbedding("your text");

// Generate embedding cho câu hỏi
float[] questionEmbedding = embeddingService.generateQuestionEmbedding("your question");

// Generate embedding cho content
float[] contentEmbedding = embeddingService.generateContentEmbedding("content");
```

### B. Updated AIService
**File**: [src/main/java/net/javaguides/lmms/service/AIService.java](../src/main/java/net/javaguides/lmms/service/AIService.java)

#### Phương pháp 1: Pure Semantic Search
```java
// Tìm kiếm dựa trên ý nghĩa (vector similarity)
List<AIResponseDTO> results = aiService.searchPages(aiRequestDTO);
```

**Cách hoạt động:**
1. Generate embedding của câu hỏi
2. So sánh với embeddings của tất cả tài liệu trong Elasticsearch
3. Trả về top-10 tài liệu có độ tương tự cao nhất

#### Phương pháp 2: Hybrid Search (Recommend ⭐)
```java
// Kết hợp semantic search + keyword search
List<AIResponseDTO> results = aiService.searchPagesHybrid(aiRequestDTO);
```

**Ưu điểm:**
- Semantic search: Hiểu được ý nghĩa, ngữ cảnh
- Keyword search: Chính xác với từ khóa cụ thể
- Kết hợp: Kết quả tốt hơn và đầy đủ hơn

### C. BookPageDocument Entity
**File**: [src/main/java/net/javaguides/lmms/entity/BookPageDocument.java](../src/main/java/net/javaguides/lmms/entity/BookPageDocument.java)

```java
@Field(type = FieldType.Dense_Vector,
        dims = 768,
        index = true,
        similarity = "cosine")
private float[] embedding;
```

**Cấu hình:**
- `dims=768`: Kích thước vector (dimensionality)
- `index=true`: Cho phép tìm kiếm trên field này
- `similarity=cosine`: Dùng Cosine Similarity để so sánh

### D. BookService Updates
**File**: [src/main/java/net/javaguides/lmms/service/BookService.java](../src/main/java/net/javaguides/lmms/service/BookService.java)

Khi upload PDF, `BookService` sẽ:
1. Extract text từ PDF
2. Split thành chunks
3. **Generate embedding cho mỗi chunk** ← NEW
4. Index vào Elasticsearch với embedding

```java
@Async("threadPoolTaskExecutor")
public void indexBatch(List<BookPage> pages) {
    List<BookPageDocument> docs = pages.stream()
            .map(p -> {
                float[] embedding = embeddingService.generateContentEmbedding(p.getContent());
                return new BookPageDocument(..., embedding);
            })
            .toList();
    searchRepo.saveAll(docs);
}
```

## API Endpoints

### 1. Semantic Search
```http
POST /api/auth/search
Content-Type: application/json

{
    "question": "Mối quan hệ giữa variance và standard deviation là gì?"
}
```

**Response:**
```json
[
    {
        "bookTitle": "Statistics 101",
        "pageNumber": 45,
        "snippet": "Variance là bình phương của standard deviation...",
        "filepath": "uploads/..."
    },
    ...
]
```

### 2. Hybrid Search (Recommend)
```http
POST /api/auth/search/hybrid
Content-Type: application/json

{
    "question": "Mối quan hệ giữa variance và standard deviation là gì?"
}
```

### 3. Ask Gemini (với Semantic Search)
```http
POST /api/auth/ask
Content-Type: application/json

{
    "question": "Mối quan hệ giữa variance và standard deviation là gì?"
}
```

**Response:**
```json
{
    "answer": "Variance là bình phương của standard deviation. Nếu σ là standard deviation, thì variance = σ²...",
    "sources": [
        {
            "bookTitle": "Statistics 101",
            "pageNumber": 45,
            ...
        }
    ]
}
```

## So Sánh: Keyword Search vs Semantic Search

| Yếu tố | Keyword Search | Semantic Search |
|--------|----------------|-----------------|
| **Tìm kiếm dựa trên** | Từ khóa chính xác | Ý nghĩa, ngữ cảnh |
| **Hiểu rõ câu hỏi** | ❌ Không | ✅ Có |
| **Tìm thấy nghĩa tương tự** | ❌ Không | ✅ Có |
| **Ví dụ** | "variance" chỉ tìm từ "variance" | "variance" tìm cả "dispersion", "variability" |
| **Cách hoạt động** | String matching | Vector similarity (cosine, euclidean) |
| **Speed** | ⚡ Nhanh | 🔄 Chậm hơn (cần generate embedding) |
| **Accuracy** | 📊 Trung bình | 📊📊 Cao hơn |

## Configuration Files

### Elasticsearch Mapping & Settings
- [src/main/resources/es-mapping.json](../src/main/resources/es-mapping.json) - Cấu hình mapping cho dense_vector
- [src/main/resources/es-settings.json](../src/main/resources/es-settings.json) - Settings cho index

## Performance Optimization

### 1. Batch Processing
```java
// Async processing để không block thread chính
@Async("threadPoolTaskExecutor")
public void indexBatch(List<BookPage> pages) {
    // Xử lý 100 trang cùng lúc
}
```

### 2. Caching
Có thể thêm caching cho embeddings:
```java
@Cacheable(value = "embeddings", key = "#text.hashCode()")
public float[] generateEmbedding(String text) {
    ...
}
```

### 3. K-value Tuning
```java
.knnSearch(k -> k
    .field("embedding")
    .queryVector(questionEmbedding)
    .k(10)  // ← Adjust dựa trên cần thiết
)
```

## Troubleshooting

### ❌ Embedding Dimension Mismatch
```
Error: Embedding dimension 768 does not match index mapping
```
**Giải pháp**: Xác nhận Google's text-embedding-004 trả về 768 dimensions

### ❌ Dense Vector Not Indexed
```
Error: field 'embedding' is not configured for this search
```
**Giải pháp**: 
- Rebuild Elasticsearch index
- Đảm bảo mapping được load từ es-mapping.json

### ❌ Out of Memory khi Generate Embedding
**Giải pháp**:
- Batch embedding generation
- Tăng Java heap size
- Implement streaming/chunking

## Next Steps

1. **Fine-tuning**: Tune k-value, similarity metric
2. **Caching**: Cache embeddings cho frequently asked questions
3. **Monitoring**: Track embedding generation time
4. **Advanced RAG**: Implement reranking, query expansion

## References
- [Elasticsearch Dense Vector](https://www.elastic.co/guide/en/elasticsearch/reference/current/dense-vector.html)
- [Google Generative AI Embeddings](https://ai.google.dev/api/embeddings)
- [RAG Patterns](https://www.anthropic.com/research/building-effective-agents)
