package com.lineOperation.crud.repository;

import com.lineOperation.crud.entity.Line;
import com.lineOperation.crud.entity.ShiftA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftARepository extends JpaRepository<ShiftA, Long> {

    ShiftA findByLine(Line line);

    ShiftA findByLineId(Long lineId);

    ShiftA findByLineLid(String lid); // updated method signature

    Long countByShiftStatus(boolean shiftStatus);

    Line findByLid(String lid);

    void deleteByLine(Line line);
}