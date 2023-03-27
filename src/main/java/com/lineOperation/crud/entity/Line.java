package com.lineOperation.crud.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "line_entries")
public class Line {

    @Id
    @Column(name = "Line_Id")
    private long id;

    @Column(name = "Team_Leader")
    private String Team_Leader;

    @Column(name = "Shift_Status")
    private String Shift_Status;



    public Line() {

    }


    public Line(String team_Leader, String shift_Status) {
        super();
        Team_Leader = team_Leader;
        Shift_Status = shift_Status;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getTeam_Leader() {
        return Team_Leader;
    }

    public void setTeam_Leader(String team_Leader) {
        Team_Leader = team_Leader;
    }

    public String getShift_Status() {
        return Shift_Status;
    }

    public void setShift_Status(String shift_Status) {
        Shift_Status = shift_Status;
    }
}
