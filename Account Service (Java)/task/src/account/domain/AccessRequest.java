package account.domain;

public class AccessRequest {
    public enum Operation {
        LOCK, UNLOCK;
    }
    private String user;
    private Operation operation;

    public AccessRequest() {
    }

    public AccessRequest(String user, Operation operation) {
        this.user = user;
        this.operation = operation;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

}
