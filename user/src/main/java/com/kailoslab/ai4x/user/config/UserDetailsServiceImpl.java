package com.kailoslab.ai4x.user.config;

import com.kailoslab.ai4x.commons.utils.Constants;
import com.kailoslab.ai4x.user.data.UserRepository;
import com.kailoslab.ai4x.user.data.dto.SessionInfoDto;
import com.kailoslab.ai4x.user.data.entity.UserEntity;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.*;

@Component
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final SessionInfoDto sessionInfoDto;

    @PostConstruct
    private void insertAdmin() {
        if(userRepository.count() == 0) {
            UserEntity system = new UserEntity(Constants.SYSTEM_ID, encoder.encode("kailoslab"), "kailoslab@gmail.com");
            userRepository.save(system);
            UserEntity admin = new UserEntity("admin", encoder.encode("kailoslab"), "kailoslab@gmail.com");
            userRepository.save(admin);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Optional<UserEntity> userEntityOptional = userRepository.findById(userId);
        if(userEntityOptional.isPresent()) {
            UserEntity userEntity = userEntityOptional.get();
            List<String> roles = Collections.singletonList("ADMIN");
            Set<GrantedAuthority> ga = new HashSet<>();
            for(String role:roles) {
                ga.add(new SimpleGrantedAuthority(role));
            }

            sessionInfoDto.setId(userId);
            sessionInfoDto.setEmail(userEntity.getEmail());
            sessionInfoDto.setRoles(roles);
            return new User(userId, userEntity.getPassword(), ga);
        } else {
            throw new UsernameNotFoundException("Cannot find a admin ["+userId+"]");
        }
    }
}