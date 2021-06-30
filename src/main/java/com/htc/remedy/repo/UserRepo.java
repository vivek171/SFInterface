package com.htc.remedy.repo;

import com.htc.remedy.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by poovarasanv on 3/10/17.
 * Project : remedy-web-services
 */
@Repository
public interface UserRepo extends JpaRepository<UserModel,Integer> {
    UserModel findByUserName(String username);
}
