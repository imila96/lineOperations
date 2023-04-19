package com.lineOperation.crud.repository;

//import com.lineOperation.crud.entity.Line;

import com.lineOperation.crud.entity.Line;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LineRepository extends JpaRepository<Line, Long> {

    Line findByLid(String lid);

    @Query("SELECT MAX(l.lid) FROM Line l WHERE l.lid LIKE 'DS%'")
    String findMaxLid();


}
