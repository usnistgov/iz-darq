package gov.nist.healthcare.iz.darq.controller.service;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisReport;
import gov.nist.healthcare.iz.darq.analyzer.model.template.ReportTemplate;
import gov.nist.healthcare.iz.darq.model.*;
import gov.nist.healthcare.iz.darq.users.service.impl.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DescriptorService {

    @Autowired
    private UserManagementService userManagementService;

    public ConfigurationDescriptor getConfigurationDescriptor(DigestConfiguration config) {
        return new ConfigurationDescriptor(
                config.getId(),
                config.getName(),
                config.getOwner(),
                config.getOwnerId(),
                this.userManagementService.getUserDisplayName(config.getOwnerId()),
                config.getLastUpdated(),
                config.isLocked(),
                config.isPublished());
    }

    public TemplateDescriptor getTemplateDescriptor(ReportTemplate reportTemplate, List<DigestConfiguration> compatibilities) {
        return new TemplateDescriptor(
                reportTemplate.getId(),
                reportTemplate.getName(),
                reportTemplate.getOwner(),
                reportTemplate.getOwnerId(),
                this.userManagementService.getUserDisplayName(reportTemplate.getOwnerId()),
                compatibilities.stream().map(this::getConfigurationDescriptor).collect(Collectors.toList()),
                reportTemplate.isPublic()
        );
    }

    public QueryDescriptor getQueryDescriptor(Query query, List<DigestConfiguration> compatibilities) {
        return new QueryDescriptor(
                query.getId(),
                query.getName(),
                query.getOwner(),
                query.getOwnerId(),
                this.userManagementService.getUserDisplayName(query.getOwnerId()),
                compatibilities.stream().map(this::getConfigurationDescriptor).collect(Collectors.toList())
        );
    }

    public ADFDescriptor getADFDescriptor(UserUploadedFile userUploadedFile, List<DigestConfiguration> compatibilities) {
        return new ADFDescriptor(
                userUploadedFile.getId(),
                userUploadedFile.getName(),
                userUploadedFile.getTags(),
                userUploadedFile.getOwner(),
                this.userManagementService.getUserDisplayName(userUploadedFile.getOwnerId()),
                userUploadedFile.getOwnerId(),
                userUploadedFile.getAnalysedOn(),
                userUploadedFile.getUploadedOn(),
                userUploadedFile.getSize(),
                compatibilities.stream().map(this::getConfigurationDescriptor).collect(Collectors.toList()),
                userUploadedFile.getVersion(),
                userUploadedFile.getBuild(),
                userUploadedFile.getMqeVersion(),
                userUploadedFile.getFacilityId(),
                userUploadedFile.getComponents()
        );
    }

    public ReportDescriptor getReportDescriptor(AnalysisReport report, List<DigestConfiguration> compatibilities) {
        return new ReportDescriptor(
                report.getId(),
                report.getName(),
                report.getDescription(),
                this.getTemplateDescriptor(report.getReportTemplate(), compatibilities),
                report.getOwner(),
                report.getOwnerId(),
                this.userManagementService.getUserDisplayName(report.getOwnerId()),
                report.isPublic(),
                report.getLastUpdated(),
                report.getFacilityId(),
                report.getAdfName(),
                report.getPublishDate()
        );
    }

    public AnalysisJobDescriptor getAnalysisJobDescriptor(AnalysisJob job, List<DigestConfiguration> compatibilities) {
        return new AnalysisJobDescriptor(
                job.getId(),
                job.getName(),
                job.getAdfId(),
                job.getAdfName(),
                this.getTemplateDescriptor(job.getTemplate(), compatibilities),
                job.getSubmitTime(),
                job.getStartTime(),
                job.getEndTime(),
                job.getStatus(),
                job.getOwner(),
                job.getOwnerId(),
                this.userManagementService.getUserDisplayName(job.getOwnerId()),
                job.getFailure(),
                job.getReportId(),
                job.getFacilityId()
        );
    }
}
