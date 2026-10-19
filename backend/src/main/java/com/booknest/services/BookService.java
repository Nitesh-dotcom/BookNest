package com.booknest.services;

import com.booknest.dto.BookCreateRequest;
import com.booknest.dto.BookDto;
import com.booknest.dto.BookUpdateRequest;
import com.booknest.dto.PagedResponse;
import com.booknest.entities.Book;
import com.booknest.entities.Category;
import com.booknest.exceptions.BadRequestException;
import com.booknest.exceptions.ResourceNotFoundException;
import com.booknest.repositories.BookRepository;
import com.booknest.repositories.CategoryRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    public BookService(BookRepository bookRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public PagedResponse<BookDto> getBooks(
            String query,
            Long categoryId,
            String author,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean inStockOnly,
            Boolean activeOnly,
            int page,
            int size,
            String sortBy,
            String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection != null ? sortDirection : "DESC"),
                resolveSortField(sortBy));
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 50), sort);

        Specification<Book> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (activeOnly == null || activeOnly) {
                predicates.add(cb.isTrue(root.get("active")));
            }

            if (StringUtils.hasText(query)) {
                String term = "%" + query.trim().toLowerCase() + "%";
                Predicate titleP = cb.like(cb.lower(root.get("title")), term);
                Predicate authorP = cb.like(cb.lower(root.get("author")), term);
                Predicate isbnP = cb.like(cb.lower(root.get("isbn")), term);
                Predicate descP = cb.like(cb.lower(root.get("description")), term);
                predicates.add(cb.or(titleP, authorP, isbnP, descP));
            }

            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }

            if (StringUtils.hasText(author)) {
                predicates.add(cb.like(cb.lower(root.get("author")), "%" + author.trim().toLowerCase() + "%"));
            }

            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            if (inStockOnly != null && inStockOnly) {
                predicates.add(cb.greaterThan(root.get("stockQuantity"), 0));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Book> bookPage = bookRepository.findAll(spec, pageable);
        List<BookDto> dtos = bookPage.getContent().stream()
                .map(BookDto::from)
                .collect(Collectors.toList());

        return PagedResponse.from(bookPage, dtos);
    }

    @Transactional(readOnly = true)
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));
        return BookDto.from(book);
    }

    @Transactional(readOnly = true)
    public List<BookDto> getFeaturedBooks(int limit) {
        Pageable pageable = PageRequest.of(0, Math.min(limit, 20), Sort.by(Sort.Direction.DESC, "createdAt"));
        return bookRepository.findByFeaturedTrueAndActiveTrue(pageable)
                .stream().map(BookDto::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookDto> getBestsellers(int limit) {
        Pageable pageable = PageRequest.of(0, Math.min(limit, 20), Sort.by(Sort.Direction.DESC, "averageRating"));
        return bookRepository.findByBestsellerTrueAndActiveTrue(pageable)
                .stream().map(BookDto::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookDto> getNewArrivals(int limit) {
        Pageable pageable = PageRequest.of(0, Math.min(limit, 20), Sort.by(Sort.Direction.DESC, "createdAt"));
        return bookRepository.findByActiveTrueOrderByCreatedAtDesc(pageable)
                .stream().map(BookDto::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookDto> getRelatedBooks(Long bookId, int limit) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        Pageable pageable = PageRequest.of(0, Math.min(limit, 10), Sort.by(Sort.Direction.DESC, "averageRating"));
        return bookRepository.findRelatedBooks(book.getCategory().getId(), bookId, pageable)
                .stream().map(BookDto::from).collect(Collectors.toList());
    }

    @Transactional
    public BookDto createBook(BookCreateRequest req) {
        String cleanIsbn = req.getIsbn().trim();
        if (bookRepository.existsByIsbn(cleanIsbn)) {
            throw new BadRequestException("Book with ISBN " + cleanIsbn + " already exists");
        }

        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + req.getCategoryId()));

        Book book = new Book();
        book.setCategory(category);
        book.setTitle(req.getTitle().trim());
        book.setAuthor(req.getAuthor().trim());
        book.setIsbn(cleanIsbn);
        book.setDescription(req.getDescription().trim());
        book.setPrice(req.getPrice());
        book.setOriginalPrice(req.getOriginalPrice());
        book.setStockQuantity(req.getStockQuantity());
        book.setCoverImageUrl(req.getCoverImageUrl());
        book.setPublisher(req.getPublisher());
        book.setPublicationDate(req.getPublicationDate());
        book.setFeatured(req.getFeatured() != null && req.getFeatured());
        book.setBestseller(req.getBestseller() != null && req.getBestseller());
        book.setActive(req.getActive() != null ? req.getActive() : true);

        return BookDto.from(bookRepository.save(book));
    }

    @Transactional
    public BookDto updateBook(Long id, BookUpdateRequest req) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));

        String cleanIsbn = req.getIsbn().trim();
        if (bookRepository.existsByIsbnAndIdNot(cleanIsbn, id)) {
            throw new BadRequestException("Another book already exists with ISBN: " + cleanIsbn);
        }

        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + req.getCategoryId()));

        book.setCategory(category);
        book.setTitle(req.getTitle().trim());
        book.setAuthor(req.getAuthor().trim());
        book.setIsbn(cleanIsbn);
        book.setDescription(req.getDescription().trim());
        book.setPrice(req.getPrice());
        book.setOriginalPrice(req.getOriginalPrice());
        book.setStockQuantity(req.getStockQuantity());
        book.setCoverImageUrl(req.getCoverImageUrl());
        book.setPublisher(req.getPublisher());
        book.setPublicationDate(req.getPublicationDate());
        if (req.getFeatured() != null) book.setFeatured(req.getFeatured());
        if (req.getBestseller() != null) book.setBestseller(req.getBestseller());
        if (req.getActive() != null) book.setActive(req.getActive());

        return BookDto.from(bookRepository.save(book));
    }

    @Transactional
    public BookDto updateStock(Long id, int stockQuantity) {
        if (stockQuantity < 0) {
            throw new BadRequestException("Stock quantity cannot be negative");
        }
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));
        book.setStockQuantity(stockQuantity);
        return BookDto.from(bookRepository.save(book));
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));
        // Soft delete to protect integrity of historical orders
        book.setActive(false);
        bookRepository.save(book);
    }

    @Transactional
    public BookDto toggleActive(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));
        book.setActive(!book.isActive());
        return BookDto.from(bookRepository.save(book));
    }

    private String resolveSortField(String sortBy) {
        if (sortBy == null) return "createdAt";
        return switch (sortBy.toLowerCase()) {
            case "price" -> "price";
            case "title" -> "title";
            case "rating" -> "averageRating";
            case "stock" -> "stockQuantity";
            default -> "createdAt";
        };
    }
}
