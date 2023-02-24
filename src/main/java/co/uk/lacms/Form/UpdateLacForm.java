package co.uk.lacms.Form;

import javax.validation.constraints.NotNull;

public class UpdateLacForm {

    private String lacUid;

    @NotNull(message = "Please select a social worker.")
    private String swUid;

    public String getLacUid() {
        return lacUid;
    }

    public void setLacUid(String lacUid) {
        this.lacUid = lacUid;
    }

    public String getSwUid() {
        return swUid;
    }

    public void setSwUid(String swUid) {
        this.swUid = swUid;
    }
}
