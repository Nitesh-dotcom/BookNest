package com.booknest.config;

import com.booknest.entities.*;
import com.booknest.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           BookRepository bookRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedUsers();
        seedCatalogIfEmpty();
    }

    private void seedUsers() {
        userRepository.findByEmailIgnoreCase("admin@booknest.com").ifPresentOrElse(
                admin -> {
                    admin.setPasswordHash(passwordEncoder.encode("Admin@123"));
                    admin.setRole(Role.ADMIN);
                    admin.setActive(true);
                    userRepository.save(admin);
                    log.info("Synchronized admin password for admin@booknest.com");
                },
                () -> {
                    User admin = new User(
                            "admin@booknest.com",
                            passwordEncoder.encode("Admin@123"),
                            "Eleanor Vance (Admin)",
                            "+1 (555) 234-5678",
                            Role.ADMIN
                    );
                    userRepository.save(admin);
                    log.info("Initialized default administrator account: admin@booknest.com");
                }
        );

        userRepository.findByEmailIgnoreCase("customer@booknest.com").ifPresentOrElse(
                customer -> {
                    customer.setPasswordHash(passwordEncoder.encode("Customer@123"));
                    customer.setRole(Role.CUSTOMER);
                    customer.setActive(true);
                    userRepository.save(customer);
                    log.info("Synchronized customer password for customer@booknest.com");
                },
                () -> {
                    User customer = new User(
                            "customer@booknest.com",
                            passwordEncoder.encode("Customer@123"),
                            "Arthur Pendelton",
                            "+1 (555) 876-5432",
                            Role.CUSTOMER
                    );
                    userRepository.save(customer);
                    log.info("Initialized default customer account: customer@booknest.com");
                }
        );
    }

    private void seedCatalogIfEmpty() {
        if (categoryRepository.count() == 0) {
            log.info("Catalog empty. Initializing categories and books...");
            Category c1 = categoryRepository.save(new Category("Fiction & Literature", "fiction-literature", "Timeless classics and compelling contemporary drama."));
            Category c2 = categoryRepository.save(new Category("Science Fiction & Fantasy", "sci-fi-fantasy", "Epic space sagas and imaginative world-building."));
            Category c3 = categoryRepository.save(new Category("Mystery & Thriller", "mystery-thriller", "Gripping detective novels and psychological suspense."));
            Category c4 = categoryRepository.save(new Category("Business & Economics", "business-economics", "Leadership, investing, and economics."));
            Category c5 = categoryRepository.save(new Category("Science & Technology", "science-technology", "Computer science, distributed systems, and AI."));
            Category c6 = categoryRepository.save(new Category("Self-Improvement & Psychology", "self-improvement", "Habit building, personal growth, and mindfulness."));

            createBook(c1, "The Shadow of the Wind", "Carlos Ruiz Zafón", "978-0143034902",
                    "Barcelona, 1945: A young boy selects an obscure novel that plunges him into a deadly web of mystery.",
                    new BigDecimal("18.99"), new BigDecimal("24.99"), 35,
                    "https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&w=600&q=80",
                    "Penguin Books", LocalDate.of(2005, 1, 25), true, true);

            createBook(c2, "Dune", "Frank Herbert", "978-0441172719",
                    "Set on the desert planet Arrakis, Dune tells the story of Paul Atreides as he navigates political betrayal and a cosmic destiny.",
                    new BigDecimal("19.95"), new BigDecimal("26.00"), 50,
                    "https://images.unsplash.com/photo-1518770660439-4636190af475?auto=format&fit=crop&w=600&q=80",
                    "Chilton Books", LocalDate.of(1965, 8, 1), true, true);

            createBook(c3, "The Silent Patient", "Alex Michaelides", "978-1250301697",
                    "Alicia Berenson’s life is seemingly perfect until one evening she shoots her husband five times in the face and never speaks another word.",
                    new BigDecimal("15.20"), new BigDecimal("19.99"), 45,
                    "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?auto=format&fit=crop&w=600&q=80",
                    "Celadon Books", LocalDate.of(2019, 2, 5), true, true);

            createBook(c4, "The Psychology of Money", "Morgan Housel", "978-0857197689",
                    "Doing well with money isn’t necessarily about what you know. It’s about how you behave. Timeless lessons on wealth and greed.",
                    new BigDecimal("16.99"), new BigDecimal("22.00"), 60,
                    "https://images.unsplash.com/photo-1559526324-4b87b5e36e44?auto=format&fit=crop&w=600&q=80",
                    "Harriman House", LocalDate.of(2020, 9, 8), true, true);

            createBook(c5, "Designing Data-Intensive Applications", "Martin Kleppmann", "978-1449373320",
                    "The definitive guide to the architecture, reliability, and scalability of modern distributed data systems.",
                    new BigDecimal("38.99"), new BigDecimal("49.99"), 25,
                    "https://images.unsplash.com/photo-1555066931-4365d14bab8c?auto=format&fit=crop&w=600&q=80",
                    "O’Reilly Media", LocalDate.of(2017, 3, 16), true, true);

            createBook(c6, "Atomic Habits", "James Clear", "978-0735211292",
                    "A proven framework for improving every day. Tiny changes in system design lead to remarkable life transformations.",
                    new BigDecimal("17.50"), new BigDecimal("23.00"), 75,
                    "https://images.unsplash.com/photo-1499750310107-5fef28a66643?auto=format&fit=crop&w=600&q=80",
                    "Avery", LocalDate.of(2018, 10, 16), true, true);

            log.info("Catalog initialized with core categories and sample books.");
        }
    }

    private void createBook(Category category, String title, String author, String isbn, String description,
                            BigDecimal price, BigDecimal originalPrice, int stock, String coverUrl,
                            String publisher, LocalDate pubDate, boolean featured, boolean bestseller) {
        Book b = new Book();
        b.setCategory(category);
        b.setTitle(title);
        b.setAuthor(author);
        b.setIsbn(isbn);
        b.setDescription(description);
        b.setPrice(price);
        b.setOriginalPrice(originalPrice);
        b.setStockQuantity(stock);
        b.setCoverImageUrl(coverUrl);
        b.setPublisher(publisher);
        b.setPublicationDate(pubDate);
        b.setAverageRating(new BigDecimal("4.80"));
        b.setReviewCount(12);
        b.setFeatured(featured);
        b.setBestseller(bestseller);
        b.setActive(true);
        bookRepository.save(b);
    }
}
