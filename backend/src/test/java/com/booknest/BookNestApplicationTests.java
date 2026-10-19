package com.booknest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class BookNestApplicationTests {

    @Test
    void contextLoads() {
        // Verifies Spring ApplicationContext boots cleanly with test configuration
    }
}
