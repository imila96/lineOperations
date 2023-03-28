package com.lineOperation.crud.entity;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "line_entries")
@Data
@NoArgsConstructor
public class Line {

    @Id
    private long id;

    private String teamLeader;

    private String shift;

}
