package com.kailoslab.ai4x.user.data;

import com.kailoslab.ai4x.user.data.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, String> {

}
