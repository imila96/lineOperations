package com.lineOperation.crud.controller;

import com.lineOperation.crud.entity.Line;
import com.lineOperation.crud.exception.ResourceNotFoundException;
import com.lineOperation.crud.repository.LineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/lines")
public class LineController {

    @Autowired
    private LineRepository lineRepository;

    // get all lines
    @GetMapping
    public List<Line> getAllLines() {
        return this.lineRepository.findAll();
    }

    // get user by id
    @GetMapping("/{id}")
    public Line getLineById(@PathVariable(value = "id") long lineId) {
        return this.lineRepository.findById(lineId)
                .orElseThrow(() -> new ResourceNotFoundException("Line not found with id :" + lineId));
    }

    // create line
    @PostMapping
    public Line createLine(@RequestBody Line line) {
        return this.lineRepository.save(line);
    }

    // update line
    @PutMapping("/{id}")
    public Line updateLine(@RequestBody Line line, @PathVariable("id") long lineId) {
        Line existingLine = this.lineRepository.findById(lineId)
                .orElseThrow(() -> new ResourceNotFoundException("Line not found with id :" + lineId));
        existingLine.setTeamLeader(line.getTeamLeader());
        existingLine.setShift(line.getShift());

        return this.lineRepository.save(existingLine);
    }

    @PutMapping("/{id}/team-leader")
    public Line updateTeamLeader(@RequestBody Line line, @PathVariable("id") long lineId) {
        Line existingLine = this.lineRepository.findById(lineId)
                .orElseThrow(() -> new ResourceNotFoundException("Line not found with id :" + lineId));
        existingLine.setTeamLeader(line.getTeamLeader());
        return this.lineRepository.save(existingLine);
    }

    @PutMapping("/{id}/shift")
    public Line updateShift(@RequestBody Line line, @PathVariable("id") long lineId) {
        Line existingLine = this.lineRepository.findById(lineId)
                .orElseThrow(() -> new ResourceNotFoundException("Line not found with id :" + lineId));
        existingLine.setShift(line.getShift());
        return this.lineRepository.save(existingLine);
    }

    // delete line by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Line> deleteLine(@PathVariable("id") long lineId) {
        Line existingLine = this.lineRepository.findById(lineId)
                .orElseThrow(() -> new ResourceNotFoundException("line not found with id :" + lineId));
        this.lineRepository.delete(existingLine);
        return ResponseEntity.ok().build();
    }
}