package com.lineOperation.crud.service;

import com.lineOperation.crud.entity.Line;
import com.lineOperation.crud.entity.LineDto;
import com.lineOperation.crud.exception.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface LineService {
    public ResponseEntity<Object> createLine(Line line) throws LineValidationException, LineSaveException;

    Line getLineByLId(String lid) throws ResourceNotFoundException, LineValidationException;


    List<LineDto> getAllLines();

    ResponseEntity<String> updateLine(Line line, String lineId);

    ResponseEntity<String> updateShiftStatus(String lineId, String shift, boolean status);


    List<Map<String, Object>> getAllDetailsByShift(String shift);

    List<Map<String, Object>> getAllShiftDetails();

    public Long getShiftStatusActiveCount(String shift) throws InvalidShiftException;


    Long getShiftStatusDeActiveCount(String shift) throws InvalidShiftException;;

    ResponseEntity<String> deleteLine(String lid);


}

