package dev.interview.server.user.repository;

import dev.interview.server.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

// 사용자 데이터베이스에 접근하는 Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // 이메일로 사용자 조회
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
