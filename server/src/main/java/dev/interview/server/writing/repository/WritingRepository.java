package dev.interview.server.writing.repository;

import dev.interview.server.writing.domain.Writing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

// 사용자가 작성한 글 Repository
public interface WritingRepository extends JpaRepository<Writing, UUID> {

    // 사용자 글 목록 조회
    List<Writing> findAllByUserId(UUID userId);
}
