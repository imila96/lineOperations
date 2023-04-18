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

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class LineServiceImpl implements LineService {

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private ShiftARepository shiftARepository;

    @Autowired
    private ShiftBRepository shiftBRepository;

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

    @Override
    public Line getLineById(long id) throws ResourceNotFoundException {
        return this.lineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Line not found with id :" + id));
    }

    @Override
    public List<Line> getAllLines() {
        return this.lineRepository.findAll();
    }

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


    //updateShiftStatus
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


    @Override
    public Long getShiftStatusCount(String shift) {
        Long count;
        if (shift.equals("ShiftA")) {
            count = shiftARepository.countByShiftStatus("on");
        } else if (shift.equals("ShiftB")) {
            count = shiftBRepository.countByShiftStatus("on");
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
