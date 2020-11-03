package gov.nist.healthcare.iz.darq.users.controller;
import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;
import gov.nist.healthcare.iz.darq.service.exception.OperationFailureException;
import gov.nist.healthcare.iz.darq.users.domain.ProfileUpdateRequest;
import gov.nist.healthcare.iz.darq.users.domain.LockRequest;
import gov.nist.healthcare.iz.darq.users.domain.SetRoleRequest;
import gov.nist.healthcare.iz.darq.users.domain.User;
import gov.nist.healthcare.iz.darq.users.exception.FieldValidationException;
import gov.nist.healthcare.iz.darq.users.service.AccountNotificationService;
import gov.nist.healthcare.iz.darq.users.service.impl.UserCleanupService;
import gov.nist.healthcare.iz.darq.users.service.impl.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/account")
public class UserManagementController {

    @Autowired
    private AccountNotificationService accountNotificationService;
    @Autowired
    private UserManagementService userManagementService;
    @Autowired
    private UserCleanupService userCleanupService;

    // Get All Users
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @ResponseBody
    public Set<User> all() {
        return this.userManagementService.getAllUsers();
    }

    // Get All Regular Users
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @ResponseBody
    public Set<User> exceptAdmin() {
        return this.userManagementService.getAllUsers().stream().filter(u -> !u.isAdministrator()).collect(Collectors.toSet());
    }

    @RequestMapping(value = "/lock", method = RequestMethod.POST)
    @ResponseBody
    public OpAck<User> lock(@RequestBody LockRequest lockRequest) {
        User user = this.userManagementService.setLock(lockRequest.getId(), lockRequest.isLock());
        this.accountNotificationService.notifyAccountLock(user, lockRequest.isLock());
        return new OpAck<>(OpAck.AckStatus.SUCCESS, (lockRequest.isLock() ? "Locked" : "Unlocked") + " Successfully", user, "LOCK");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public OpAck<User> update(
            @RequestBody ProfileUpdateRequest profileUpdateRequest,
            @AuthenticationPrincipal User current) throws OperationFailureException {
        try {
            User user = this.userManagementService.updateProfile(profileUpdateRequest, current);
            if(current.isAdministrator() && !current.getId().equals(user.getId())) {
                this.accountNotificationService.notifyAccountUpdated(user);
            }
            return new OpAck<>(OpAck.AckStatus.SUCCESS, "Profile Updated Successfully", user, "PROFILE_UPDATE");
        } catch (FieldValidationException e) {
            throw new OperationFailureException(e.getMessage());
        }
    }

    @RequestMapping(value = "/role", method = RequestMethod.POST)
    @ResponseBody
    public OpAck<User> setPrivilege(@RequestBody SetRoleRequest setRoleRequest) throws OperationFailureException {
        try {
            User user = this.userManagementService.setPrivilege(setRoleRequest.getId(), setRoleRequest.getRole());
            this.accountNotificationService.notifyAccountRoleChange(user, setRoleRequest.getRole());
            return new OpAck<>(OpAck.AckStatus.SUCCESS, "Role granted Successfully", user, "GRANT_ROLE");
        } catch (Exception e) {
            throw new OperationFailureException(e.getMessage());
        }
    }

    @RequestMapping(value = "/approve/{id}", method = RequestMethod.POST)
    @ResponseBody
    public OpAck<User> approve(@PathVariable String id) {
        User user = this.userManagementService.approve(id);
        this.accountNotificationService.notifyAccountApproved(user);
        return new OpAck<>(OpAck.AckStatus.SUCCESS,"Account Approved Successfully", user, "APPROVE");
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public OpAck<User> delete(@PathVariable String id) throws NotFoundException {
        User user = this.userCleanupService.deleteUser(id);
        this.accountNotificationService.notifyAccountDeleted(user);
        return new OpAck<>(OpAck.AckStatus.SUCCESS,"Account Deleted Successfully", user, "DELETE");
    }
}
