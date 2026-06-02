package com.rutusoft.flowable.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ProcessStatus {
    IN_PROGRESS("IN_PROGRESS", "In Progress"),
    SUSPENDED("SUSPENDED", "Suspended"),
    COMPLETED("COMPLETED", "Completed");

    private final String code;
    private final String label;

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }
}
