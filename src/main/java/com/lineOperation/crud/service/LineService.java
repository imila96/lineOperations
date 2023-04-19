package com.lineOperation.crud.service;

import com.lineOperation.crud.entity.Line;
import com.lineOperation.crud.entity.LineDto;
import com.lineOperation.crud.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Map;

public interface LineService {
    Line createLine(Line line) throws Exception;

    Line getLineByLId(String lid) throws ResourceNotFoundException;

    List<Line> getAllLines();

    List<LineDto> getAllLines2();

    Line updateLine(Line line, String lineId) throws Exception;

    boolean updateShiftStatus(String lineId, String shift, boolean status);

    List<Map<String, Object>> getAllDetailsByShift(String shift);

    List<Map<String, Object>> getAllShiftDetails();

    Long getShiftStatusActiveCount(String shift);

    Long getShiftStatusDeActiveCount(String shift);

    void deleteLine(String lid) throws Exception;
}

