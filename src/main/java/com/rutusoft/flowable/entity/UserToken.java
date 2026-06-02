package com.rutusoft.flowable.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_token")
@Data
public class UserToken {

    @Id
    private String token;

    private String userId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expiryTime;
}