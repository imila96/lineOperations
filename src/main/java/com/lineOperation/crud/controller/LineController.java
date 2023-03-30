package com.lineOperation.crud.controller;

import com.lineOperation.crud.entity.Line;
import com.lineOperation.crud.exception.ResourceNotFoundException;
import com.lineOperation.crud.repository.LineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;


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
    public Line getLineById(@PathVariable(value = "id") long lineId) throws Exception {

        if(Objects.isNull(lineId))
            throw new Exception("Enter Line ID");

        return this.lineRepository.findById(lineId)
                .orElseThrow(() -> new ResourceNotFoundException("Line not found with id :" + lineId));
    }

    // create line
    @PostMapping
    public Line createLine(@RequestBody Line line) throws Exception {

        if(Objects.isNull(line.getId()))
            throw new Exception("Enter Line ID");

        if(line.getTeamLeader() == null)
            throw new Exception("Enter team leader!");
//            throw new

        if(line.getShift() == null)
            throw new Exception("Enter shift!");


        return this.lineRepository.save(line);


    }

    // update line
    @PutMapping("/{id}")
    public Line updateLine(@RequestBody Line line, @PathVariable("id") long lineId) throws Exception {


       /* if(line.getId() == null)
            throw new Exception("Enter team leader!");
*/
        if(Objects.isNull(lineId))
            throw new Exception("Enter Line ID");

        if(line.getTeamLeader() == null)
            throw new Exception("Enter team leader!");

        if(line.getShift() == null)
            throw new Exception("Enter shift!");

        Line existingLine = this.lineRepository.findById(lineId)
                .orElseThrow(() -> new ResourceNotFoundException("Line not found with id :" + lineId));
        existingLine.setTeamLeader(line.getTeamLeader());
        existingLine.setShift(line.getShift());



        return this.lineRepository.save(existingLine);
    }

    @PutMapping("/{id}/team-leader")
    public Line updateTeamLeader(@RequestBody Line line, @PathVariable("id") long lineId) throws Exception {

        if(Objects.isNull(lineId))
            throw new Exception("Enter Line ID");

        if(line.getTeamLeader() == null)
            throw new Exception("Enter team leader!");



        Line existingLine = this.lineRepository.findById(lineId)
                .orElseThrow(() -> new ResourceNotFoundException("Line not found with id :" + lineId));
        existingLine.setTeamLeader(line.getTeamLeader());
        return this.lineRepository.save(existingLine);
    }

    @PutMapping("/{id}/shift")
    public Line updateShift(@RequestBody Line line, @PathVariable("id") long lineId) throws Exception {

        if(Objects.isNull(lineId))
            throw new Exception("Enter Line ID");

        if(line.getShift() == null)
            throw new Exception("Enter shift!");

        Line existingLine = this.lineRepository.findById(lineId)
                .orElseThrow(() -> new ResourceNotFoundException("Line not found with id :" + lineId));
        existingLine.setShift(line.getShift());
        return this.lineRepository.save(existingLine);
    }

    // delete line by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Line> deleteLine(@PathVariable("id") long lineId) throws Exception {

        if(Objects.isNull(lineId))
            throw new Exception("Enter Line ID");

        Line existingLine = this.lineRepository.findById(lineId)
                .orElseThrow(() -> new ResourceNotFoundException("line not found with id :" + lineId));
        this.lineRepository.delete(existingLine);
        return ResponseEntity.ok().build();
    }
}