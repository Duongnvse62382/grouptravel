package com.fpt.gta.model.repository;

import com.fpt.gta.model.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository  extends JpaRepository<Group,Integer> {

}
