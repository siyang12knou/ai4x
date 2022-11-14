package com.kailoslab.ai4x.user.data;

import com.kailoslab.ai4x.user.data.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, String> {

}
