package com.lineOperation.crud.repository;

import com.lineOperation.crud.entity.Line;
import com.lineOperation.crud.entity.ShiftA;
import com.lineOperation.crud.entity.ShiftB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftBRepository extends JpaRepository<ShiftB, Long> {

    ShiftB findByLine(Line line);

    ShiftB findByLineId(Long lineId);

    ShiftB findByLineLid(String lid); // updated method signature

    Long countByShiftStatus(boolean shiftStatus);

    Line findByLid(String lid);

    void deleteByLine(Line line);
}