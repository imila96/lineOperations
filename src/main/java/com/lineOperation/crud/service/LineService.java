package com.lineOperation.crud.service;

import com.lineOperation.crud.entity.Line;
import com.lineOperation.crud.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Map;

public interface LineService {
    Line createLine(Line line) throws Exception;

    Line getLineById(long id) throws ResourceNotFoundException;

    List<Line> getAllLines();

    Line updateLine(Line line, long lineId) throws Exception;

    void updateShiftStatus(Map<String, String> request);

    Long getShiftStatusCount(String shift);

    void deleteLine(long lineId) throws Exception;
}

