package com.eirs.lsm.repository.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "allowed_tac_his")
public class AllowedTac {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tac;

    private Integer operation;

    private LocalDateTime createdOn;

}
