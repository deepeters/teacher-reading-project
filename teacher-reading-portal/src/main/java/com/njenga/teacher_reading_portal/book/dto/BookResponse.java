package com.njenga.teacher_reading_portal.book.dto;

import com.njenga.teacher_reading_portal.book.Book;

public record BookResponse(
        Long id,
        String title,
        String author,
        String description,
        String content
) {
    public static BookResponse from(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getDescription(),
                book.getContent()
        );
    }
}
