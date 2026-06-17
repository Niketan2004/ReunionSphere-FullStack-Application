package com.ReunionSphere.User_Service.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ReunionSphere.User_Service.Entities.Location;

public interface LocationRepo extends JpaRepository<Location,String> {
     
}
