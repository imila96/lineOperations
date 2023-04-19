    package com.lineOperation.crud.entity;

    import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import javax.persistence.*;

    @Entity
    @Table(name = "shiftA")

    @Data
    @NoArgsConstructor
    public class ShiftA {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;

        @Column(unique = true)
        private String lid;

        private String teamLeader;

        private boolean shiftStatus = false;

        @OneToOne(fetch = FetchType.LAZY)
        @MapsId
        private Line line;
    }
