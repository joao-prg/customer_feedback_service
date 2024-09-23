package com.joaogoncalves.feedback.controller;


import com.joaogoncalves.feedback.entity.Feedback;
import com.joaogoncalves.feedback.model.FeedbackCreate;
import com.joaogoncalves.feedback.model.FeedbackListRead;
import com.joaogoncalves.feedback.model.FeedbackRead;
import com.joaogoncalves.feedback.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @PostMapping
    public ResponseEntity<Void> createFeedback(@RequestBody FeedbackCreate request) {
        Feedback feedback = new Feedback(
                request.getRating(),
                request.getFeedback(),
                request.getCustomer(),
                request.getProduct(),
                request.getVendor()
        );

        Feedback savedFeedback = feedbackRepository.save(feedback);

        URI location = URI.create(String.format("/feedback/%s", savedFeedback.getId()));

        return ResponseEntity.created(location).build();  // HTTP 201 Created with Location header
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFeedbackById(@PathVariable String id) {
        Optional<Feedback> feedbackOptional = feedbackRepository.findById(id);

        if (feedbackOptional.isPresent()) {
            Feedback feedback = feedbackOptional.get();
            return ResponseEntity.ok(
                    new FeedbackRead(
                            feedback.getId(),
                            feedback.getRating(),
                            feedback.getFeedback(),
                            feedback.getCustomer(),
                            feedback.getProduct(),
                            feedback.getVendor()
                    )
            );
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<FeedbackListRead> getAllFeedback(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "perPage", defaultValue = "10") int perPage,
            @RequestParam(value = "rating", required = false) Optional<Integer> rating,
            @RequestParam(value = "customer", required = false) Optional<String> customer,
            @RequestParam(value = "product", required = false) Optional<String> product,
            @RequestParam(value = "vendor", required = false) Optional<String> vendor) {
        if (page < 1) {
            page = 1;
        }
        if (perPage < 5 || perPage > 20) {
            perPage = 10;
        }
        Pageable pageable = PageRequest.of(page - 1, perPage, Sort.by(Sort.Direction.DESC, "_id"));
        Page<Feedback> feedbackPage = feedbackRepository.findByFilters(rating, customer, product, vendor, pageable);
        List<FeedbackRead> feedbackResponses = feedbackPage.getContent().stream()
                .map(feedback -> new FeedbackRead(
                        feedback.getId(),
                        feedback.getRating(),
                        feedback.getFeedback(),
                        feedback.getCustomer(),
                        feedback.getProduct(),
                        feedback.getVendor()))
                .collect(Collectors.toList());
        FeedbackListRead response = new FeedbackListRead(
                feedbackPage.getTotalElements(),
                feedbackPage.isFirst(),
                feedbackPage.isLast(),
                feedbackResponses
        );
        return ResponseEntity.ok(response);
    }

}
