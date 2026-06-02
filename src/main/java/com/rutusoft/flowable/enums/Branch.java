package com.rutusoft.flowable.enums;

public enum Branch {
    BRANCH_HILLS("BRANCH_NRB", "Hill Branch"),
    BRANCH_NRB("BRANCH_WESTLANDS", "Westlands Branch");

    private final String code;
    private final String displayName;

    Branch(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Branch fromCode(String code) {
        for (Branch branch : values()) {
            if (branch.code.equalsIgnoreCase(code)) {
                return branch;
            }
        }
        throw new IllegalArgumentException("Invalid branch code: " + code);
    }
}
