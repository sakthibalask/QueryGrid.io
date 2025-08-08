package com.application.QueryGrid.io.Repository;

import com.application.QueryGrid.io.Entity.Group.Groups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface GroupRepository extends JpaRepository<Groups, String> {

}
