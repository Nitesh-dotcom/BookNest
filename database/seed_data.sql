-- ========================================================
-- BookNest Online Bookstore - Seed Data
-- ========================================================

USE booknest_db;

-- 1. Seed Users (passwords are BCrypt encrypted for 'Admin@123' and 'Customer@123')
-- Hash for 'Admin@123': $2a$10$wT5tT99X8K9p9C5yM0hUOu7iK2fXGZ8Vq7dKz7Y2XvFh5j7zF0m2S
-- Hash for 'Customer@123': $2a$10$X8hUOu7iK2fXGZ8Vq7dKz7Y2XvFh5j7zF0m2SwT5tT99X8K9p9C5y
INSERT INTO users (id, email, password_hash, full_name, phone, role, active) VALUES
(1, 'admin@booknest.com', '$2a$10$7v1b1lK7n7V8/rG6o8Z3A.YJ3aYpLwXWv9M0Lw5yC9fO4x6J2b0Gy', 'Eleanor Vance (Admin)', '+1 (555) 234-5678', 'ADMIN', TRUE),
(2, 'customer@booknest.com', '$2a$10$7v1b1lK7n7V8/rG6o8Z3A.YJ3aYpLwXWv9M0Lw5yC9fO4x6J2b0Gy', 'Arthur Pendelton', '+1 (555) 876-5432', 'CUSTOMER', TRUE),
(3, 'sarah.connor@example.com', '$2a$10$7v1b1lK7n7V8/rG6o8Z3A.YJ3aYpLwXWv9M0Lw5yC9fO4x6J2b0Gy', 'Sarah Connor', '+1 (555) 345-6789', 'CUSTOMER', TRUE);

-- 2. Seed Categories
INSERT INTO categories (id, name, slug, description, active) VALUES
(1, 'Fiction & Literature', 'fiction-literature', 'Timeless classics, modern masterpieces, contemporary drama, and compelling storytelling.', TRUE),
(2, 'Science Fiction & Fantasy', 'sci-fi-fantasy', 'Epic space sagas, dystopian futures, mythical realms, and imaginative world-building.', TRUE),
(3, 'Mystery & Thriller', 'mystery-thriller', 'Gripping detective novels, psychological thrillers, and edge-of-your-seat suspense.', TRUE),
(4, 'Business & Economics', 'business-economics', 'Leadership, personal finance, investing strategies, entrepreneurship, and economics.', TRUE),
(5, 'Science & Technology', 'science-technology', 'Computer science, artificial intelligence, astrophysics, software architecture, and biology.', TRUE),
(6, 'Self-Improvement & Psychology', 'self-improvement', 'Habit building, mindfulness, cognitive science, personal growth, and emotional resilience.', TRUE);

