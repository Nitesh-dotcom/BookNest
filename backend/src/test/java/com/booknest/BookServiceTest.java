package com.booknest;

import com.booknest.dto.BookCreateRequest;
import com.booknest.dto.BookDto;
import com.booknest.dto.PagedResponse;
import com.booknest.entities.Category;
import com.booknest.exceptions.BadRequestException;
import com.booknest.repositories.CategoryRepository;
import com.booknest.services.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category testCategory;

    @BeforeEach
    void setup() {
        testCategory = categoryRepository.save(new Category("Test Category", "test-cat-" + System.currentTimeMillis(), "Testing category"));
    }

    @Test
    void testCreateBookAndRetrieve() {
        BookCreateRequest req = new BookCreateRequest();
        req.setCategoryId(testCategory.getId());
        req.setTitle("Test-Driven Architecture");
        req.setAuthor("Martin Fowler");
        req.setIsbn("978-0321125217");
        req.setDescription("A comprehensive testing book.");
        req.setPrice(new BigDecimal("45.50"));
        req.setStockQuantity(20);

        BookDto created = bookService.createBook(req);

        assertNotNull(created.getId());
        assertEquals("Test-Driven Architecture", created.getTitle());
        assertEquals(20, created.getStockQuantity());
        assertTrue(created.isInStock());

        BookDto fetched = bookService.getBookById(created.getId());
        assertEquals(created.getId(), fetched.getId());
        assertEquals("978-0321125217", fetched.getIsbn());
    }

    @Test
    void testRejectDuplicateIsbn() {
        BookCreateRequest req1 = new BookCreateRequest();
        req1.setCategoryId(testCategory.getId());
        req1.setTitle("Book One");
        req1.setAuthor("Author A");
        req1.setIsbn("978-0201485677");
        req1.setDescription("Description");
        req1.setPrice(new BigDecimal("29.99"));
        req1.setStockQuantity(5);
        bookService.createBook(req1);

        BookCreateRequest req2 = new BookCreateRequest();
        req2.setCategoryId(testCategory.getId());
        req2.setTitle("Book Two");
        req2.setAuthor("Author B");
        req2.setIsbn("978-0201485677"); // Same ISBN
        req2.setDescription("Description");
        req2.setPrice(new BigDecimal("39.99"));
        req2.setStockQuantity(10);

        assertThrows(BadRequestException.class, () -> bookService.createBook(req2));
    }

    @Test
    void testSearchAndFilterBooks() {
        PagedResponse<BookDto> results = bookService.getBooks(
                "Dune", null, null, null, null, false, true,
                0, 10, "createdAt", "DESC"
        );

        assertNotNull(results);
        assertTrue(results.getTotalElements() >= 1);
        assertTrue(results.getContent().stream().anyMatch(b -> b.getTitle().contains("Dune")));
    }
}
