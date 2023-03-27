package com.lineOperation.crud.repository;

//import com.lineOperation.crud.entity.Line;
import com.lineOperation.crud.entity.Line;
//import com.lineOperation.crud.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LineRepository extends JpaRepository<Line, Long> {

}
