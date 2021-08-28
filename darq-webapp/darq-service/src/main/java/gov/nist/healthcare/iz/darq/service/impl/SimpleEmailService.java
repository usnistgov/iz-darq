package gov.nist.healthcare.iz.darq.service.impl;

import com.google.common.base.Strings;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.iz.darq.access.service.ConfigurableService;
import gov.nist.healthcare.iz.darq.access.service.EmailService;
import gov.nist.healthcare.iz.darq.model.*;
import gov.nist.healthcare.iz.darq.repository.EmailTemplateRepository;
import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

public class SimpleEmailService implements EmailService, ConfigurableService {

    private final EmailTemplateRepository repository;
    private static final String EMAIL_SENDER_KEY = "mail.sender";
    private static final String EMAIL_USERNAME_KEY = "mail.username";
    private static final String EMAIL_PASSWORD_KEY = "mail.password";
    public static final List<ToolConfigurationKey> properties = Collections.unmodifiableList(Arrays.asList(
            new ToolConfigurationKey(EMAIL_SENDER_KEY,true),
            new ToolConfigurationKey(EMAIL_USERNAME_KEY,false),
            new ToolConfigurationKey(EMAIL_PASSWORD_KEY,false),
            new ToolConfigurationKey("mail.smtp.auth",true),
            new ToolConfigurationKey("mail.smtp.timeout",false),
            new ToolConfigurationKey("mail.smtp.starttls.enable",false),
            new ToolConfigurationKey("mail.smtp.ssl.trust",false),
            new ToolConfigurationKey("mail.smtp.host",true),
            new ToolConfigurationKey("mail.smtp.port", true)
    ));
    private String EMAIL_SENDER;
    private Session SESSION;
    private final Environment environment;


    public SimpleEmailService(EmailTemplateRepository repository, Environment environment) {
        this.repository = repository;
        this.environment = environment;
    }

    @Override
    public List<EmailTemplate> findAll() {
        return this.repository.findAll();
    }

    @Override
    public EmailTemplate setEmailTemplate(EmailTemplate emailTemplate) {
        if(emailTemplate == null) {
            throw new IllegalArgumentException("Email must not be null");
        }
        if(emailTemplate.getType() == null) {
            throw new IllegalArgumentException("Email type must not be null");
        }
        if(emailTemplate.isEnabled() && (Strings.isNullOrEmpty(emailTemplate.getSubject()) || Strings.isNullOrEmpty(emailTemplate.getTemplate()))) {
            throw new IllegalArgumentException("Email template subject and content must not be empty");
        }
        return this.repository.save(emailTemplate);
    }

    @Override
    public EmailTemplate setEmailTemplateIfAbsent(EmailTemplate emailTemplate) {
        boolean exists = this.repository.exists(emailTemplate.getType());
        if(!exists) {
            this.setEmailTemplate(emailTemplate);
        }
        return emailTemplate;
    }

    @Override
    public EmailTemplate getTemplate(EmailType emailType) throws NotFoundException {
        EmailTemplate email = this.repository.findOne(emailType);
        if(email == null) {
            throw new NotFoundException("Email template " + emailType + " not found");
        }
        return email;
    }

    @Override
    public void setEmailServerProperties(Properties properties) {
        // Check Sender
        this.EMAIL_SENDER = properties.getProperty(EMAIL_SENDER_KEY);
        if(Strings.isNullOrEmpty(EMAIL_SENDER)) {
            throw new IllegalArgumentException("Email sender property is required");
        }
        this.SESSION = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        properties.getProperty(EMAIL_USERNAME_KEY),
                        properties.getProperty(EMAIL_PASSWORD_KEY)
                );
            }
        });
    }

    @Override
    public boolean isEnabled(EmailType emailType) throws NotFoundException {
        EmailTemplate email = this.getTemplate(emailType);
        return email.isEnabled();
    }

    @Override
    public void send(EmailType emailType, String to, Map<String, String> params) throws NotFoundException, TemplateException, IOException, MessagingException {
        EmailTemplate email = this.getTemplate(emailType);
        this.send(email, to, params);
    }

    @Override
    public boolean sendIfEnabled(EmailType emailType, String to, Map<String, String> params) throws NotFoundException, TemplateException, IOException, MessagingException {
        EmailTemplate email = this.getTemplate(emailType);
        if(email.isEnabled()) {
            this.send(email, to, params);
            return true;
        }
        return false;
    }

    public void send(EmailTemplate email, String to, Map<String, String> params) throws IOException, TemplateException, MessagingException {
        Template template = new Template("name", new StringReader(email.getTemplate()), new Configuration());
        String htmlBody = FreeMarkerTemplateUtils.processTemplateIntoString(template, params);
        this.send(to, email.getSubject(), htmlBody);
    }

    public void send(String to, String subject, String content) throws MessagingException {
        MimeMessage message = new MimeMessage(SESSION);
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(EMAIL_SENDER);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        Transport.send(message);
    }

    @Override
    public boolean shouldReloadOnUpdate(Set<ToolConfigurationKeyValue> keyValueSet) {
        return keyValueSet.stream().anyMatch((prop) -> prop.getKey().startsWith("mail"));
    }


    @Override
    public void configure(Properties properties) {
        this.setEmailServerProperties(properties);
    }

    @Override
    public Set<ToolConfigurationProperty> initialize() {
        return properties.stream()
                .map((value) -> new ToolConfigurationProperty(value.getKey(), this.environment.getProperty(value.getKey()), value.isRequired()))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<ToolConfigurationKey> getConfigurationKeys() {
        return new HashSet<>(properties);
    }

    @Override
    public OpAck<Void> checkServiceStatus() {
        try {
            Transport transport = this.SESSION.getTransport("smtp");
            transport.connect();
            transport.close();
            return new OpAck<>(OpAck.AckStatus.SUCCESS, "Server healthy", null,  this.getServiceDisplayName());
        } catch (Exception e) {
            return new OpAck<>(OpAck.AckStatus.FAILED, e.getMessage(), null, this.getServiceDisplayName());
        }
    }

    @Override
    public String getServiceDisplayName() {
        return "EMAIL_SERVER";
    }
}
