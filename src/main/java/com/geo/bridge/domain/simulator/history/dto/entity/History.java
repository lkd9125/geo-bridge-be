package com.geo.bridge.domain.simulator.history.dto.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
@Builder 
@Table ("HISTORY")
public class History implements Persistable<Long>{

    @Id
    @Column("SN")
    private Long sn;

    @Column("TITLE")
    private String title; // nullable

    @Column("DESCRIPTION")
    private String description; // nullable

    @Column("PATH")
    private String path; // json

    @Column("TYPE")
    private String type;

    @Column("HOST")
    private String host;

    @Column("TOPIC")
    private String topic;

    @Column("HOST_ID")
    private String hostId;

    @Column("HOST_PASSWORD")
    private String hostPassword;

    @Column("SPEED")
    private Double speed;

    @Column("SPEED_UNIT")
    private String speedUnit;

    @Column("CYCLE")
    private Integer cycle; // 반복주기

    @Column("FORMAT")
    private String format;

    @Column("PARAMETER")
    private String parameter; // json (Map<String, String>)

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Override
    public Long getId() {
        return sn;
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }

}
