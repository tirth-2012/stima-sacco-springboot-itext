package com.rutusoft.flowable.enums;

public enum Region {
    CENTRAL("CENTRAL", "Central"),
    EASTERN("EASTERN", "Eastern");

    private final String code;
    private final String displayName;

    Region(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Region fromCode(String code) {
        for (Region branch : values()) {
            if (branch.code.equalsIgnoreCase(code)) {
                return branch;
            }
        }
        throw new IllegalArgumentException("Invalid region code: " + code);
    }
}
