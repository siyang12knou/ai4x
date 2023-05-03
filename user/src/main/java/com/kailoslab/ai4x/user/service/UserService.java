package com.kailoslab.ai4x.user.service;

import com.kailoslab.ai4x.user.data.GroupRepository;
import com.kailoslab.ai4x.user.data.UserRepository;
import com.kailoslab.ai4x.user.data.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public Optional<UserEntity> getUserById(String username) {
        return userRepository.findById(username);
    }
}
