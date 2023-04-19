package com.lineOperation.crud.service;

import com.lineOperation.crud.entity.Line;
import com.lineOperation.crud.entity.LineDto;
import com.lineOperation.crud.exception.*;

import java.util.List;
import java.util.Map;

public interface LineService {
    Line createLine(Line line) throws LineValidationException, LineSaveException;

    Line getLineByLId(String lid) throws ResourceNotFoundException, LineValidationException;


    List<LineDto> getAllLines();

    Line updateLine(Line line, String lineId) throws Exception;

    boolean updateShiftStatus(String lineId, String shift, boolean status) throws LineNotFoundException, ShiftUpdateException;

    List<Map<String, Object>> getAllDetailsByShift(String shift);

    List<Map<String, Object>> getAllShiftDetails();

    public Long getShiftStatusActiveCount(String shift) throws InvalidShiftException;


    Long getShiftStatusDeActiveCount(String shift) throws InvalidShiftException;;

    void deleteLine(String lid) throws Exception;
}

