package com.joaogoncalves.feedback.controller;


import com.joaogoncalves.feedback.model.FeedbackCreate;
import com.joaogoncalves.feedback.model.FeedbackListRead;
import com.joaogoncalves.feedback.model.FeedbackRead;
import com.joaogoncalves.feedback.model.FeedbackUpdate;
import com.joaogoncalves.feedback.service.FeedbackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/feedbacks")
@Validated
@Slf4j
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping(path="/new", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<URI> create(@RequestBody @Valid FeedbackCreate feedbackCreate) {
        log.info(
                "User [username: {}] is creating feedback for product [product: {}]",
                feedbackCreate.getUser(),
                feedbackCreate.getProduct()
        );
        final FeedbackRead feedbackRead = feedbackService.create(feedbackCreate);
        final URI location = URI.create(String.format("/feedback/%s", feedbackRead.getId()));
        return ResponseEntity.created(location).build();
    }

    @GetMapping(path="/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<FeedbackRead> read(@PathVariable final String id) {
        log.info("Reading feedback [ID: {}]", id);
        return ResponseEntity.ok(feedbackService.read(id));
    }

    @PutMapping(path = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<FeedbackRead> update(
            @PathVariable final String id,
            @RequestBody @Valid final FeedbackUpdate feedbackUpdate) {
        log.info("Updating feedback [ID: {}]", id);
        return ResponseEntity.ok(feedbackService.update(id, feedbackUpdate));
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable String id) {
        log.info("Deleting feedback [ID: {}]", id);
        feedbackService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<FeedbackListRead> search(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "perPage", defaultValue = "10") int perPage,
            @RequestParam(value = "rating", required = false) Optional<Integer> rating,
            @RequestParam(value = "customer", required = false) Optional<String> user,
            @RequestParam(value = "product", required = false) Optional<String> product,
            @RequestParam(value = "vendor", required = false) Optional<String> vendor) {
        log.info("Searching recipes [page: {}] [perPage: {}] [rating: {}] [user: {}] [product: {}] [vendor: {}]",
            page,
            perPage,
            rating,
            user,
            product,
            vendor
        );
        return ResponseEntity.ok(feedbackService.search(page, perPage, rating, user, product, vendor));
    }
}
