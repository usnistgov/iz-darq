package gov.nist.healthcare.iz.darq.access.configuration;

import gov.nist.healthcare.iz.darq.access.security.SimpleResourceQualifier;
import gov.nist.healthcare.iz.darq.access.security.CustomMethodSecurityExpressionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AccessControlConfiguration extends GlobalMethodSecurityConfiguration {

    @Autowired
    SimpleResourceQualifier resourceQualifier;

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        return new CustomMethodSecurityExpressionHandler(resourceQualifier);
    }

}
