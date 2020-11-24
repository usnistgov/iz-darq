package gov.nist.healthcare.iz.darq.boot;

import com.google.common.base.Strings;
import gov.nist.healthcare.domain.trait.Owned;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisReport;
import gov.nist.healthcare.iz.darq.analyzer.model.template.ReportTemplate;
import gov.nist.healthcare.iz.darq.model.AnalysisJob;
import gov.nist.healthcare.iz.darq.model.DigestConfiguration;
import gov.nist.healthcare.iz.darq.model.Facility;
import gov.nist.healthcare.iz.darq.model.UserUploadedFile;
import gov.nist.healthcare.iz.darq.repository.*;
import gov.nist.healthcare.iz.darq.service.exception.OperationFailureException;
import gov.nist.healthcare.iz.darq.users.domain.UserAccount;
import gov.nist.healthcare.iz.darq.users.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class SetUserIdIDataIntegrityInitializer {

    @Autowired
    ADFMetaDataRepository adfMetaDataRepository;
    @Autowired
    DigestConfigurationRepository digestConfigurationRepository;
    @Autowired
    TemplateRepository templateRepository;
    @Autowired
    AnalysisJobRepository analysisJobRepository;
    @Autowired
    AnalysisReportRepository reportRepository;
    @Autowired
    FacilityRepository facilityRepository;
    @Autowired
    UserAccountRepository userAccountRepository;

    @PostConstruct
    public void setUserId() throws OperationFailureException {
        //--- ADF
        List<UserUploadedFile> userUploadedFileList = adfMetaDataRepository.findAll();
        for(UserUploadedFile file: userUploadedFileList) {
            this.adfMetaDataRepository.save(this.handleResource(file, file.getOwner(), "ADF", file.getId()));
        }

        //--- Configuration
        List<DigestConfiguration> configurations = digestConfigurationRepository.findAll();
        for(DigestConfiguration configuration: configurations) {
            this.digestConfigurationRepository.save(this.handleResource(configuration, configuration.getOwner(), "Configuration", configuration.getId()));
        }

        //--- Template
        List<ReportTemplate> reportTemplates = templateRepository.findAll();
        for(ReportTemplate template: reportTemplates) {
            this.templateRepository.save(this.handleResource(template, template.getOwner(), "Report Template", template.getId()));
        }

        //--- Analysis Job
        List<AnalysisJob> analysisJobs = analysisJobRepository.findAll();
        for(AnalysisJob job: analysisJobs) {
            ReportTemplate reportTemplate = this.handleResource(job.getTemplate(), job.getTemplate().getOwner(), "Report Template", job.getTemplate().getId());
            job.setTemplate(reportTemplate);
            this.analysisJobRepository.save(this.handleResource(job, job.getOwner(), "Analysis Job", job.getId()));
        }

        //--- Report
        List<AnalysisReport> reports = reportRepository.findAll();
        for(AnalysisReport report: reports) {
            ReportTemplate reportTemplate = this.handleResource(report.getReportTemplate(), report.getReportTemplate().getOwner(), "Report Template", report.getReportTemplate().getId());
            report.setReportTemplate(reportTemplate);
            this.reportRepository.save(this.handleResource(report, report.getOwner(), "Analysis Report", report.getId()));
        }

        //--- Facilities
        List<Facility> facilities = facilityRepository.findAll();
        for(Facility facility: facilities) {
            Set<String> members = new HashSet<>();
            for(String member: facility.getMembers()) {
                UserAccount userByOwner = this.userAccountRepository.findByUsernameIgnoreCase(member);
                UserAccount userByOwnerId = this.userAccountRepository.findById(member);

                if(userByOwner == null && userByOwnerId == null) {
                    throw new OperationFailureException("Facility has unrecognized member " + member);
                } else {
                    members.add(userByOwner == null ? userByOwnerId.getId() : userByOwner.getId());
                }
            }
            facility.setMembers(members);
            this.facilityRepository.save(facility);
        }
    }

    <T extends Owned> T handleResource(T resource, String owner, String name, String id) throws OperationFailureException {
        String ownerId = resource.getOwnerId();
        if(Strings.isNullOrEmpty(owner) && Strings.isNullOrEmpty(ownerId)) {
            throw new OperationFailureException(name + " " + id + " has no user");
        }
        if(!Strings.isNullOrEmpty(owner)) {
            UserAccount account = this.userAccountRepository.findByUsernameIgnoreCase(owner);
            resource.setOwnerId(account.getId());
        }
        return resource;
    }

}
