package com.rutusoft.flowable.repository;

import com.rutusoft.flowable.entity.ApplicationDraft;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationDraftRepository extends JpaRepository<ApplicationDraft, Long> {

    Optional<ApplicationDraft> findByDraftId(String draftId);

    Optional<ApplicationDraft> findByUserIdAndStatus(String userId, String status);

    List<ApplicationDraft> findAllByUserIdAndStatus(String userId, String status);

}