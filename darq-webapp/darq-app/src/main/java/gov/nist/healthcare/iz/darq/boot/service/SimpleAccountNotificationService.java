package gov.nist.healthcare.iz.darq.boot.service;

import com.google.common.base.Strings;
import gov.nist.healthcare.iz.darq.model.EmailType;
import gov.nist.healthcare.iz.darq.service.impl.SimpleEmailService;
import gov.nist.healthcare.iz.darq.users.domain.User;
import gov.nist.healthcare.iz.darq.users.service.AccountNotificationService;
import gov.nist.healthcare.iz.darq.users.service.impl.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class SimpleAccountNotificationService implements AccountNotificationService {

    @Autowired
    SimpleEmailService emailService;
    @Autowired
    UserManagementService userManagementService;

    @Override
    public void notifyAdminAccountCreated(User created) {
        this.userManagementService.getAllUsers().stream().filter((u) -> u.isAdministrator() && !Strings.isNullOrEmpty(u.getEmail())).forEach(
                (user) -> {
                    try {
                        HashMap<String, String> params = new HashMap<>();
                        params.put("ADMIN_USERNAME", user.getScreenName());
                        params.put("USER_FULL_NAME", created.getName());
                        params.put("USERNAME", created.getUsername());
                        params.put("USER_EMAIL", created.getEmail());
                        params.put("USER_ORG", created.getOrganization());
                        params.put("ACCOUNT_SOURCE", created.getSource() != null ? created.getSource() : "qDAR");
                        emailService.sendIfEnabled(EmailType.ADMIN_ACCOUNT_CREATED, user.getEmail(), params);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    @Override
    public void notifyAccountApproved(User user) {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("USERNAME", user.getScreenName());
            emailService.sendIfEnabled(EmailType.ACCOUNT_APPROVED, user.getEmail(), params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notifyAccountUpdated(User user) {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("USERNAME", user.getScreenName());
            emailService.sendIfEnabled(EmailType.ACCOUNT_UPDATED, user.getEmail(), params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notifyAccountLock(User user, boolean lock) {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("USERNAME", user.getScreenName());
            emailService.sendIfEnabled(lock ? EmailType.ACCOUNT_LOCKED : EmailType.ACCOUNT_UNLOCKED, user.getEmail(), params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notifyAccountRoleChange(User user, String role) {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("USERNAME", user.getScreenName());
            params.put("ROLE", role);
            emailService.sendIfEnabled(EmailType.ACCOUNT_ROLE_CHANGE, user.getEmail(), params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notifyAccountDeleted(User user) {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("USERNAME", user.getScreenName());
            emailService.sendIfEnabled(EmailType.ACCOUNT_DELETED, user.getEmail(), params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
