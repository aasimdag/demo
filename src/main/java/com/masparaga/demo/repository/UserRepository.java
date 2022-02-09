package com.masparaga.demo.repository;

import com.masparaga.demo.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String name);
    Boolean usernameExists(String username);
    Boolean emailExists(String email);
}
