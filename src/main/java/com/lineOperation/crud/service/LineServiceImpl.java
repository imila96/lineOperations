package com.lineOperation.crud.service;

import com.lineOperation.crud.entity.Line;
import com.lineOperation.crud.entity.LineDto;
import com.lineOperation.crud.entity.ShiftA;
import com.lineOperation.crud.entity.ShiftB;
import com.lineOperation.crud.exception.*;
import com.lineOperation.crud.repository.LineRepository;
import com.lineOperation.crud.repository.ShiftARepository;
import com.lineOperation.crud.repository.ShiftBRepository;
import com.lineOperation.crud.service.LineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
    public Line createLine(Line line) throws LineValidationException, LineSaveException {

        if (line.getLid() == null)
            throw new LineValidationException("Line ID is required.");

        if (!line.getLid().matches("^DS\\d+$"))
            throw new LineValidationException("Line ID should start with 'DS' and be followed by any number.");


        if (line.getShiftAteamLeader() == null)
            throw new LineValidationException("Team leader for Shift A is required.");

        if (line.getShiftBteamLeader() == null)
            throw new LineValidationException("Team leader for Shift B is required.");

        try {
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
        } catch (DataAccessException e) {
            throw new LineSaveException("Error occurred while saving the line.", e);
        }
    }



    //get details by lineid

    @Override
    public Line getLineByLId(String lid) throws ResourceNotFoundException, LineValidationException {
        if (lid == null || !lid.matches("^DS\\d+$")) {
            throw new LineValidationException("Invalid Line ID format.");
        }

        Line line = this.lineRepository.findByLid(lid);
        if (line == null) {
            throw new ResourceNotFoundException("Line with ID " + lid + " not found.");
        }
        return line;
    }


     //get all lines
     @Override
     public List<LineDto> getAllLines() throws ResourceNotFoundException {
         List<Line> lines = this.lineRepository.findAll();
         if (lines == null || lines.isEmpty()) {
             throw new ResourceNotFoundException("No lines found.");
         }
         List<LineDto> lineDtos = new ArrayList<>();
         for (Line line : lines) {
             lineDtos.add(new LineDto(line));
         }
         return lineDtos;
     }


//update line details-teamleaders

    @Override
    public Line updateLine(Line line, String lineId) throws LineValidationException, LineNotFoundException, LineSaveException {

        if (line == null)
            throw new LineValidationException("Line object cannot be null.");

        if (lineId == null || lineId.isEmpty())
            throw new LineValidationException("Line ID cannot be null or empty.");

        if (!lineId.matches("^DS\\d+$"))
            throw new LineValidationException("Line ID format is incorrect.");

        Line existingLine = this.lineRepository.findByLid(lineId);
        if (existingLine == null)
            throw new LineNotFoundException("Line not found with ID: " + lineId);

        if (line.getShiftAteamLeader() == null)
            throw new LineValidationException("Team leader for Shift A is required.");

        if (line.getShiftBteamLeader() == null)
            throw new LineValidationException("Team leader for Shift B is required.");

        try {
            existingLine.setShiftAteamLeader(line.getShiftAteamLeader());
            existingLine.setShiftBteamLeader(line.getShiftBteamLeader());

            Line savedLine = this.lineRepository.save(existingLine);

            ShiftA shiftA = this.shiftARepository.findByLine(savedLine);
            if (shiftA != null) {
                shiftA.setTeamLeader(savedLine.getShiftAteamLeader());
            } else {
                shiftA = new ShiftA();
                shiftA.setLine(savedLine);
                shiftA.setTeamLeader(savedLine.getShiftAteamLeader());
            }
            this.shiftARepository.save(shiftA);

            ShiftB shiftB = this.shiftBRepository.findByLine(savedLine);
            if (shiftB != null) {
                shiftB.setTeamLeader(savedLine.getShiftBteamLeader());
            } else {
                shiftB = new ShiftB();
                shiftB.setLine(savedLine);
                shiftB.setTeamLeader(savedLine.getShiftBteamLeader());
            }
            this.shiftBRepository.save(shiftB);

            return savedLine;
        } catch (DataAccessException e) {
            throw new LineSaveException("Error occurred while saving the line.", e);
        }
    }


    //update ShiftStatus
    @Override
    @Transactional
    public boolean updateShiftStatus(String lineId, String shift, boolean status) throws LineNotFoundException, ShiftUpdateException {
        if (!lineId.matches("^DS\\d+$")) {
            throw new LineNotFoundException("Invalid line ID: " + lineId);
        }
        if (!shift.equalsIgnoreCase("ShiftA") && !shift.equalsIgnoreCase("ShiftB")) {
            throw new ShiftUpdateException("Invalid shift: " + shift);
        }
        try {
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
        } catch (DataAccessException e) {
            throw new ShiftUpdateException("Error occurred while updating shift status.", e);
        }
    }





    //return all details (shift A+Shift B)
    @Override
    public List<Map<String, Object>> getAllShiftDetails() throws ShiftDetailsNotFoundException {
        try {
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

            if (result.isEmpty()) {
                throw new ShiftDetailsNotFoundException("No shift details found.");

            }

            result.sort(Comparator.comparing(m -> ((String) m.get("lineId")).substring(2)));

            return result;
        } catch (DataAccessException e) {
            throw new ShiftDetailsNotFoundException(e);
        }
    }



    //return details by passing shift
    @Override
    public List<Map<String, Object>> getAllDetailsByShift(String shift) throws ShiftDetailsNotFoundException {
        try {
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
            } else {
                throw new ShiftDetailsNotFoundException("Invalid shift type.");
            }
            result.sort(Comparator.comparing(m -> ((String) m.get("lineId")).substring(2)));
            return result;
        } catch (DataAccessException e) {
            throw new ShiftDetailsNotFoundException("Error occurred while retrieving shift details.", e);
        }
    }




    //get number of working lines
    @Override
    public Long getShiftStatusActiveCount(String shift) throws InvalidShiftException {
        Long count;
        if (shift.equals("ShiftA")) {
            count = shiftARepository.countByShiftStatus(true);
        } else if (shift.equals("ShiftB")) {
            count = shiftBRepository.countByShiftStatus(true);
        } else {
            throw new InvalidShiftException("Invalid shift: " + shift);
        }
        return count;
    }




    @Override
    public Long getShiftStatusDeActiveCount(String shift) throws InvalidShiftException {
        Long count;
        if (shift.equals("ShiftA")) {
            count = shiftARepository.countByShiftStatus(false);
        } else if (shift.equals("ShiftB")) {
            count = shiftBRepository.countByShiftStatus(false);
        } else {
            throw new InvalidShiftException("Invalid shift: " + shift);
        }
        return count;
    }


    @Transactional
    @Override
    public void deleteLine(String lid) throws InvalidLineIdException, LineNotFoundException {
        if (lid == null || !lid.matches("^DS\\d+$")) {
            throw new InvalidLineIdException("Invalid line ID: " + lid);
        }

        Line existingLine = this.lineRepository.findByLid(lid);
        if (existingLine == null) {
            throw new LineNotFoundException("Line not found with ID: " + lid);
        }


        this.shiftARepository.deleteByLine(existingLine);
        this.shiftBRepository.deleteByLine(existingLine);

        this.lineRepository.delete(existingLine);
    }


}
