package gov.nist.healthcare.iz.darq.access.security;

import com.google.common.base.Strings;
import gov.nist.healthcare.domain.trait.AssignableToFacility;
import gov.nist.healthcare.domain.trait.Owned;
import gov.nist.healthcare.domain.trait.Publishable;
import gov.nist.healthcare.iz.darq.access.domain.*;
import gov.nist.healthcare.iz.darq.repository.AnalysisJobRepository;
import gov.nist.healthcare.iz.darq.repository.DigestConfigurationRepository;
import gov.nist.healthcare.iz.darq.repository.TemplateRepository;
import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;
import gov.nist.healthcare.iz.darq.service.impl.ADFStorage;
import gov.nist.healthcare.iz.darq.service.impl.AnalysisReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SimpleResourceQualifier {

    @Autowired
    ADFStorage adfStorage;
    @Autowired
    DigestConfigurationRepository digestConfigurationRepository;
    @Autowired
    TemplateRepository templateRepository;
    @Autowired
    AnalysisJobRepository analysisJobRepository;
    @Autowired
    AnalysisReportService analysisReportService;

    public ResourceSecurityWrapper getSecurityQualifiedResource(ResourceType type, String id) throws NotFoundException {
        Object resource = this.getResource(type, id);
        if(resource == null) {
            throw new NotFoundException(type.name(), id);
        } else {
            return new ResourceSecurityWrapper(type, this.getQualifiedAccessToken(resource), this.getQualifiedScope(resource), resource);
        }
    }

    public ResourceSecurityWrapper getSecurityQualifiedResource(ResourceType type, Object resource) {
        return new ResourceSecurityWrapper(type, this.getQualifiedAccessToken(resource), this.getQualifiedScope(resource), resource);
    }


    public Object getResource(ResourceType type, String id) {
        switch (type) {
            case ADF:
                return this.adfStorage.get(id);
            case CONFIGURATION:
                return this.digestConfigurationRepository.findOne(id);
            case REPORT_TEMPLATE:
                return this.templateRepository.findOne(id);
            case ANALYSIS_JOB:
                return this.analysisJobRepository.findOne(id);
            case REPORT:
                return this.analysisReportService.findOne(id);
        }
        return null;
    }

    public QualifiedScope getQualifiedScope(Object resource) {
        if(resource instanceof AssignableToFacility && !Strings.isNullOrEmpty(((AssignableToFacility) resource).getFacilityId())) {
            return new QualifiedScope(Scope.FACILITY, ((AssignableToFacility) resource).getFacilityId());
        } else {
            return new QualifiedScope(Scope.GLOBAL);
        }
    }

    public QualifiedAccessToken getQualifiedAccessToken(Object resource) {
        if(resource instanceof Publishable && ((Publishable) resource).isPublic()) {
            return new QualifiedAccessToken(AccessToken.PUBLIC);
        }
        if(resource instanceof Owned) {
            return new QualifiedAccessToken(AccessToken.OWNER, ((Owned) resource).getOwnerId());
        }
        return new QualifiedAccessToken(AccessToken.ANY);
    }
}
