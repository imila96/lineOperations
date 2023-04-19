package com.lineOperation.crud.controller;

import com.lineOperation.crud.entity.Line;
import com.lineOperation.crud.entity.LineDto;
import com.lineOperation.crud.exception.*;
import com.lineOperation.crud.service.LineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/lines")
public class LineController {

    @Autowired
    private LineService lineService;

    // create line
    @PostMapping
    public ResponseEntity<Object> createLine(@RequestBody Line line) {
        try {
            ResponseEntity<Object> response = lineService.createLine(line);
            return new ResponseEntity<>(response.getBody(), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // get line by id
    @GetMapping("/{lid}")
    public ResponseEntity<?> getLineByLId(@PathVariable(value = "lid") String lineId) {
        try {
            Line line = lineService.getLineByLId(lineId);
            return new ResponseEntity<>(line, HttpStatus.OK);
        } catch (ResourceNotFoundException | LineValidationException e) {
            String errorMessage = e.getMessage();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
    }


    // get all lines
    @GetMapping
    public List<LineDto> getAllLines() {
        return this.lineService.getAllLines();
    }


    //get all details with shift status

    @GetMapping("/shiftDetails")
    public ResponseEntity<List<Map<String, Object>>> getAllShiftDetails() {
        List<Map<String, Object>> details = lineService.getAllShiftDetails();
        return new ResponseEntity<>(details, HttpStatus.OK);
    }


    //get line details by shift
    @GetMapping("/shiftDetails/{shift}")
    public ResponseEntity<?> getAllDetailsByShift(@PathVariable String shift) {
        try {
            List<Map<String, Object>> details = lineService.getAllDetailsByShift(shift);
            return new ResponseEntity<>(details, HttpStatus.OK);
        } catch (ShiftDetailsNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    //Update Line
    @PutMapping("/{lid}")
    public ResponseEntity<String> updateLine(@RequestBody Line line, @PathVariable("lid") String lineId) throws Exception {
        return lineService.updateLine(line, lineId);
    }


    //change the shift status
    @PutMapping("/shift-status/{lid}/{shift}")
    public ResponseEntity<String> updateShiftStatus(@PathVariable String lid, @PathVariable String shift, @RequestBody Map<String, Object> request) throws LineNotFoundException, ShiftUpdateException {
        request.put("lid", lid);
        request.put("shift", shift);
        Object statusObj = request.get("status");
        if (statusObj == null) {
            return ResponseEntity.badRequest().body("Status field is missing");
        }
        if (!(statusObj instanceof Boolean)) {
            return ResponseEntity.badRequest().body("Status field must be a boolean");
        }
        boolean status = (Boolean) statusObj;

        return lineService.updateShiftStatus((String) request.get("lid"), (String) request.get("shift"), status);
    }


    //get number of working lines
    @GetMapping("/shift-active-status/{shift}")
    public ResponseEntity<?> getShiftStatusActiveCount(@PathVariable("shift") String shift) {
        try {
            Long count = lineService.getShiftStatusActiveCount(shift);
            return ResponseEntity.ok(count);
        } catch (InvalidShiftException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/shift-deactive-status/{shift}")
    public ResponseEntity<?> getShiftStatusDeActiveCount(@PathVariable("shift") String shift) {
        try {
            Long count = lineService.getShiftStatusDeActiveCount(shift);
            return ResponseEntity.ok(count);
        } catch (InvalidShiftException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }




    //delete line
    @DeleteMapping("/{lid}")
    public ResponseEntity<String> deleteLine(@PathVariable("lid") String lid) {
        ResponseEntity<String> response = lineService.deleteLine(lid);
        return response;
    }


}
