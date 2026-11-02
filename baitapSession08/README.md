# Session08 - Bài 1 và 2: Thêm sách kèm ảnh bìa và cập nhật tồn kho

Java 21, Spring Boot, JPA và PostgreSQL.

## Chạy

1. Mở project trong IntelliJ, chọn JDK 21.
2. Chạy `BaitapSession08Application`, đặt Working directory là thư mục project `baitapSession08`.
3. Thử API bằng Postman theo ví dụ bên dưới.

Database `library_management_session08` và kết nối đã được chuẩn bị trên máy hiện tại. Mật khẩu nằm ngoài project tại `../.local-config/baitapSession08.properties`.

Máy khác cần chạy `CREATE DATABASE library_management_session08;`, rồi đặt biến môi trường `DB_USERNAME`, `DB_PASSWORD`. Có thể đổi địa chỉ kết nối bằng `DB_URL`. Hibernate tự tạo bảng `books` nhờ `ddl-auto=update`.

## Thêm sách

```text
POST http://localhost:8080/api/books
```

Chọn **Body → form-data** trong Postman:

| Key | Type | Value mẫu |
| --- | --- | --- |
| title | Text | Lão Hạc |
| author | Text | Nam Cao |
| stock | Text | 50 |
| coverImage | File | Chọn một ảnh bìa trên máy |

Không đặt Content-Type thủ công; Postman tự tạo multipart/form-data với boundary.

Controller nhận `@Valid @ModelAttribute BookCreateDTO`. Service lưu file vào `uploads/` trong Working directory, đặt tên UUID để tránh trùng tên, gán đường dẫn tương đối vào coverUrl rồi lưu Book qua JpaRepository.

Thành công trả **201 Created**, JSON gồm id tự tăng, title, author, stock, coverUrl. Ví dụ coverUrl: `uploads/550e8400-e29b-41d4-a716-446655440000.png`. Đây là đường dẫn file tương đối, dùng để kiểm tra ảnh trong thư mục uploads.

Ảnh hỗ trợ đuôi PNG, JPG, JPEG, GIF, WEBP; tối đa 5 MB. Tên sách/tác giả bắt buộc và tối đa 255 ký tự, stock là số nguyên không âm. Thiếu/rỗng ảnh hoặc dữ liệu sai trả 400; file quá lớn trả 413. Lỗi được bọc trong ApiResponse (status, message, data).

Service dùng `@Transactional`; nếu thao tác ghi file hoặc lưu DB trong phương thức thất bại, file mới được dọn để tránh ảnh không có bản ghi sách.

## Cập nhật số lượng tồn (Bài 2)

Trong Postman chọn **PATCH → Body → raw → JSON**:

```text
PATCH http://localhost:8080/api/books/update/1
Content-Type: application/json
```

```json
{
  "stock": 25
}
```

Thay 1 bằng id nhận được khi thêm sách. `BookUpdateStockDTO` dùng `@NotNull` và `@Min(0)`, Controller dùng `@Valid @RequestBody`.

`BookService.updateBook(Long id, BookUpdateStockDTO dto)` tìm sách bằng findById, cập nhật riêng stock rồi save trong `@Transactional`. title, author và coverUrl giữ nguyên. Thành công trả **200 OK** kèm thông tin sách sau cập nhật.

- stock âm, thiếu hoặc null: **400 Bad Request**, lỗi nằm trong `data.stock` của ApiResponse.
- stock không phải số nguyên, JSON sai hoặc ID sai kiểu: **400 Bad Request**.
- Không tìm thấy sách: Service ném **ResourceNotFoundException**, GlobalExceptionHandler trả **404 Not Found** qua ApiResponse.

Ví dụ phản hồi khi ID không tồn tại:

```json
{
  "status": "404 NOT_FOUND",
  "message": "Không tìm thấy sách với ID 999999",
  "data": null
}
```

## Kiểm tra kết quả

Mở thư mục `uploads` của project để xem ảnh đã nhận. Trong pgAdmin chọn database `library_management_session08` rồi chạy:

```sql
SELECT id, title, author, stock, cover_url FROM books ORDER BY id;
```

Build/cache được đặt ngoài project khi kiểm tra. `.gitignore` bỏ qua build, cache, uploads và file cấu hình mật khẩu. Nếu đã tự build nhiều lần bằng IntelliJ, chỉ kéo mã nguồn và các file Gradle lên GitHub, bỏ qua build/.gradle/.idea/uploads.
