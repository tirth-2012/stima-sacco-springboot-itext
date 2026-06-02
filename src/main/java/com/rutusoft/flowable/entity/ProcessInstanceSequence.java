package com.rutusoft.flowable.entity;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;


@Entity
@Table(name = "process_instance_sequence")
@Data
public class ProcessInstanceSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "process_inst_seq_gen")
    @SequenceGenerator(
            name = "process_inst_seq_gen",
            sequenceName = "process_inst_seq_gen",
            allocationSize = 1
    )
    private Long id;
}