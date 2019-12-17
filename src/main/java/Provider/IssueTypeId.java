package Provider;

public enum IssueTypeId {
    BUG_TYPE_ID("10200"),
    TASK_TYPE_ID("10100");

    public final String id;

    IssueTypeId(String id) {
        this.id = id;
    }
}