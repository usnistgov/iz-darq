package gov.nist.healthcare.iz.darq.users.service;

import gov.nist.healthcare.iz.darq.users.domain.User;

public interface AccountNotificationService {
    void notifyAdminAccountCreated(User user);
    void notifyAccountApproved(User user);
    void notifyAccountUpdated(User user);
    void notifyAccountLock(User user, boolean lock);
    void notifyAccountRoleChange(User user, String role);
    void notifyAccountDeleted(User user);
}
