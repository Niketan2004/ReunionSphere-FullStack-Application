package com.ReuinonSphere.LostAndFoundService.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.ReuinonSphere.LostAndFoundService.Entity.Report;

public interface ReportRepo extends MongoRepository<Report, String> {
     
}
