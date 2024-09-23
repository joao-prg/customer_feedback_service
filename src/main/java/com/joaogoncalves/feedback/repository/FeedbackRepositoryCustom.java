package com.joaogoncalves.feedback.repository;

import com.joaogoncalves.feedback.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface FeedbackRepositoryCustom {
    Page<Feedback> findByFilters(Optional<Integer> rating,
                                 Optional<String> customer,
                                 Optional<String> product,
                                 Optional<String> vendor,
                                 Pageable pageable);
}
