package com.rutusoft.flowable.enums;

public enum ActionType {

    SUBMIT("SUBMIT", "Submitted"),
    APPROVE("APPROVE", "Approved"),
    REJECT("REJECT", "Rejected"),
    SEND_BACK("SEND_BACK", "Sent Back"),
    AUTO_DECLINE("AUTO_DECLINE", "Auto Declined"),
    OVERRIDE("OVERRIDE", "Override"),
    ESCALATE("ESCALATE", "Escalated"),
    DECLINED("DECLINED", "Declined");


    private final String code;
    private final String label;

    ActionType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    // Optional: reverse lookup
    public static ActionType fromCode(String code) {
        for (ActionType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid ActionType: " + code);
    }
}