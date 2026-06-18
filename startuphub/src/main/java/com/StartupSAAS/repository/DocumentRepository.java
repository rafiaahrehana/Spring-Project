package com.StartupSAAS.repository;

import com.StartupSAAS.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Query("""
        SELECT d FROM Document d
        LEFT JOIN FETCH d.serviceRequest
        LEFT JOIN FETCH d.uploadedBy
        LEFT JOIN FETCH d.company
        WHERE d.id = :id
    """)
    Optional<Document> findByIdWithDetails(@Param("id") Long id);

    List<Document> findByServiceRequestId(Long serviceRequestId);

    List<Document> findByCompanyId(Long companyId);
}
