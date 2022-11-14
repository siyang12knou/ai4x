package com.kailoslab.ai4x.user.service;

import com.kailoslab.ai4x.user.data.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;


}
