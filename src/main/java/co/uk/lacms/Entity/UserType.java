package co.uk.lacms.Entity;

public enum UserType {
    SW("Social worker"),
    LAC("Looked after child"),
    PA("Personal advisor");

    private final String displayName;
    UserType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
