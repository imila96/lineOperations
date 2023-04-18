package com.lineOperation.crud.repository;

import com.lineOperation.crud.entity.Line;
import com.lineOperation.crud.entity.ShiftB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftBRepository extends JpaRepository<ShiftB, Long> {

    ShiftB findByLine(Line line);

    ShiftB findByLineId(Long lineId);


    Long countByShiftStatus(String shiftStatus);
    void deleteByLine(Line line);
}