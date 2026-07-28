package com.example.order_service.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderMongoRepository extends MongoRepository<OrderDocument, String> {

    List<OrderDocument> findByUserId(String userId);

}
