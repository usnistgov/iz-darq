package gov.nist.healthcare.iz.darq.users.domain;

public class LockRequest {
    private String id;
    private boolean lock;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }
}
