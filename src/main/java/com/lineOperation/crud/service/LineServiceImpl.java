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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Object> createLine(Line line) throws LineValidationException, LineSaveException {
        try {
            if (line.getShiftAteamLeader() == null)
                throw new LineValidationException("Team leader for Shift A is required.");

            if (line.getShiftBteamLeader() == null)
                throw new LineValidationException("Team leader for Shift B is required.");

            // retrieve the maximum lid from the database
            String maxLid = lineRepository.findMaxLid();

            // if the maximum lid is null, set it to DS0000
            if (maxLid == null) {
                maxLid = "DS0000";
            }

            // extract the numeric portion of the maximum lid and increment it by 1
            int newLidNum = Integer.parseInt(maxLid.substring(2)) + 1;

            // format the new lid with leading zeroes and the 'DS' prefix
            String newLid = String.format("DS%04d", newLidNum);

            // set the new lid for the line
            line.setLid(newLid);

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

            System.out.println("Line Created Successfully");

            return ResponseEntity.ok("Line Created Successfully");
        } catch (LineValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (DataAccessException e) {
            throw new LineSaveException("Error occurred while saving the line.", e);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
    public ResponseEntity<String> updateLine(Line line, String lineId) {

        try {
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

            return ResponseEntity.ok("Line Updated Successfully");

        } catch (LineValidationException | LineNotFoundException | DataAccessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while updating the line.");
        }
    }



    //update ShiftStatus
    @Override
    @Transactional
    public ResponseEntity<String> updateShiftStatus(String lineId, String shift, boolean status) {
        try {
            if (!lineId.matches("^DS\\d+$")) {
                throw new LineNotFoundException("Invalid line ID: " + lineId);
            }
            if (!shift.equalsIgnoreCase("ShiftA") && !shift.equalsIgnoreCase("ShiftB")) {
                throw new ShiftUpdateException("Invalid shift: " + shift);
            }

            if (shift.equalsIgnoreCase("ShiftA")) {
                ShiftA shiftA = shiftARepository.findByLineLid(lineId);
                if (shiftA != null) {
                    shiftA.setShiftStatus(status);
                    shiftARepository.save(shiftA);
                    return ResponseEntity.ok("Shift Status Changed Successfully");
                }
            } else if (shift.equalsIgnoreCase("ShiftB")) {
                ShiftB shiftB = shiftBRepository.findByLineLid(lineId);
                if (shiftB != null) {
                    shiftB.setShiftStatus(status);
                    shiftBRepository.save(shiftB);
                    return ResponseEntity.ok("Shift Status Changed Successfully");
                }
            }

            throw new LineNotFoundException("Line not found with ID: " + lineId);

        } catch (LineNotFoundException | ShiftUpdateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while updating shift status.");
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

            result.sort(Comparator.comparing(m -> Integer.parseInt(((String) m.get("lineId")).substring(2))));

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
            result.sort(Comparator.comparing(m -> Integer.parseInt(((String) m.get("lineId")).substring(2))));
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
    public ResponseEntity<String> deleteLine(String lid) {
        try {
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

            return ResponseEntity.ok("Line deleted successfully.");
        } catch (InvalidLineIdException | LineNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while deleting the line.");
        }
    }




}
