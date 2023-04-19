package com.lineOperation.crud.service;

import com.lineOperation.crud.entity.Line;
import com.lineOperation.crud.entity.LineDto;
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

        if (line.getLid() == null)
            throw new Exception("Enter Line ID");


        if (line.getShiftAteamLeader() == null)
            throw new Exception("Enter team leader for Shift A!");

        if (line.getShiftBteamLeader() == null)
            throw new Exception("Enter team leader for Shift B!");

        Line savedLine = this.lineRepository.save(line);

        ShiftA shiftA = new ShiftA();
        shiftA.setLine(savedLine);
        shiftA.setTeamLeader(savedLine.getShiftAteamLeader());
        shiftA.setLid(savedLine.getLid());
        this.shiftARepository.save(shiftA);

        ShiftB shiftB = new ShiftB();
        shiftB.setLine(savedLine);
        shiftB.setTeamLeader(savedLine.getShiftBteamLeader());
        shiftB.setLid(savedLine.getLid());
        this.shiftBRepository.save(shiftB);

        return savedLine;
    }


    //get details by lineid

    @Override
    public Line getLineByLId(String lid) throws ResourceNotFoundException {
        return this.lineRepository.findByLid(lid);
               // .orElseThrow(() -> new ResourceNotFoundException("Line not found with id :" + lid));
    }
    @Override
    public List<Line> getAllLines() {
        return this.lineRepository.findAll();
    }

    @Override
    public List<LineDto> getAllLines2() {
        List<Line> lines = this.lineRepository.findAll();
        List<LineDto> lineDtos = new ArrayList<>();
        for (Line line : lines) {
            lineDtos.add(new LineDto(line));
        }
        return lineDtos;
    }



//update line details-teamleaders

    @Override
    public Line updateLine(Line line, String lineId) throws Exception {
//        if (line.getLid() == null)
//            throw new Exception("Enter Line ID");

        if (line.getShiftAteamLeader() == null)
            throw new Exception("Enter team leader for Shift A!");

        if (line.getShiftBteamLeader() == null)
            throw new Exception("Enter team leader for Shift B!");

        Line existingLine = this.lineRepository.findByLid(lineId);
                //.orElseThrow(() -> new ResourceNotFoundException("Line not found with id :" + lineId));

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
    @Transactional
    public boolean updateShiftStatus(String lineId, String shift, boolean status) {
        if (shift.equalsIgnoreCase("ShiftA")) {
            ShiftA shiftA = shiftARepository.findByLineLid(lineId);
            if (shiftA != null) {
                shiftA.setShiftStatus(status);
                shiftARepository.save(shiftA);
                return true;
            }
        } else if (shift.equalsIgnoreCase("ShiftB")) {
            ShiftB shiftB = shiftBRepository.findByLineLid(lineId);
            if (shiftB != null) {
                shiftB.setShiftStatus(status);
                shiftBRepository.save(shiftB);
                return true;
            }
        }
        return false;
    }




    //return all details (shift A+Shift B)
    @Override
    public List<Map<String, Object>> getAllShiftDetails() {
        List<Map<String, Object>> result = new ArrayList<>();
        List<ShiftA> shiftAList = shiftARepository.findAll();
        for (ShiftA shiftA : shiftAList) {
            Map<String, Object> details = new HashMap<>();
            details.put("teamLeader", shiftA.getTeamLeader());
            details.put("lineId", shiftA.getLine().getLid());
            details.put("shiftStatus", shiftA.isShiftStatus());
            details.put("shift", "ShiftA");
            result.add(details);
        }

        List<ShiftB> shiftBList = shiftBRepository.findAll();
        for (ShiftB shiftB : shiftBList) {
            Map<String, Object> details = new HashMap<>();
            details.put("teamLeader", shiftB.getTeamLeader());
            details.put("lineId", shiftB.getLine().getLid());
            details.put("shiftStatus", shiftB.isShiftStatus());
            details.put("shift", "ShiftB");
            result.add(details);
        }
        result.sort(Comparator.comparing(m -> (String) m.get("lineId")));
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
            details.put("lineId", shiftA.getLine().getLid());
            details.put("shiftStatus", shiftA.isShiftStatus());
            result.add(details);
        }
    } else if (shift.equalsIgnoreCase("ShiftB")) {
        List<ShiftB> shiftBList = shiftBRepository.findAll();
        for (ShiftB shiftB : shiftBList) {
            Map<String, Object> details = new HashMap<>();
            details.put("teamLeader", shiftB.getTeamLeader());
            details.put("lineId", shiftB.getLine().getLid());
            details.put("shiftStatus", shiftB.isShiftStatus());
            result.add(details);
        }
    }
    result.sort(Comparator.comparing(m -> (String) m.get("lineId")));
    return result;
}



    //get number of working lines
    @Override
    public Long getShiftStatusActiveCount(String shift) {
        Long count;
        if (shift.equals("ShiftA")) {
            count = shiftARepository.countByShiftStatus(true);
        } else if (shift.equals("ShiftB")) {
            count = shiftBRepository.countByShiftStatus(true);
        } else {
            throw new IllegalArgumentException("Invalid shift: " + shift);
        }
        return count;
    }

    @Override
    public Long getShiftStatusDeActiveCount(String shift) {
        Long count;
        if (shift.equals("ShiftA")) {
            count = shiftARepository.countByShiftStatus(false);
        } else if (shift.equals("ShiftB")) {
            count = shiftBRepository.countByShiftStatus(false);
        } else {
            throw new IllegalArgumentException("Invalid shift: " + shift);
        }
        return count;
    }


    @Transactional
    @Override
    public void deleteLine(String lid) throws Exception {
        if (lid ==null) {
            throw new Exception("Enter Line ID");
        }

        Line existingLine = this.lineRepository.findByLid(lid);
              //  .orElseThrow(() -> new ResourceNotFoundException("line not found with id :" + lineId));

        this.shiftARepository.deleteByLine(existingLine);
        this.shiftBRepository.deleteByLine(existingLine);

        this.lineRepository.delete(existingLine);
    }
}
