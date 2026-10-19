package com.booknest.services;

import com.booknest.dto.NewsletterRequest;
import com.booknest.entities.NewsletterSubscriber;
import com.booknest.exceptions.BadRequestException;
import com.booknest.repositories.NewsletterSubscriberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NewsletterService {

    private final NewsletterSubscriberRepository subscriberRepository;

    public NewsletterService(NewsletterSubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }

    @Transactional
    public void subscribe(NewsletterRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (subscriberRepository.existsByEmailIgnoreCase(email)) {
            throw new BadRequestException("You are already subscribed to the BookNest newsletter!");
        }
        subscriberRepository.save(new NewsletterSubscriber(email));
    }
}
