package org.aionys.notes.service.users;

import org.aionys.notes.persistence.model.entity.User;

import java.util.Optional;

public interface UserService {
    User save(User user);
    Optional<User> findByUsername(String username);
    void deleteById(Long id);
}
