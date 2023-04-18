package com.lineOperation.crud.controller;

import com.lineOperation.crud.entity.Line;
import com.lineOperation.crud.entity.ShiftA;
import com.lineOperation.crud.exception.ResourceNotFoundException;
import com.lineOperation.crud.service.LineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/lines")
public class LineController {

    @Autowired
    private LineService lineService;

    // create line
    @PostMapping
    public ResponseEntity<Line> createLine(@RequestBody Line line) {
        try {
            Line savedLine = lineService.createLine(line);
            return new ResponseEntity<>(savedLine, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // get line by id
    @GetMapping("/{id}")
    public ResponseEntity<Line> getLineById(@PathVariable(value = "id") long lineId) {
        try {
            Line line = lineService.getLineById(lineId);
            return new ResponseEntity<>(line, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // get all lines
    @GetMapping
    public List<Line> getAllLines() {
        return this.lineService.getAllLines();
    }

    //gift al details with shift status

    @GetMapping("/shiftDetails")
    public ResponseEntity<List<Map<String, Object>>> getAllShiftDetails() {
        List<Map<String, Object>> details = lineService.getAllShiftDetails();
        return new ResponseEntity<>(details, HttpStatus.OK);
    }


    //get line details by shift
    @GetMapping("/shiftDetails/{shift}")
    public ResponseEntity<List<Map<String, Object>>> getAllDetailsByShift(@PathVariable String shift) {
        List<Map<String, Object>> details = lineService.getAllDetailsByShift(shift);
        return new ResponseEntity<>(details, HttpStatus.OK);
    }



    //Update Line
    @PutMapping("/{id}")
    public Line updateLine(@RequestBody Line line, @PathVariable("id") long lineId) throws Exception {
        return lineService.updateLine(line, lineId);
    }


    //change the shift status
    @PutMapping("/shift-status/{lineId}/{shift}")
    public void updateShiftStatus(@PathVariable Long lineId, @PathVariable String shift, @RequestBody Map<String, String> request) {
        request.put("lineId", String.valueOf(lineId));
        request.put("shift", shift);
        lineService.updateShiftStatus(request);
    }

//get number of working lines
    @GetMapping("/shift-active-status/{shift}")
    public ResponseEntity<Long> getShiftStatusActiveCount(@PathVariable("shift") String shift) {
        Long count = lineService.getShiftStatusActiveCount(shift);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/shift-deactive-status/{shift}")
    public ResponseEntity<Long> getShiftStatusDeActiveCount(@PathVariable("shift") String shift) {
        Long count = lineService.getShiftStatusDeActiveCount(shift);
        return ResponseEntity.ok(count);
    }

    //delete line
    @DeleteMapping("/{id}")
    public ResponseEntity<Line> deleteLine(@PathVariable("id") long lineId) throws Exception {
        lineService.deleteLine(lineId);
        return ResponseEntity.ok().build();
    }
}
