package gov.nist.healthcare.iz.darq.boot;

import gov.nist.healthcare.iz.darq.analyzer.model.template.ReportTemplate;
import gov.nist.healthcare.iz.darq.configuration.exception.InvalidConfigurationPayload;
import gov.nist.healthcare.iz.darq.model.DigestConfiguration;
import gov.nist.healthcare.iz.darq.model.UserUploadedFile;
import gov.nist.healthcare.iz.darq.repository.ADFMetaDataRepository;
import gov.nist.healthcare.iz.darq.repository.DigestConfigurationRepository;
import gov.nist.healthcare.iz.darq.repository.TemplateRepository;
import gov.nist.healthcare.iz.darq.service.utils.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ConfigurationVerificationIntegrityCheck {

    @Autowired
    ConfigurationService configurationService;
    @Autowired
    DigestConfigurationRepository configurationRepository;
    @Autowired
    ADFMetaDataRepository adfMetaDataRepository;
    @Autowired
    TemplateRepository templateRepository;

    void check() throws Exception {
        checkADFs();
        checkConfigurations();
        checkTemplates();
    }

    void checkADFs() throws Exception {
        for(UserUploadedFile adf: adfMetaDataRepository.findAll()) {
            try {
                this.configurationService.validateConfigurationPayload(adf.getConfiguration(), false);
            } catch (InvalidConfigurationPayload invalidConfigurationPayload) {
                throw new Exception("Invalid ADF (Configuration Payload) ID '" + adf.getId() +"' due to : " + invalidConfigurationPayload.getMessage());
            }
        }
    }

    void checkTemplates() throws Exception {
        for(ReportTemplate template: templateRepository.findAll()) {
            try {
                this.configurationService.validateConfigurationPayload(template.getConfiguration(), false);
            } catch (InvalidConfigurationPayload invalidConfigurationPayload) {
                throw new Exception("Invalid Report Template (Configuration Payload) ID '" + template.getId() +"' due to : " + invalidConfigurationPayload.getMessage());
            }
        }
    }

    void checkConfigurations() throws Exception {
        for(DigestConfiguration configuration: configurationRepository.findAll()) {
            try {
                this.configurationService.validateConfigurationPayload(configuration.getPayload(), false);
            } catch (InvalidConfigurationPayload invalidConfigurationPayload) {
                throw new Exception("Invalid Digest Configuration ID '" + configuration.getId() +"' due to : " + invalidConfigurationPayload.getMessage());
            }
        }
    }
}