-- 3. Seed Books (24 Curated Books)
INSERT INTO books (id, category_id, title, author, isbn, description, price, original_price, stock_quantity, cover_image_url, publisher, publication_date, average_rating, review_count, featured, bestseller, active) VALUES
-- Fiction & Literature
(1, 1, 'The Shadow of the Wind', 'Carlos Ruiz Zafón', '978-0143034902', 'Barcelona, 1945: A young boy is taken by his antiquarian book-dealer father to the secret Cemetery of Forgotten Books, where he selects an obscure novel that plunges him into a deadly web of mystery.', 18.99, 24.99, 35, 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&w=600&q=80', 'Penguin Books', '2005-01-25', 4.80, 24, TRUE, TRUE, TRUE),
(2, 1, 'A Gentleman in Moscow', 'Amor Towles', '978-0143110439', 'In 1922, Count Alexander Rostov is sentenced by a Bolshevik tribunal to spend the rest of his life inside the luxurious Hotel Metropol across from the Kremlin, embarking on a deep emotional journey.', 16.50, 21.00, 28, 'https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&fit=crop&w=600&q=80', 'Viking Press', '2016-09-06', 4.70, 19, TRUE, FALSE, TRUE),
(3, 1, 'Klara and the Sun', 'Kazuo Ishiguro', '978-0593318171', 'Nobel laureate Kazuo Ishiguro explores love, humanity, and hope through the eyes of Klara, an Artificial Friend with extraordinary observational qualities.', 15.99, 19.99, 42, 'https://images.unsplash.com/photo-1543002588-bfa74002ed7e?auto=format&fit=crop&w=600&q=80', 'Knopf', '2021-03-02', 4.40, 15, FALSE, FALSE, TRUE),
(4, 1, 'All the Light We Cannot See', 'Anthony Doerr', '978-1501173219', 'A blind French girl and a German orphan boy try to survive the devastation of World War II in occupied France. Winner of the Pulitzer Prize.', 17.20, 22.00, 18, 'https://images.unsplash.com/photo-1532012164546-f432f2e37b73?auto=format&fit=crop&w=600&q=80', 'Scribner', '2014-05-06', 4.90, 32, TRUE, TRUE, TRUE),

-- Science Fiction & Fantasy
(5, 2, 'Dune', 'Frank Herbert', '978-0441172719', 'Set on the desert planet Arrakis, Dune tells the story of Paul Atreides as he navigates political betrayal, ecological survival, and a cosmic destiny that will alter the universe forever.', 19.95, 26.00, 50, 'https://images.unsplash.com/photo-1518770660439-4636190af475?auto=format&fit=crop&w=600&q=80', 'Chilton Books', '1965-08-01', 4.90, 48, TRUE, TRUE, TRUE),
(6, 2, 'Project Hail Mary', 'Andy Weir', '978-0593135204', 'Ryland Grace is the sole survivor on a desperate, last-chance interstellar mission to save humanity from an extinction-level solar crisis. He just has to remember who he is first.', 18.00, 22.50, 31, 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=600&q=80', 'Ballantine Books', '2021-05-04', 4.90, 41, TRUE, TRUE, TRUE),
(7, 2, 'Neuromancer', 'William Gibson', '978-0441569595', 'The seminal cyberpunk masterpiece that coined the term cyberspace, following washed-up console cowboy Case as he is hired for one last impossible corporate hack.', 14.99, 18.00, 20, 'https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?auto=format&fit=crop&w=600&q=80', 'Ace Books', '1984-07-01', 4.60, 18, FALSE, FALSE, TRUE),
(8, 2, 'The Name of the Wind', 'Patrick Rothfuss', '978-0756404741', 'The heroic and tragic tale of Kvothe, from his childhood in a troupe of traveling players to his years as a near-feral orphan in a crime-riddled city, to his daring entry into a legendary school of magic.', 17.50, 21.00, 22, 'https://images.unsplash.com/photo-1514894780887-121968d00567?auto=format&fit=crop&w=600&q=80', 'DAW Books', '2007-03-27', 4.80, 29, FALSE, TRUE, TRUE),

-- Mystery & Thriller
(9, 3, 'The Silent Patient', 'Alex Michaelides', '978-1250301697', 'Alicia Berenson’s life is seemingly perfect until one evening she shoots her husband five times in the face and never speaks another word. Theo Faber is determined to unravel the truth.', 15.20, 19.99, 45, 'https://images.unsplash.com/photo-1509198397868-475647b2a1e5?auto=format&fit=crop&w=600&q=80', 'Celadon Books', '2019-02-05', 4.50, 27, TRUE, TRUE, TRUE),
(10, 3, 'The Maid', 'Nita Prose', '978-0593356159', 'Molly Gray is not like everyone else. Her unique social struggles make her orderly job as hotel maid an ideal haven, until she discovers the infamous Charles Black dead in his suite.', 16.00, 20.00, 26, 'https://images.unsplash.com/photo-1589829085413-56de8ae18c73?auto=format&fit=crop&w=600&q=80', 'Ballantine Books', '2022-01-04', 4.30, 12, FALSE, FALSE, TRUE),
(11, 3, 'The Thursday Murder Club', 'Richard Osman', '978-0143135968', 'In a peaceful retirement village, four unlikely friends meet weekly in the Jigsaw Room to investigate unsolved crimes. When a brutal murder occurs on their doorstep, the game turns deadly serious.', 14.80, 18.50, 34, 'https://images.unsplash.com/photo-1476275466078-4007374efbbe?auto=format&fit=crop&w=600&q=80', 'Pamela Dorman Books', '2020-09-03', 4.60, 21, FALSE, TRUE, TRUE),
(12, 3, 'Gone Girl', 'Gillian Flynn', '978-0307588371', 'On their fifth wedding anniversary, Nick Dunne arrives home to find the front door open, an iron overturned, and his wife Amy vanished into thin air without a trace.', 15.50, 19.00, 16, 'https://images.unsplash.com/photo-1457369804613-52c61a468e7d?auto=format&fit=crop&w=600&q=80', 'Crown Publishing', '2012-06-05', 4.70, 35, FALSE, FALSE, TRUE),

-- Business & Economics
(13, 4, 'The Psychology of Money', 'Morgan Housel', '978-0857197689', 'Doing well with money isn’t necessarily about what you know. It’s about how you behave. Timeless lessons on wealth, greed, and happiness across 19 engaging short stories.', 16.99, 22.00, 60, 'https://images.unsplash.com/photo-1559526324-4b87b5e36e44?auto=format&fit=crop&w=600&q=80', 'Harriman House', '2020-09-08', 4.90, 52, TRUE, TRUE, TRUE),
(14, 4, 'Principles: Life and Work', 'Ray Dalio', '978-1501124020', 'Ray Dalio, one of the world’s most successful investors and entrepreneurs, shares the unconventional principles that he developed and used over forty years to create unique results.', 24.50, 32.00, 19, 'https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?auto=format&fit=crop&w=600&q=80', 'Simon & Schuster', '2017-09-19', 4.70, 23, FALSE, FALSE, TRUE),
(15, 4, 'Zero to One', 'Peter Thiel', '978-0804139298', 'Notes on startups, how to build the future, and why doing what someone else already knows how to do takes the world from 1 to n, adding more of something familiar.', 17.00, 21.50, 38, 'https://images.unsplash.com/photo-1460925895917-afdab827c52f?auto=format&fit=crop&w=600&q=80', 'Crown Business', '2014-09-16', 4.60, 29, FALSE, TRUE, TRUE),
(16, 4, 'Good to Great', 'Jim Collins', '978-0066620992', 'Why some companies make the leap from average to enduring greatness and others do not. Based on a five-year rigorous research study of elite performing corporations.', 21.00, 27.50, 15, 'https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&w=600&q=80', 'HarperBusiness', '2001-10-16', 4.50, 17, FALSE, FALSE, TRUE),

-- Science & Technology
(17, 5, 'Designing Data-Intensive Applications', 'Martin Kleppmann', '978-1449373320', 'The definitive guide to the architecture, reliability, scalability, and maintainability of modern data systems, storage engines, distributed consensus, and stream processing.', 38.99, 49.99, 25, 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?auto=format&fit=crop&w=600&q=80', 'O’Reilly Media', '2017-03-16', 5.00, 64, TRUE, TRUE, TRUE),
(18, 5, 'Clean Code', 'Robert C. Martin', '978-0132350884', 'Even bad code can function. But if code isn’t clean, it can bring a development organization to its knees. A handbook of agile software craftsmanship.', 34.00, 44.00, 30, 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?auto=format&fit=crop&w=600&q=80', 'Prentice Hall', '2008-08-11', 4.60, 38, FALSE, TRUE, TRUE),
(19, 5, 'Artificial Intelligence: A Modern Approach', 'Stuart Russell & Peter Norvig', '978-0134610993', 'The standard and most comprehensive university textbook on artificial intelligence, covering rational agents, machine learning, probabilistic reasoning, and robotics.', 79.99, 99.00, 12, 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=600&q=80', 'Pearson', '2020-04-28', 4.80, 21, FALSE, FALSE, TRUE),
(20, 5, 'A Brief History of Time', 'Stephen Hawking', '978-0553380163', 'A landmark volume in science writing by one of the great minds of our time, exploring profound questions about black holes, the big bang, and cosmic physics.', 14.50, 18.00, 40, 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=600&q=80', 'Bantam Books', '1998-09-01', 4.80, 31, TRUE, FALSE, TRUE),

-- Self-Improvement & Psychology
(21, 6, 'Atomic Habits', 'James Clear', '978-0735211292', 'No matter your goals, Atomic Habits offers a proven framework for improving every day. Learn how tiny changes in system architecture lead to remarkable life transformations.', 17.50, 23.00, 75, 'https://images.unsplash.com/photo-1499750310107-5fef28a66643?auto=format&fit=crop&w=600&q=80', 'Avery', '2018-10-16', 4.90, 78, TRUE, TRUE, TRUE),
(22, 6, 'Thinking, Fast and Slow', 'Daniel Kahneman', '978-0374533557', 'Nobel laureate Daniel Kahneman takes us on a groundbreaking tour of the mind, explaining the two systems that drive the way we think and make everyday decisions.', 16.20, 21.00, 32, 'https://images.unsplash.com/photo-1506126613408-eca07ce68773?auto=format&fit=crop&w=600&q=80', 'Farrar, Straus and Giroux', '2013-04-02', 4.70, 44, FALSE, TRUE, TRUE),
(23, 6, 'Deep Work', 'Cal Newport', '978-1455586691', 'Rules for focused success in a distracted world. Deep work is the ability to focus without distraction on a cognitively demanding task that produces genuine value.', 16.80, 21.50, 27, 'https://images.unsplash.com/photo-1497633762265-9d179a990aa6?auto=format&fit=crop&w=600&q=80', 'Grand Central Publishing', '2016-01-05', 4.70, 26, FALSE, FALSE, TRUE),
(24, 6, 'The Courage to Be Disliked', 'Ichiro Kishimi & Fumitake Koga', '978-1501197277', 'The Japanese phenomenon that shows you how to unlock your power, build authentic interpersonal bonds, and achieve true, unburdened happiness using Adlerian psychology.', 15.00, 19.50, 35, 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?auto=format&fit=crop&w=600&q=80', 'Atria Books', '2018-05-08', 4.60, 22, TRUE, FALSE, TRUE);

-- 4. Seed Initial Wishlist for Customer
INSERT INTO wishlists (id, user_id) VALUES (1, 2);
INSERT INTO wishlist_items (wishlist_id, book_id) VALUES (1, 6), (1, 13);

-- 5. Seed Sample Reviews
INSERT INTO reviews (book_id, user_id, rating, headline, comment, verified_purchase, approved) VALUES
(1, 2, 5, 'An absolute literary gem', 'Carlos Ruiz Zafon weaves an enchanting, atmospheric mystery that stays with you long after the final page.', TRUE, TRUE),
(5, 2, 5, 'The gold standard of worldbuilding', 'Frank Herbert created a political, ecological, and cultural tour-de-force. Simply unforgettable.', TRUE, TRUE),
(13, 2, 5, 'Changed how I think about savings and risk', 'Morgan Housel cuts through financial jargon and teaches practical wisdom. Highly recommended.', TRUE, TRUE),
(17, 3, 5, 'Required reading for every software engineer', 'Clear, deep, and beautifully structured. The benchmark book for distributed systems.', TRUE, TRUE);

-- 6. Seed Newsletter Subscribers
INSERT INTO newsletter_subscribers (email) VALUES
('reader.club@example.com'),
('bookworm99@example.com');
