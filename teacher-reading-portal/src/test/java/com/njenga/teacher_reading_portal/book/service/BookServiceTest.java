package com.njenga.teacher_reading_portal.book.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.njenga.teacher_reading_portal.book.Book;
import com.njenga.teacher_reading_portal.book.BookRepository;
import com.njenga.teacher_reading_portal.book.dto.BookResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void getAllBooksReturnsMappedBooks() {
        Book book = Book.builder()
                .id(1L)
                .title("The Curious Forest")
                .author("Maya Stone")
                .description("A short story about curiosity.")
                .content("Book content")
                .build();

        when(bookRepository.findAll()).thenReturn(List.of(book));

        List<BookResponse> responses = bookService.getAllBooks();

        assertThat(responses).containsExactly(new BookResponse(
                1L,
                "The Curious Forest",
                "Maya Stone",
                "A short story about curiosity.",
                "Book content"
        ));
        verify(bookRepository).findAll();
    }
}
