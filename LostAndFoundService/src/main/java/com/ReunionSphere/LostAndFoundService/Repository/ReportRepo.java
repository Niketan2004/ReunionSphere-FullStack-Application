package com.ReunionSphere.LostAndFoundService.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.ReunionSphere.LostAndFoundService.Entity.Report;

public interface ReportRepo extends MongoRepository<Report, String> {
     Page<Report> findByReporterId(String reporterId, Pageable pageable);
}
