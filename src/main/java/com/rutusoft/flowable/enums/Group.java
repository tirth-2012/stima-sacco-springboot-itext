package com.rutusoft.flowable.enums;

public enum Group {
    //Roles for Sacco
    MEMBER("MEMBER", "Member"),
    CREDIT_OFFICER("CREDIT_OFFICER", "Credit Officer"),
    BRANCH_MANAGER("BRANCH_MANAGER", "Branch Manager"),
    GUARANTOR_VERIFICARION_OFFICER("GUARANTOR_VERIFICARION_OFFICER", "Guarantor Verification Officer"),
    SYSTEM_ADMINISTRATOR("SYSTEM_ADMINISTRATOR", "System Administrator"),
    CREDIT_APRAISAL("CREDIT_APRAISAL", "Credit Appraisal"),
    SENIOR_CREDIT_MANAGER("SENIOR_CREDIT_MANAGER", "Senior Credit Manager"),
    CREDIT_COMMITTEE("CREDIT_COMMITTEE", "Credit Committee"),
    BRANCH_CREDIT_COMMITTEE("BRANCH_CREDIT_COMMITTEE", "Branch Credit Committee"),
    LEGAL_OFFICER("LEGAL_OFFICER", "Legal Officer"),
    CREDIT_ADMINISTRATOR("CREDIT_ADMINISTRATOR", "Credit Administrator");

    private final String code;
    private final String displayName;

    Group(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Group fromCode(String code) {
        for (Group group : values()) {
            if (group.code.equalsIgnoreCase(code)) {
                return group;
            }
        }
        throw new IllegalArgumentException("Invalid group code: " + code);
    }
}