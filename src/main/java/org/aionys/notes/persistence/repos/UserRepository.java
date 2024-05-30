package org.aionys.notes.persistence.repos;

import org.aionys.notes.persistence.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
