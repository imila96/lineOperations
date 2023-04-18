package com.lineOperation.crud.controller;

import com.lineOperation.crud.entity.Line;
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

    //Update Line
    @PutMapping("/{id}")
    public Line updateLine(@RequestBody Line line, @PathVariable("id") long lineId) throws Exception {
        return lineService.updateLine(line, lineId);
    }

    @PutMapping("/shift-status/{lineId}/{shift}")
    public void updateShiftStatus(@PathVariable Long lineId, @PathVariable String shift, @RequestBody Map<String, String> request) {
        request.put("lineId", String.valueOf(lineId));
        request.put("shift", shift);
        lineService.updateShiftStatus(request);
    }


    @GetMapping("/shift-status/{shift}")
    public ResponseEntity<Long> getShiftStatusCount(@PathVariable("shift") String shift) {
        Long count = lineService.getShiftStatusCount(shift);
        return ResponseEntity.ok(count);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Line> deleteLine(@PathVariable("id") long lineId) throws Exception {
        lineService.deleteLine(lineId);
        return ResponseEntity.ok().build();
    }
}
