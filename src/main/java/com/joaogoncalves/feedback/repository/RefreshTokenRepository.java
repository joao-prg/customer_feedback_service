package com.joaogoncalves.feedback.repository;

import com.joaogoncalves.feedback.entity.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> { }
