package com.samhcoco.healthapp.core.repository;

import com.samhcoco.healthapp.core.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    User findById(long patientId);

}
