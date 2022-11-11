package com.kailoslab.ai4x.commons.data;

import com.kailoslab.ai4x.commons.data.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, String> {

}
