package com.example.baitapsession08.repository;

import com.example.baitapsession08.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
