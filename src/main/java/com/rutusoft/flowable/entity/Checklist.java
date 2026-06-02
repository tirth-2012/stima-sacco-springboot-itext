package com.rutusoft.flowable.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "checklist_master")
public class Checklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String checklistTitle;

    private String category;

    private Integer categoryOrder;

    private Integer checklistOrder;

    private String productName;

    private String productType;

    private String stage;

    private Boolean isMandatory;

    private String variable;

}