package com.librarymanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class BookSearchTest {

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    public void setUp() {
        bookRepository.deleteAll();

        // Add a variety of books for search tests
        List<Book> books = new ArrayList<>();
        books.add(new Book("Java Programming", "Alice", 3));
        books.add(new Book("Advanced Java", "Bob", 2));
        books.add(new Book("Spring in Action", "Craig", 5));
        books.add(new Book("Spring Boot Guide", "Alice", 4));
        books.add(new Book("Hibernate Essentials", "David", 1));
        books.add(new Book("Effective Java", "Eve", 2));

        // add additional books for pagination
        for (int i = 1; i <= 20; i++) {
            books.add(new Book("Book " + i, "Author" + i, 1));
        }

        bookRepository.saveAll(books);
    }

    @Test
    public void testSearchByTitle() {
        Page<Book> page = bookRepository.searchBooks("Java", PageRequest.of(0, 20));
        assertNotNull(page);
        assertTrue(page.getTotalElements() >= 3, "Should find at least 3 books with 'Java' in title or author");
        // ensure returned titles contain 'Java' or author equals 'Java' (case-insensitive)
        boolean found = page.stream().anyMatch(b -> b.getTitle().toLowerCase().contains("java"));
        assertTrue(found, "At least one returned book should have 'java' in the title");
    }

    @Test
    public void testSearchByAuthor() {
        Page<Book> page = bookRepository.searchBooks("Alice", PageRequest.of(0, 10));
        assertNotNull(page);
        // Expect two books authored by Alice
        long count = page.stream().filter(b -> "Alice".equalsIgnoreCase(b.getAuthor())).count();
        assertEquals(2, count, "There should be 2 books authored by Alice");
    }

    @Test
    public void testSearchByTitleAndAuthor() {
        // The repository query uses OR between title LIKE and author =
        // Use a keyword that matches some titles and some authors to verify union behavior
        Page<Book> page = bookRepository.searchBooks("Spring", PageRequest.of(0, 20));
        assertNotNull(page);
        // Expect at least the two Spring-related books
        long springCount = page.stream().filter(b -> b.getTitle().toLowerCase().contains("spring")).count();
        assertEquals(2, springCount, "Should find the 2 books with 'Spring' in the title");
    }

    @Test
    public void testSearchNoResults() {
        Page<Book> page = bookRepository.searchBooks("NonexistentKeyword", PageRequest.of(0, 10));
        assertNotNull(page);
        assertEquals(0, page.getTotalElements(), "Search for unknown keyword should return no results");
    }

    @Test
    public void testPagination() {
        // Use findAll to verify pagination works (10 per page)
        Page<Book> firstPage = bookRepository.findAll(PageRequest.of(0, 10));
        assertNotNull(firstPage);
        assertEquals(10, firstPage.getSize(), "Page size should be 10");
        assertEquals(10, firstPage.getNumberOfElements(), "First page should contain 10 elements");

        // Check total pages given total elements (we inserted 26 books in setup)
        int expectedTotal = (int) bookRepository.count();
        int expectedPages = (int) Math.ceil(expectedTotal / 10.0);
        assertEquals(expectedPages, firstPage.getTotalPages(), "Total pages should match computed value");
    }
}

