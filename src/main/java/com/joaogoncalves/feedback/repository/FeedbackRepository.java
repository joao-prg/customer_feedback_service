package com.joaogoncalves.feedback.repository;

import com.joaogoncalves.feedback.entity.Feedback;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FeedbackRepository extends MongoRepository<Feedback, String>, FeedbackRepositoryCustom { }

