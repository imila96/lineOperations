package com.lineOperation.crud.controller;

import com.lineOperation.crud.entity.Line;
import com.lineOperation.crud.entity.ShiftA;
import com.lineOperation.crud.entity.ShiftB;
import com.lineOperation.crud.exception.ResourceNotFoundException;
import com.lineOperation.crud.repository.LineRepository;
import com.lineOperation.crud.repository.ShiftARepository;
import com.lineOperation.crud.repository.ShiftBRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@RestController
@RequestMapping("/api/v1/lines")
public class LineController {

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private ShiftARepository shiftARepository;

    @Autowired
    private ShiftBRepository shiftBRepository;

    // create line
    @PostMapping
    public Line createLine(@RequestBody Line line) throws Exception {

        if (Objects.isNull(line.getId()))
            throw new Exception("Enter Line ID");

        if (line.getShiftAteamLeader() == null)
            throw new Exception("Enter team leader for Shift A!");

        if (line.getShiftBteamLeader() == null)
            throw new Exception("Enter team leader for Shift B!");

        Line savedLine = this.lineRepository.save(line);

        ShiftA shiftA = new ShiftA();
        shiftA.setLine(savedLine);
        shiftA.setTeamLeader(savedLine.getShiftAteamLeader());
        this.shiftARepository.save(shiftA);

        ShiftB shiftB = new ShiftB();
        shiftB.setLine(savedLine);
        shiftB.setTeamLeader(savedLine.getShiftBteamLeader());
        this.shiftBRepository.save(shiftB);

        return savedLine;
    }

    // get user by id
    @GetMapping("/{id}")
    public Line getLineById(@PathVariable(value = "id") long lineId) throws Exception {

        if(Objects.isNull(lineId))
            throw new Exception("Enter Line ID");

        return this.lineRepository.findById(lineId)
                .orElseThrow(() -> new ResourceNotFoundException("Line not found with id :" + lineId));
    }

    // get all lines
    @GetMapping
    public List<Line> getAllLines() {
        return this.lineRepository.findAll();
    }

    // update line
    @PutMapping("/{id}")
    public Line updateLine(@RequestBody Line line, @PathVariable("id") long lineId) throws Exception {

        if (Objects.isNull(lineId))
            throw new Exception("Enter Line ID");

        if (line.getShiftAteamLeader() == null)
            throw new Exception("Enter team leader for Shift A!");

        if (line.getShiftBteamLeader() == null)
            throw new Exception("Enter team leader for Shift B!");

        Line existingLine = this.lineRepository.findById(lineId)
                .orElseThrow(() -> new ResourceNotFoundException("Line not found with id :" + lineId));

        existingLine.setShiftAteamLeader(line.getShiftAteamLeader());
        existingLine.setShiftBteamLeader(line.getShiftBteamLeader());

        this.lineRepository.save(existingLine);

        // update shift A
        ShiftA shiftA = this.shiftARepository.findByLine(existingLine);
        if (shiftA != null) {
            shiftA.setTeamLeader(existingLine.getShiftAteamLeader());
        } else {
            shiftA = new ShiftA();
            shiftA.setLine(existingLine);
            shiftA.setTeamLeader(existingLine.getShiftAteamLeader());
        }
        this.shiftARepository.save(shiftA);

// update shift B
        ShiftB shiftB = this.shiftBRepository.findByLine(existingLine);
        if (shiftB != null) {
            shiftB.setTeamLeader(existingLine.getShiftBteamLeader());
        } else {
            shiftB = new ShiftB();
            shiftB.setLine(existingLine);
            shiftB.setTeamLeader(existingLine.getShiftBteamLeader());
        }
        this.shiftBRepository.save(shiftB);
        return existingLine;
    }


//change shift status according to the shift
    @PutMapping("/shift-status")
    public void updateShiftStatus(@RequestBody Map<String, String> request) {
        Long lineId = Long.parseLong(request.get("lineId"));
        String shift = request.get("shift");
        String status = request.get("status");
        if (shift.equals("ShiftA")) {
            ShiftA shiftA = shiftARepository.findByLineId(lineId);
            shiftA.setShiftStatus(status);
            shiftARepository.save(shiftA);
        } else if (shift.equals("ShiftB")) {
            ShiftB shiftB = shiftBRepository.findByLineId(lineId);
            shiftB.setShiftStatus(status);
            shiftBRepository.save(shiftB);
        } else {
            throw new IllegalArgumentException("Invalid shift: " + shift);
        }
    }

//get how many lines are working
    @GetMapping("/shift-status/{shift}")
    public ResponseEntity<Long> getShiftStatusCount(@PathVariable("shift") String shift) {
        Long count = 0L;
        if (shift.equals("ShiftA")) {
            count = shiftARepository.countByShiftStatus("on");
        } else if (shift.equals("ShiftB")) {
            count = shiftBRepository.countByShiftStatus("on");
        } else {
            throw new IllegalArgumentException("Invalid shift: " + shift);
        }
        return ResponseEntity.ok(count);
    }






    // delete line by id
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Line> deleteLine(@PathVariable("id") long lineId) throws Exception {

        if (Objects.isNull(lineId))
            throw new Exception("Enter Line ID");

        Line existingLine = this.lineRepository.findById(lineId)
                .orElseThrow(() -> new ResourceNotFoundException("line not found with id :" + lineId));

        // delete corresponding records from ShiftA and ShiftB tables
        this.shiftARepository.deleteByLine(existingLine);
        this.shiftBRepository.deleteByLine(existingLine);

        this.lineRepository.delete(existingLine);
        return ResponseEntity.ok().build();
    }

}
