package gov.nist.healthcare.iz.darq.service.impl;

import com.google.common.base.Strings;
import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.iz.darq.access.service.ConfigurableService;
import gov.nist.healthcare.iz.darq.model.ToolConfiguration;
import gov.nist.healthcare.iz.darq.model.ToolConfigurationKey;
import gov.nist.healthcare.iz.darq.model.ToolConfigurationProperty;
import gov.nist.healthcare.iz.darq.model.ToolConfigurationKeyValue;
import gov.nist.healthcare.iz.darq.repository.ToolConfigurationRepository;
import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;
import gov.nist.healthcare.iz.darq.service.exception.PropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ToolConfigurationService {

    @Autowired
    private ToolConfigurationRepository toolConfigurationRepository;
    @Autowired
    private List<ConfigurableService> configurableServiceList;

    public ToolConfiguration getToolConfiguration() {
        ToolConfiguration toolConfiguration = this.toolConfigurationRepository.findOne(ToolConfiguration.TOOL_CONFIGURATION_ID);
        if(toolConfiguration == null) {
            toolConfiguration = new ToolConfiguration();
            this.save(toolConfiguration);
        }
        return toolConfiguration;
    }

    public ToolConfiguration save(ToolConfiguration toolConfiguration) {
        toolConfiguration.setId(ToolConfiguration.TOOL_CONFIGURATION_ID);
        return this.toolConfigurationRepository.save(toolConfiguration);
    }

    public void setPropertyValue(ToolConfigurationKeyValue keyValue) {
        ToolConfiguration toolConfiguration = this.getToolConfiguration();
        this.get(toolConfiguration, keyValue.getKey()).ifPresent((tc) -> {
            tc.setValue(keyValue.getValue());
        });
        this.save(toolConfiguration);
    }

    public void setIfAbsent(ToolConfigurationProperty property) throws NotFoundException, PropertyException {
        ToolConfiguration toolConfiguration = this.getToolConfiguration();
        if(toolConfiguration == null) {
            throw new NotFoundException("Tool configuration not found");
        } else {
            if(property == null || Strings.isNullOrEmpty(property.getKey())) {
                throw new PropertyException("Property must not be null");
            }

            if(property.isRequired() && Strings.isNullOrEmpty(property.getValue())) {
                throw new PropertyException("Property " + property.getKey() + " is required");
            }

            boolean exists = toolConfiguration.getProperties()
                    .stream()
                    .anyMatch((key) -> key.getKey().equals(property.getKey()));

            if(!exists) {
                ToolConfigurationProperty toolConfigurationProperty = new ToolConfigurationProperty();
                toolConfigurationProperty.setKey(property.getKey());
                toolConfigurationProperty.setValue(property.getValue());
                toolConfigurationProperty.setRequired(property.isRequired());
                toolConfiguration.getProperties().add(toolConfigurationProperty);
            }
        }
        this.save(toolConfiguration);
    }

    public Properties getPropertiesObject(Set<String> keys) throws NotFoundException {
        ToolConfiguration toolConfiguration = this.getToolConfiguration();
        Properties properties = new Properties();
        Set<String> get = new HashSet<>(keys);

        toolConfiguration.getProperties().forEach(property -> {
            if(get.contains(property.getKey()) && !Strings.isNullOrEmpty(property.getValue())) {
                properties.put(property.getKey(), property.getValue());
                get.remove(property.getKey());
            }
        });
        return properties;
    }

    public Set<OpAck<Void>> updateProperties(Set<ToolConfigurationKeyValue> keyValues) throws PropertyException {
        ToolConfiguration toolConfiguration = this.getToolConfiguration();
        for(ToolConfigurationKeyValue kv: keyValues) {
            this.checkForUpdate(toolConfiguration, kv);
        }

        // Everything OK
        keyValues.forEach(kv -> {
            try {
                this.get(toolConfiguration, kv.getKey()).ifPresent((tc) -> tc.setValue(kv.getValue()));
            } catch (Exception ignored) {
            }
        });
        this.save(toolConfiguration);
        return this.update(keyValues);
    }

    public Optional<ToolConfigurationProperty> get(ToolConfiguration toolConfiguration, String key) {
        return toolConfiguration.getProperties()
                .stream()
                .filter((prop) -> prop.getKey().equals(key))
                .findAny();
    }

    public ToolConfigurationProperty checkForUpdate(ToolConfiguration toolConfiguration, ToolConfigurationKeyValue keyValue) throws PropertyException {
        Optional<ToolConfigurationProperty> property = this.get(toolConfiguration, keyValue.getKey());
        if(property.isPresent()) {
            if(Strings.isNullOrEmpty(keyValue.getValue()) && property.get().isRequired()) {
                throw new PropertyException("Property " + keyValue.getKey() + " is required");
            }
            return property.get();
        } else {
            throw new PropertyException("Only existing properties can be updated");
        }
    }

    public Set<OpAck<Void>> update(Set<ToolConfigurationKeyValue> keyValues) {
        Set<ConfigurableService> configurableServices = this.configurableServiceList
                .stream()
                .filter((service) -> service.shouldReloadOnUpdate(keyValues))
                .collect(Collectors.toSet());

        return configurableServices.stream().map((service) -> {
            try {
                service.configure(
                    this.getPropertiesObject(service.getConfigurationKeys()
                        .stream()
                        .map(ToolConfigurationKey::getKey)
                        .collect(Collectors.toSet())
                    )
                );
                return service.checkServiceStatus();
            } catch (Exception e) {
                return new OpAck<Void>(OpAck.AckStatus.FAILED, e.getMessage(), null, service.getServiceDisplayName());
            }
        }).collect(Collectors.toSet());
    }

    public Set<OpAck<Void>> checkConfigurationStatus() {
        return this.configurableServiceList
                .stream()
                .map(ConfigurableService::checkServiceStatus)
                .collect(Collectors.toSet());
    }

    @PostConstruct
    private void configure() throws Exception {
        for(ConfigurableService service: configurableServiceList) {
            Set<ToolConfigurationProperty> props = service.initialize();
            for (ToolConfigurationProperty prop : props) {
                this.setIfAbsent(prop);
            }
            try {
                service.configure(
                        this.getPropertiesObject(service.getConfigurationKeys()
                                .stream()
                                .map(ToolConfigurationKey::getKey)
                                .collect(Collectors.toSet())
                        )
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
