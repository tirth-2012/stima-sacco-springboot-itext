package com.rutusoft.flowable.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Status {
    IN_PROGRESS("IN_PROGRESS", "In Progress"),
    COMPLETED("COMPLETED", "Completed"),
    APPROVED("APPROVED", "Approved"),
    DECLINED("DECLINED", "Declined"),
    REJECTED("REJECTED", "Rejected"),
    PENDING("PENDING", "Pending");

    private final String code;
    private final String label;

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

}
