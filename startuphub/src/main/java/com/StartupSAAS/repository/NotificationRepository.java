package com.StartupSAAS.repository;

import com.StartupSAAS.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("""
        SELECT n FROM Notification n
        LEFT JOIN FETCH n.recipient
        LEFT JOIN FETCH n.serviceRequest
        WHERE n.recipient.id = :userId
        ORDER BY n.createdAt DESC
    """)
    List<Notification> findByRecipientId(@Param("userId") Long userId);

    List<Notification> findByRecipientIdAndIsReadFalse(Long userId);

    long countByRecipientIdAndIsReadFalse(Long userId);
}
