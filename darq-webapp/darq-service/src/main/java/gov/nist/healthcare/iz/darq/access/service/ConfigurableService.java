package gov.nist.healthcare.iz.darq.access.service;

import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.iz.darq.model.ToolConfigurationKey;
import gov.nist.healthcare.iz.darq.model.ToolConfigurationKeyValue;
import gov.nist.healthcare.iz.darq.model.ToolConfigurationProperty;

import java.util.Properties;
import java.util.Set;

public interface ConfigurableService {
    boolean shouldReloadOnUpdate(Set<ToolConfigurationKeyValue> keyValueSet);
    void configure(Properties properties) throws Exception;
    Set<ToolConfigurationProperty> initialize();
    Set<ToolConfigurationKey> getConfigurationKeys();
    OpAck<Void> checkServiceStatus();
    String getServiceDisplayName();
}
