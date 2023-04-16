package com.lineOperation.crud.entity;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "shiftB")
@Data
@NoArgsConstructor
public class ShiftB {

    @Id
    private long id;

    private String teamLeader;

    private String shiftStatus = "off";

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private Line line;
}
