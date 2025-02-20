package com.samhcoco.healthapp.core.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "message")
public class Message  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String recipient;
    private String sender;
    private String subject;
    private String body;
    private String channel;
    private Long userId;
    private String status;
    private Date created;
}