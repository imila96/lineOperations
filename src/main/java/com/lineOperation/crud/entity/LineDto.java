package com.lineOperation.crud.entity;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@NoArgsConstructor
public class LineDto {

    @Column(unique = true)
    private String lid;
    private String shiftAteamLeader;
    private String shiftBteamLeader;

    public LineDto(Line line) {
        this.lid = line.getLid();
        this.shiftAteamLeader = line.getShiftAteamLeader();
        this.shiftBteamLeader = line.getShiftBteamLeader();
    }
}
