package co.uk.lacms.Entity;

public enum UserType {
    SW("Social worker"),
    SW_MANAGER("Social worker manager"),
    LAC("Looked after child"),
    PA("Personal advisor");

    private final String displayName;
    UserType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static UserType fromDisplayName(String displayName) {
        for (UserType userType : UserType.values()) {
            if (userType.displayName.equalsIgnoreCase(displayName)) {
                return userType;
            }

        }
        return null;
    }
}
