package com.joaogoncalves.feedback.repository;

import com.joaogoncalves.feedback.entity.Feedback;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface FeedbackRepository extends MongoRepository<Feedback, UUID>, FeedbackRepositoryCustom { }

