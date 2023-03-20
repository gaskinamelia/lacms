package co.uk.lacms.Form;

public class FilterManagerDashboardForm {

    private boolean unassignedLacs;

    private boolean assignedLacs;

    public FilterManagerDashboardForm() {}

    public FilterManagerDashboardForm(boolean unassignedLacs, boolean assignedLacs) {
        this.assignedLacs = assignedLacs;
        this.unassignedLacs = unassignedLacs;
    }

    public boolean isUnassignedLacs() {
        return unassignedLacs;
    }

    public void setUnassignedLacs(boolean unassignedLacs) {
        this.unassignedLacs = unassignedLacs;
    }

    public boolean isAssignedLacs() {
        return assignedLacs;
    }

    public void setAssignedLacs(boolean assignedLacs) {
        this.assignedLacs = assignedLacs;
    }
}

