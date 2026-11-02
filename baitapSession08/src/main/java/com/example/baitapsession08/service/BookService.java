package com.example.baitapsession08.service;

import com.example.baitapsession08.dto.BookCreateDTO;
import com.example.baitapsession08.dto.BookUpdateStockDTO;
import com.example.baitapsession08.exception.ResourceNotFoundException;
import com.example.baitapsession08.model.Book;
import com.example.baitapsession08.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class BookService {
    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional
    public Book updateBook(Long id, BookUpdateStockDTO dto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách với ID " + id));
        book.setStock(dto.getStock());
        return bookRepository.save(book);
    }

    @Transactional(rollbackFor = IOException.class)
    public Book createBook(BookCreateDTO dto) throws IOException {
        MultipartFile image = dto.getCoverImage();
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn ảnh bìa không rỗng");
        }
        String extension = StringUtils.getFilenameExtension(image.getOriginalFilename());
        if (extension == null || !Set.of("png", "jpg", "jpeg", "gif", "webp")
                .contains(extension.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("Ảnh bìa phải có đuôi PNG, JPG, JPEG, GIF hoặc WEBP");
        }

        Path uploadDirectory = Path.of("uploads");
        Files.createDirectories(uploadDirectory);
        String fileName = UUID.randomUUID() + "." + extension.toLowerCase(Locale.ROOT);
        Path imagePath = uploadDirectory.resolve(fileName);
        try {
            try (InputStream input = image.getInputStream()) {
                Files.copy(input, imagePath);
            }
            Book book = new Book();
            book.setTitle(dto.getTitle());
            book.setAuthor(dto.getAuthor());
            book.setStock(dto.getStock());
            book.setCoverUrl("uploads/" + fileName);
            return bookRepository.saveAndFlush(book);
        } catch (IOException | RuntimeException exception) {
            try {
                Files.deleteIfExists(imagePath);
            } catch (IOException cleanupException) {
                exception.addSuppressed(cleanupException);
            }
            throw exception;
        }
    }
}
