package gov.nist.healthcare.iz.darq.boot;

import com.google.common.base.Strings;
import gov.nist.healthcare.iz.darq.access.domain.Permission;
import gov.nist.healthcare.iz.darq.access.domain.UserRole;
import gov.nist.healthcare.iz.darq.users.domain.UserAccount;
import gov.nist.healthcare.iz.darq.users.repository.UserAccountRepository;
import gov.nist.healthcare.iz.darq.users.repository.UserRoleRepository;
import gov.nist.healthcare.iz.darq.users.service.impl.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class UserAccountAndRoleDataIntegrityInitializer {

    @Value("${darq.admin.default}")
    private String ADMIN_PASSWORD;
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private UserManagementService userManagementService;

    @PostConstruct
    public void initializeAndFixAccounts() {
        //---------------------- USER ROLES ----------------------
        UserRole BASIC = this.userRoleRepository.findByRole("BASIC");
        if(BASIC == null) {
            BASIC = new UserRole();
        }
        BASIC.setRole("BASIC");
        BASIC.setPermissions(new HashSet<>(Arrays.asList(
                Permission.CONFIG_PUBLIC_VIEW,
                Permission.RT_PUBLIC_VIEW
        )));
        this.userRoleRepository.save(BASIC);

        UserRole ADVANCED = this.userRoleRepository.findByRole("ADVANCED");
        if(ADVANCED == null) {
            ADVANCED = new UserRole();
        }
        ADVANCED.setRole("ADVANCED");
        ADVANCED.setPermissions(Stream.concat(
                BASIC.getPermissions().stream(),
                Stream.of(
                        Permission.CONFIG_PRIVATE_AUTHOR,
                        Permission.RT_PRIVATE_AUTHOR,
                        Permission.DATA_PRIVATE_AUTHOR
                )).collect(Collectors.toSet())
        );
        this.userRoleRepository.save(ADVANCED);

        UserRole MODERATOR = this.userRoleRepository.findByRole("MODERATOR");
        if(MODERATOR == null) {
            MODERATOR = new UserRole();
        }
        MODERATOR.setRole("MODERATOR");
        MODERATOR.setPermissions(Stream.concat(
                ADVANCED.getPermissions().stream(),
                Stream.of(
                        Permission.CONFIG_PUBLIC_AUTHOR,
                        Permission.CONFIG_PUBLISH,
                        Permission.RT_PUBLIC_AUTHOR,
                        Permission.RT_PUBLISH
                )).collect(Collectors.toSet())
        );
        this.userRoleRepository.save(MODERATOR);

        UserRole IIS_BASIC = this.userRoleRepository.findByRole("IIS_BASIC");
        if(IIS_BASIC == null) {
            IIS_BASIC = new UserRole();
        }
        IIS_BASIC.setRole("IIS_BASIC");
        IIS_BASIC.setPermissions(new HashSet<>(Arrays.asList(
                Permission.DATA_FACILITY_VIEW,
                Permission.DATA_FACILITY_UPLOAD
        )));
        this.userRoleRepository.save(IIS_BASIC);

        UserRole IIS_ADVANCED = this.userRoleRepository.findByRole("IIS_ADVANCED");
        if(IIS_ADVANCED == null) {
            IIS_ADVANCED = new UserRole();
        }
        IIS_ADVANCED.setRole("IIS_ADVANCED");
        IIS_ADVANCED.setPermissions(Stream.concat(
                IIS_BASIC.getPermissions().stream(),
                Stream.of(
                        Permission.DATA_FACILITY_AUTHOR
                )).collect(Collectors.toSet())
        );
        this.userRoleRepository.save(IIS_ADVANCED);

        UserRole IIS_MODERATOR = this.userRoleRepository.findByRole("IIS_MODERATOR");
        if(IIS_MODERATOR == null) {
            IIS_MODERATOR = new UserRole();
        }
        IIS_MODERATOR.setRole("IIS_MODERATOR");
        IIS_MODERATOR.setPermissions(Stream.concat(
                IIS_ADVANCED.getPermissions().stream(),
                Stream.of(
                        Permission.DATA_FACILITY_PUBLISH
                )).collect(Collectors.toSet())
        );
        this.userRoleRepository.save(IIS_MODERATOR);

        // ADMIN
        UserRole ADMIN = this.userRoleRepository.findByRole("ADMIN");
        if(ADMIN == null) {
            ADMIN = new UserRole();
        }
        ADMIN.setRole("ADMIN");
        ADMIN.setPermissions(new HashSet<>(Arrays.asList(
                Permission.values()
        )));
        this.userRoleRepository.save(ADMIN);

        List<UserRole> ROLES = Arrays.asList(BASIC, ADVANCED, MODERATOR, IIS_BASIC, IIS_ADVANCED, IIS_MODERATOR, ADMIN);

        // ------------------------ USERS --------------------
        // --- [ADMIN] ----
        UserAccount admin = this.userAccountRepository.findByUsernameIgnoreCase("admin");
        if(admin == null) {
            if(ADMIN_PASSWORD != null && !ADMIN_PASSWORD.isEmpty()) {
                userManagementService.createAdmin("admin", ADMIN_PASSWORD);
            }
        }

        // --- [User Roles] ---
        List<UserAccount> users = this.userAccountRepository.findAll();
        for(UserAccount user: users) {
            Set<UserRole> valid = user.getAuthorities().stream().filter(ROLES::contains).collect(Collectors.toSet());
            user.setAuthorities(valid);

            if(!Strings.isNullOrEmpty(user.getUsername()) && user.getUsername().equals("admin") && !user.getAuthorities().contains(ADMIN)) {
                user.getAuthorities().add(ADMIN);
            } else if(user.getAuthorities().isEmpty()) {
                user.setAuthorities(new HashSet<>(Collections.singletonList(
                        BASIC
                )));
            }
            user.setqDarAccount(!Strings.isNullOrEmpty(user.getUsername()) && !Strings.isNullOrEmpty(user.getPassword()));

            if(!Strings.isNullOrEmpty(user.getUsername()) && user.getUsername().equals("admin")) {
                user.setVerified(true);
                user.setLocked(false);
                user.setPending(false);
            }

            if(!Strings.isNullOrEmpty(user.getUsername())) {
                this.userAccountRepository.findByUsernameIgnoreCase(user.getUsername());
            }

            if(!Strings.isNullOrEmpty(user.getEmail())) {
                user.setEmail(user.getEmail().toLowerCase());
                this.userAccountRepository.findByEmailIgnoreCase(user.getEmail());
            }
        }
        this.userAccountRepository.save(users);

        // --- [Role Clean Up] ---
        List<UserRole> roles = this.userRoleRepository.findAll();
        for(UserRole role: roles) {
            if(!ROLES.contains(role)) {
                this.userRoleRepository.delete(role);
            }
        }
    }
}
