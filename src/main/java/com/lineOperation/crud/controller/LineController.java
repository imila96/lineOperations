package com.lineOperation.crud.controller;

import java.util.List;

import com.lineOperation.crud.entity.Line;
import com.lineOperation.crud.exception.ResourceNotFoundException;
import com.lineOperation.crud.repository.LineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/lines")
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
    public Line getLineById(@PathVariable (value = "id") long lineId) {
        return this.lineRepository.findById(lineId)
                .orElseThrow(() -> new ResourceNotFoundException("Line not found with id :" + lineId));
    }

    // create line
    @PostMapping
    public Line createLine(@RequestBody Line line) {
        return this.lineRepository.save(line);
    }

    // update line
    @PutMapping("UpdateAll/{id}")
    public Line updateLine(@RequestBody Line line, @PathVariable ("id") long lineId) {
        Line existingLine = this.lineRepository.findById(lineId)
                .orElseThrow(() -> new ResourceNotFoundException("Line not found with id :" + lineId));
        existingLine.setTeam_Leader(line.getTeam_Leader());
        existingLine.setShift_Status(line.getShift_Status());

        return this.lineRepository.save(existingLine);
    }

    @PutMapping("UpdateTeamLeader/{id}")
    public Line updateTeamLeader(@RequestBody Line line, @PathVariable ("id") long lineId) {
        Line existingLine = this.lineRepository.findById(lineId)
                .orElseThrow(() -> new ResourceNotFoundException("Line not found with id :" + lineId));
        existingLine.setTeam_Leader(line.getTeam_Leader());
        return this.lineRepository.save(existingLine);
    }

    @PutMapping("UpdateShift/{id}")
    public Line updateShift(@RequestBody Line line, @PathVariable ("id") long lineId) {
        Line existingLine = this.lineRepository.findById(lineId)
                .orElseThrow(() -> new ResourceNotFoundException("Line not found with id :" + lineId));
        existingLine.setShift_Status(line.getShift_Status());
        return this.lineRepository.save(existingLine);
    }

    // delete line by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Line> deleteLine(@PathVariable ("id") long lineId){
        Line existingLine = this.lineRepository.findById(lineId)
                .orElseThrow(() -> new ResourceNotFoundException("line not found with id :" + lineId));
        this.lineRepository.delete(existingLine);
        return ResponseEntity.ok().build();
    }
}