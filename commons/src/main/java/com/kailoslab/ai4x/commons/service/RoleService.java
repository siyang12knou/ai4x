package com.kailoslab.ai4x.commons.service;

import com.kailoslab.ai4x.commons.data.RoleRepository;
import com.kailoslab.ai4x.commons.data.entity.RoleEntity;
import com.kailoslab.ai4x.commons.exception.Ai4xException;
import com.kailoslab.ai4x.commons.exception.Ai4xExceptionMessage;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public void save(String id, String name) {
        Optional<RoleEntity> roleEntityOptional = roleRepository.findById(id);
        RoleEntity role = roleEntityOptional.orElse(new RoleEntity(id, name));
        if(role.getDeleted()) {
            throw new Ai4xException(String.format(Ai4xExceptionMessage.E000002, "Role", id));
        } else {
            roleRepository.save(role);
        }
    }

    public void delete(String id) {
        Optional<RoleEntity> roleEntityOptional = roleRepository.findById(id);
        if(roleEntityOptional.isPresent()) {
            RoleEntity role = roleEntityOptional.get();
            if(role.getDeleted()) {
                throw new Ai4xException(String.format(Ai4xExceptionMessage.E000002, "Role", id));
            } else {
                role.setDeleted(true);
                roleRepository.save(role);
            }
        } else {
            throw new Ai4xException(String.format(Ai4xExceptionMessage.E000010, "Role", id));
        }
    }
}
