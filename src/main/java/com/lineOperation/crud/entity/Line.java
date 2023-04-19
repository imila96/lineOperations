    package com.lineOperation.crud.entity;


    import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import javax.persistence.*;

    @Entity
    @Table(name = "line_entries")
    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties({"shiftA", "shiftB"})
    public class Line {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;

        @Column(unique = true)
        private String lid;

        private String shiftAteamLeader;

        private String shiftBteamLeader;



        @OneToOne(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
        private ShiftA shiftA;

        @OneToOne(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
        private ShiftB shiftB;

    }

