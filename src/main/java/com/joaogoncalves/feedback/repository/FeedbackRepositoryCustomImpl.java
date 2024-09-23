package com.joaogoncalves.feedback.repository;

import com.joaogoncalves.feedback.entity.Feedback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FeedbackRepositoryCustomImpl implements FeedbackRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<Feedback> findByFilters(Optional<Integer> rating,
                                        Optional<String> customer,
                                        Optional<String> product,
                                        Optional<String> vendor,
                                        Pageable pageable) {
        Query query = new Query();
        rating.ifPresent(r -> query.addCriteria(Criteria.where("rating").is(r)));
        customer.ifPresent(c -> query.addCriteria(Criteria.where("customer").regex(c, "i")));
        product.ifPresent(p -> query.addCriteria(Criteria.where("product").regex(p, "i")));
        vendor.ifPresent(v -> query.addCriteria(Criteria.where("vendor").regex(v, "i")));
        long totalDocuments = mongoTemplate.count(query, Feedback.class);
        query.with(pageable);
        List<Feedback> feedbackList = mongoTemplate.find(query, Feedback.class);
        return new PageImpl<>(feedbackList, pageable, totalDocuments);
    }
}