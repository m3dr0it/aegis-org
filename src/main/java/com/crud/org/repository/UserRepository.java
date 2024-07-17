package com.crud.org.repository;

import com.crud.org.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<User, UUID> {
    @Query("select u from User u where u.email = ?1")
    User findByEmail(String email);
}
