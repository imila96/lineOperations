package com.lineOperation.crud.service;

import com.lineOperation.crud.entity.Line;
import com.lineOperation.crud.entity.ShiftA;
import com.lineOperation.crud.entity.ShiftB;
import com.lineOperation.crud.exception.ResourceNotFoundException;
import com.lineOperation.crud.repository.LineRepository;
import com.lineOperation.crud.repository.ShiftARepository;
import com.lineOperation.crud.repository.ShiftBRepository;
import com.lineOperation.crud.service.LineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class LineServiceImpl implements LineService {

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private ShiftARepository shiftARepository;

    @Autowired
    private ShiftBRepository shiftBRepository;


    //create a line
    @Override
    @Transactional
    public Line createLine(Line line) throws Exception {

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


    //get details by lineid
    @Override
    public Line getLineById(long id) throws ResourceNotFoundException {
        return this.lineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Line not found with id :" + id));
    }

    @Override
    public List<Line> getAllLines() {
        return this.lineRepository.findAll();
    }



//update line details-teamleaders

    @Override
    public Line updateLine(Line line, long lineId) throws Exception {
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

        ShiftA shiftA = this.shiftARepository.findByLine(existingLine);
        if (shiftA != null) {
            shiftA.setTeamLeader(existingLine.getShiftAteamLeader());
        } else {
            shiftA = new ShiftA();
            shiftA.setLine(existingLine);
            shiftA.setTeamLeader(existingLine.getShiftAteamLeader());
        }
        this.shiftARepository.save(shiftA);

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


    //update ShiftStatus
    @Override
    public void updateShiftStatus(Map<String, String> request) {
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
    //return all details (shift A+Shift B)
    @Override
    public List<Map<String, Object>> getAllShiftDetails() {
        List<Map<String, Object>> result = new ArrayList<>();
        List<ShiftA> shiftAList = shiftARepository.findAll();
        for (ShiftA shiftA : shiftAList) {
            Map<String, Object> details = new HashMap<>();
            details.put("teamLeader", shiftA.getTeamLeader());
            details.put("lineId", shiftA.getLine().getId());
            details.put("shiftStatus", shiftA.getShiftStatus());
            details.put("shift", "ShiftA");
            result.add(details);
        }

        List<ShiftB> shiftBList = shiftBRepository.findAll();
        for (ShiftB shiftB : shiftBList) {
            Map<String, Object> details = new HashMap<>();
            details.put("teamLeader", shiftB.getTeamLeader());
            details.put("lineId", shiftB.getLine().getId());
            details.put("shiftStatus", shiftB.getShiftStatus());
            details.put("shift", "ShiftB");
            result.add(details);
        }
        result.sort(Comparator.comparing(m -> (Long) m.get("lineId")));
        return result;
    }


//return details by passing shift
    @Override
    public List<Map<String, Object>> getAllDetailsByShift(String shift) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (shift.equalsIgnoreCase("ShiftA")) {
            List<ShiftA> shiftAList = shiftARepository.findAll();
            for (ShiftA shiftA : shiftAList) {
                Map<String, Object> details = new HashMap<>();
                details.put("teamLeader", shiftA.getTeamLeader());
                details.put("lineId", shiftA.getLine().getId());
                details.put("shiftStatus", shiftA.getShiftStatus());
                result.add(details);
            }
        } else if (shift.equalsIgnoreCase("ShiftB")) {
            List<ShiftB> shiftBList = shiftBRepository.findAll();
            for (ShiftB shiftB : shiftBList) {
                Map<String, Object> details = new HashMap<>();
                details.put("teamLeader", shiftB.getTeamLeader());
                details.put("lineId", shiftB.getLine().getId());
                details.put("shiftStatus", shiftB.getShiftStatus());
                result.add(details);
            }
        }
        return result;
    }


    //get number of working lines
    @Override
    public Long getShiftStatusActiveCount(String shift) {
        Long count;
        if (shift.equals("ShiftA")) {
            count = shiftARepository.countByShiftStatus("true");
        } else if (shift.equals("ShiftB")) {
            count = shiftBRepository.countByShiftStatus("true");
        } else {
            throw new IllegalArgumentException("Invalid shift: " + shift);
        }
        return count;
    }

    @Override
    public Long getShiftStatusDeActiveCount(String shift) {
        Long count;
        if (shift.equals("ShiftA")) {
            count = shiftARepository.countByShiftStatus("false");
        } else if (shift.equals("ShiftB")) {
            count = shiftBRepository.countByShiftStatus("false");
        } else {
            throw new IllegalArgumentException("Invalid shift: " + shift);
        }
        return count;
    }


    @Transactional
    @Override
    public void deleteLine(long lineId) throws Exception {
        if (lineId == 0) {
            throw new Exception("Enter Line ID");
        }

        Line existingLine = this.lineRepository.findById(lineId)
                .orElseThrow(() -> new ResourceNotFoundException("line not found with id :" + lineId));

        this.shiftARepository.deleteByLine(existingLine);
        this.shiftBRepository.deleteByLine(existingLine);

        this.lineRepository.delete(existingLine);
    }
}
