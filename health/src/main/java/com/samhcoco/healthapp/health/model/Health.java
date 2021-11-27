package com.samhcoco.healthapp.health.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Health implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private long userId;
    private float lastRecordedTemperature;
    private Date lastCheckup;
    private Short heartRate; // bpm

    //    private float bloodPressure; // mmHg

    private float bloodSugarLevel; // mM
    private long doctorId;
}
