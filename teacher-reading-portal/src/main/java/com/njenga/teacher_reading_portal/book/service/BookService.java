package com.njenga.teacher_reading_portal.book.service;

import com.njenga.teacher_reading_portal.book.BookRepository;
import com.njenga.teacher_reading_portal.book.dto.BookResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(BookResponse::from)
                .toList();
    }
}
