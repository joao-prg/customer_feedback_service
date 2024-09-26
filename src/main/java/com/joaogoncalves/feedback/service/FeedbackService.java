package com.joaogoncalves.feedback.service;

import com.joaogoncalves.feedback.entity.Feedback;
import com.joaogoncalves.feedback.exception.FeedbackNotFoundException;
import com.joaogoncalves.feedback.exception.InvalidPageException;
import com.joaogoncalves.feedback.model.FeedbackCreate;
import com.joaogoncalves.feedback.model.FeedbackListRead;
import com.joaogoncalves.feedback.model.FeedbackRead;
import com.joaogoncalves.feedback.model.FeedbackUpdate;
import com.joaogoncalves.feedback.repository.FeedbackRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private ModelMapper modelMapper;

    private Feedback find(final UUID id) {
        final Feedback recipe = feedbackRepository
                .findById(id)
                .orElseThrow(() -> new FeedbackNotFoundException(
                        String.format("Feedback not found! [Id: %s]", id)
                ));
        log.debug("Feedback retrieved successfully [ID: {}]", id);
        return recipe;
    }

    public FeedbackRead create(final FeedbackCreate feedbackCreate) {
        final Feedback feedbackToCreate = modelMapper.map(feedbackCreate, Feedback.class);
        final Feedback savedFeedback = feedbackRepository.save(feedbackToCreate);
        log.debug("Feedback saved successfully [ID: {}]", savedFeedback.getId());
        return modelMapper.map(savedFeedback, FeedbackRead.class);
    }

    public FeedbackRead read(final UUID id) {
        final Feedback recipe = find(id);
        return modelMapper.map(recipe, FeedbackRead.class);
    }

    public FeedbackRead update(final UUID id, final FeedbackUpdate feedbackUpdate) {
        final Feedback feedback = find(id);
        modelMapper.map(feedbackUpdate, feedback);
        final Feedback updatedFeeback = feedbackRepository.save(feedback);
        log.debug("Feedback updated successfully [ID: {}]", id);
        return modelMapper.map(updatedFeeback, FeedbackRead.class);
    }

    public void delete(final UUID id) {
        final Feedback feedback = find(id);
        feedbackRepository.delete(feedback);
        log.debug(String.format("Recipe deleted successfully [ID: %s]", id));
    }

    public FeedbackListRead search(final int page,
                                   final int perPage,
                                   final Optional<Integer> rating,
                                   final Optional<String> customer,
                                   final Optional<String> product,
                                   final Optional<String> vendor) {
        if (page < 1) {
            throw new InvalidPageException(String.format("Invalid page [number: %d]", page));
        }
        if (perPage < 5 || perPage > 20) {
            throw new InvalidPageException(String.format("Invalid per page [number: %d]", perPage));
        }
        final Pageable pageable = PageRequest.of(page - 1, perPage, Sort.by(Sort.Direction.DESC, "_id"));
        final Page<Feedback> feedbackPage = feedbackRepository.findByFilters(rating, customer, product, vendor, pageable);
        final List<FeedbackRead> feedbacks = feedbackPage
            .getContent()
            .stream()
            .map(feedback -> modelMapper.map(feedback, FeedbackRead.class))
            .collect(Collectors.toList());
        final FeedbackListRead feedbackList = new FeedbackListRead(
            feedbackPage.getTotalElements(),
            feedbackPage.isFirst(),
            feedbackPage.isLast(),
            feedbacks
        );
        log.info(
            "Found {} feedbacks for [page: {}] [perPage: {}] [rating: {}] [customer: {}] [product: {}] [vendor: {}]",
            feedbacks.size(),
            page,
            perPage,
            rating,
            customer,
            product,
            vendor
        );
        return feedbackList;
    }
}
