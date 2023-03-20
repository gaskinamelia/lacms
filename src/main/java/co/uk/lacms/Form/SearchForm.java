package co.uk.lacms.Form;

public class SearchForm {
    String searchQuery;

    boolean viewArchived;

    String lacUid;

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public boolean isViewArchived() {
        return viewArchived;
    }

    public void setViewArchived(boolean viewArchived) {
        this.viewArchived = viewArchived;
    }

    public String getLacUid() {
        return lacUid;
    }

    public void setLacUid(String lacUid) {
        this.lacUid = lacUid;
    }
}
