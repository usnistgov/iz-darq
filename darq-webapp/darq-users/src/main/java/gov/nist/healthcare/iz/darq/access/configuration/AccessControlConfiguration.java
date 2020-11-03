package gov.nist.healthcare.iz.darq.access.configuration;

import gov.nist.healthcare.iz.darq.access.method.security.CustomMethodSecurityExpressionHandler;
import gov.nist.healthcare.iz.darq.access.service.PermissionMatcher;
import gov.nist.healthcare.iz.darq.access.service.impl.SimpleAccessControlService;
import gov.nist.healthcare.iz.darq.access.service.impl.SimplePermissionMatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import java.util.ArrayList;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AccessControlConfiguration extends GlobalMethodSecurityConfiguration {

    @Bean
    public PermissionMatcher permissionMatcher() {
        return new SimplePermissionMatcher();
    }

    @Bean
    public SimpleAccessControlService accessControlService(PermissionMatcher permissionMatcher) {
        return new SimpleAccessControlService(permissionMatcher, new ArrayList<>());
    }

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        return new CustomMethodSecurityExpressionHandler(accessControlService(permissionMatcher()));
    }


}
