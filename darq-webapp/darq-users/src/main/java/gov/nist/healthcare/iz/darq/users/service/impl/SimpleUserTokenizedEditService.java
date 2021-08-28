package gov.nist.healthcare.iz.darq.users.service.impl;

import com.google.common.base.Strings;
import freemarker.template.TemplateException;
import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.iz.darq.model.EmailType;
import gov.nist.healthcare.iz.darq.model.ToolConfigurationKey;
import gov.nist.healthcare.iz.darq.model.ToolConfigurationKeyValue;
import gov.nist.healthcare.iz.darq.model.ToolConfigurationProperty;
import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;
import gov.nist.healthcare.iz.darq.service.exception.OperationFailureException;
import gov.nist.healthcare.iz.darq.service.impl.SimpleEmailService;
import gov.nist.healthcare.iz.darq.users.domain.*;
import gov.nist.healthcare.iz.darq.users.exception.FieldValidationException;
import gov.nist.healthcare.iz.darq.users.repository.UserAccountRepository;
import gov.nist.healthcare.iz.darq.users.repository.UserEditTokenRepository;
import gov.nist.healthcare.iz.darq.users.service.UserTokenizedEditService;
import org.springframework.core.env.Environment;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class SimpleUserTokenizedEditService implements UserTokenizedEditService {

    private final String EMAIL_VERIFICATION_LINK_DURATION = "qdar.user.email.verification.link.duration.seconds";
    private final String PASSWORD_RESET_LINK_DURATION = "qdar.user.password.reset.link.duration.seconds";
    private final String TOOL_DEPLOYED_URL = "qdar.deployed.url";
    private final String EMAIL_VERIFICATION_PATH = "qdar.user.email.verification.path";
    private final String PASSWORD_RESET_PATH = "qdar.user.password.reset.path";
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("E, dd MMM yyyy, HH:mm:ss z");

    private final SimpleEmailService emailService;
    private final UserEditTokenRepository userEditTokenRepository;
    private final UserAccountRepository userAccountRepository;
    private final UserManagementService userManagementService;
    private final Environment environment;
    private final double DELAY = 5 * 60;
    private double DURATION_EMAIL_VERIFICATION;
    private double DURATION_PASSWORD_RESET;
    private String EMAIL_VERIFICATION_URL;
    private String PASSWORD_CHANGE_URL;
    private String HOST;

    public SimpleUserTokenizedEditService(SimpleEmailService emailService, UserEditTokenRepository userEditTokenRepository, UserAccountRepository userAccountRepository, UserManagementService userManagementService, Environment environment) {
        this.emailService = emailService;
        this.userEditTokenRepository = userEditTokenRepository;
        this.userAccountRepository = userAccountRepository;
        this.userManagementService = userManagementService;
        this.environment = environment;
    }

    private String getLink(UserEditToken token) {
        switch (token.getType()) {
            case PASSWORD_CHANGE:
                return this.HOST + '/' + PASSWORD_CHANGE_URL + "?token=" + token.getId();
            case EMAIL_VERIFICATION:
                return this.HOST + '/' + EMAIL_VERIFICATION_URL + "?token=" + token.getId();
        }
        return "";
    }

    void createAndSendEmail(UserEditToken token, User user, double stamp, double DURATION) throws MessagingException, TemplateException, NotFoundException, IOException {
        Map<String, String> params = new HashMap<>();
        params.put("USERNAME", user.getScreenName());
        params.put("LINK", getLink(token));
        params.put("EXPIRATION", DATE_FORMAT.format(new Date((long) (stamp + (DURATION * 1000)))));
        switch (token.getType()) {
            case PASSWORD_CHANGE:
                this.emailService.sendIfEnabled(EmailType.ACCOUNT_PASSWORD_CHANGE, user.getEmail(), params);
                break;
            case EMAIL_VERIFICATION:
                this.emailService.sendIfEnabled(EmailType.USER_VERIFY_EMAIL, user.getEmail(), params);
                break;
        }
    }

    @Override
    public void requestToken(User user, UserEditTokenType tokenType) throws MessagingException, TemplateException, NotFoundException, IOException {
        UserEditToken existing = this.userEditTokenRepository.findByUserIdAndType(user.getId(), tokenType);
        double stamp = System.currentTimeMillis();
        final double DURATION = tokenType.equals(UserEditTokenType.PASSWORD_CHANGE) ? this.DURATION_PASSWORD_RESET : this.DURATION_EMAIL_VERIFICATION;
        if(existing == null || this.isExpired(existing)) {
            UserEditToken token = new UserEditToken();
            token.setUserId(user.getId());
            token.setType(tokenType);
            token.setDuration(DURATION);
            token.setIssuedAt(Math.floor(stamp / 1000));
            if(existing != null) {
                this.userEditTokenRepository.delete(existing);
            }
            this.userEditTokenRepository.save(token);
            createAndSendEmail(token, user, stamp, DURATION);
        } else {
            createAndSendEmail(existing, user, existing.getIssuedAt() * 1000, DURATION);
        }
    }

    @Override
    public UserAccount verifyEmail(VerifyEmailRequest verifyEmailRequest) throws NotFoundException, OperationFailureException {
        UserEditToken token = this.validateToken(verifyEmailRequest.getToken(), UserEditTokenType.EMAIL_VERIFICATION);
        UserAccount account = this.userManagementService.passwordIsMatch(verifyEmailRequest.getUsername(), verifyEmailRequest.getPassword());
        if(account == null) {
            throw new NotFoundException("User not found");
        } else if(account.isVerified()) {
            throw new OperationFailureException("Email address already verified");
        } else {
            account.setVerified(true);
            this.userAccountRepository.save(account);
            this.userEditTokenRepository.delete(token);
        }
        return account;
    }

    @Override
    public UserAccount resetPassword(ResetPasswordRequest resetPasswordRequest) throws NotFoundException, FieldValidationException {
        UserEditToken token = this.validateToken(resetPasswordRequest.getToken(), UserEditTokenType.PASSWORD_CHANGE);
        UserAccount user = this.userAccountRepository.findById(token.getUserId());
        if(user == null) {
            throw new NotFoundException("User not found");
        } else if(!user.getUsername().equals(resetPasswordRequest.getUsername().trim())) {
            throw new NotFoundException("Wrong username");
        } else {
            this.userManagementService.changePassword(user, resetPasswordRequest.getPassword());
            this.userEditTokenRepository.delete(token);
        }
        return user;
    }

    @Override
    public UserEditToken validateToken(String tokenValue, UserEditTokenType tokenType) throws NotFoundException {
        UserEditToken token = this.userEditTokenRepository.findByIdAndType(tokenValue, tokenType);
        String error_text = tokenType.equals(UserEditTokenType.PASSWORD_CHANGE) ? "password reset" : "email verification";
        if(token == null) {
            throw new NotFoundException("Invalid " + error_text + " link");
        } else if(isExpired(token)) {
            this.userEditTokenRepository.delete(token);
            throw new NotFoundException("Expired " + error_text + " link");
        } else {
            return token;
        }
    }

    boolean isExpired(UserEditToken token) {
        double now = Math.floor(System.currentTimeMillis() / 1000);
        double expireAt = Math.floor(token.getIssuedAt() + token.getDuration());
        return expireAt - now < DELAY;
    }

    @Override
    public boolean shouldReloadOnUpdate(Set<ToolConfigurationKeyValue> keyValueSet) {
        return this.getConfigurationKeys().stream().anyMatch((a) -> keyValueSet.stream().anyMatch((b) -> a.getKey().equals(b.getKey())));
    }

    @Override
    public void configure(Properties properties) {
        this.DURATION_EMAIL_VERIFICATION = Double.parseDouble(properties.getProperty(EMAIL_VERIFICATION_LINK_DURATION, "0"));
        this.DURATION_PASSWORD_RESET = Double.parseDouble(properties.getProperty(PASSWORD_RESET_LINK_DURATION, "0"));
        this.HOST = properties.getProperty(TOOL_DEPLOYED_URL);
        this.EMAIL_VERIFICATION_URL = properties.getProperty(EMAIL_VERIFICATION_PATH);
        this.PASSWORD_CHANGE_URL = properties.getProperty(PASSWORD_RESET_PATH);
    }

    @Override
    public Set<ToolConfigurationProperty> initialize() {
        return new HashSet<>(
                Arrays.asList(
                        new ToolConfigurationProperty(EMAIL_VERIFICATION_LINK_DURATION, this.environment.getProperty(EMAIL_VERIFICATION_LINK_DURATION), true),
                        new ToolConfigurationProperty(PASSWORD_RESET_LINK_DURATION, this.environment.getProperty(PASSWORD_RESET_LINK_DURATION), true),
                        new ToolConfigurationProperty(TOOL_DEPLOYED_URL, this.environment.getProperty(TOOL_DEPLOYED_URL), true),
                        new ToolConfigurationProperty(EMAIL_VERIFICATION_PATH, this.environment.getProperty(EMAIL_VERIFICATION_PATH), true),
                        new ToolConfigurationProperty(PASSWORD_RESET_PATH, this.environment.getProperty(PASSWORD_RESET_PATH), true)
                )
        );
    }

    @Override
    public Set<ToolConfigurationKey> getConfigurationKeys() {
        return new HashSet<>(
                Arrays.asList(
                        new ToolConfigurationKey(EMAIL_VERIFICATION_LINK_DURATION, true),
                        new ToolConfigurationKey(PASSWORD_RESET_LINK_DURATION, true),
                        new ToolConfigurationKey(TOOL_DEPLOYED_URL, true),
                        new ToolConfigurationKey(EMAIL_VERIFICATION_PATH, true),
                        new ToolConfigurationKey(PASSWORD_RESET_PATH, true)
                )
        );
    }

    @Override
    public OpAck<Void> checkServiceStatus() {
        if(this.DURATION_EMAIL_VERIFICATION < DELAY) {
            return new OpAck<>(OpAck.AckStatus.FAILED, "Email verification link duration should be higher than " + DELAY + " seconds", null, "USER VERIFICATION");
        }

        if(this.DURATION_PASSWORD_RESET < DELAY) {
            return new OpAck<>(OpAck.AckStatus.FAILED, "Password reset link duration should be higher than " + DELAY + " seconds", null, "USER VERIFICATION");
        }

        if(Strings.isNullOrEmpty(HOST)) {
            return new OpAck<>(OpAck.AckStatus.FAILED, "Host is required", null, "USER VERIFICATION");
        }

        try {
            URL url = new URL(HOST);
            String host = url.getHost();
            int port = url.getPort() != -1 ? url.getPort() : url.getDefaultPort() != -1 ? url.getDefaultPort() : 80;
            if(!pingHost(host, port, 10000)) {
                return new OpAck<>(OpAck.AckStatus.FAILED, "Host is not reachable", null, "USER VERIFICATION");
            }
        } catch (MalformedURLException e) {
            return new OpAck<>(OpAck.AckStatus.FAILED, "Invalid host due to " + e.getMessage(), null, "USER VERIFICATION");
        }

        return new OpAck<>(OpAck.AckStatus.SUCCESS, "Configuration is valid", null, "USER VERIFICATION");
    }

    public static boolean pingHost(String host, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        } catch (IOException e) {
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
    }

    @Override
    public String getServiceDisplayName() {
        return "USER_VERIFICATION";
    }

}
