package gov.nist.healthcare.iz.darq.users.service.impl;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisReport;
import gov.nist.healthcare.iz.darq.analyzer.model.template.ReportTemplate;
import gov.nist.healthcare.iz.darq.model.AnalysisJob;
import gov.nist.healthcare.iz.darq.model.DigestConfiguration;
import gov.nist.healthcare.iz.darq.model.Facility;
import gov.nist.healthcare.iz.darq.model.UserUploadedFile;
import gov.nist.healthcare.iz.darq.repository.*;
import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;
import gov.nist.healthcare.iz.darq.service.impl.ADFStorage;
import gov.nist.healthcare.iz.darq.users.domain.User;
import gov.nist.healthcare.iz.darq.users.domain.UserEditToken;
import gov.nist.healthcare.iz.darq.users.repository.UserAccountRepository;
import gov.nist.healthcare.iz.darq.users.repository.UserEditTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class UserCleanupService {

    @Autowired
    ADFMetaDataRepository adfMetaDataRepository;
    @Autowired
    FacilityRepository facilityRepository;
    @Autowired
    ADFStorage adfStorage;
    @Autowired
    DigestConfigurationRepository digestConfigurationRepository;
    @Autowired
    TemplateRepository templateRepository;
    @Autowired
    AnalysisJobRepository analysisJobRepository;
    @Autowired
    AnalysisReportRepository reportRepository;
    @Autowired
    UserManagementService userManagementService;
    @Autowired
    UserAccountRepository userAccountRepository;
    @Autowired
    UserEditTokenRepository userEditTokenRepository;

    public User deleteUser(String userId) throws NotFoundException {
        User user = this.userManagementService.findUserById(userId);
        if(user == null) {
            throw new NotFoundException("User " + userId + " not found");
        } else if( user.getUsername() != null && user.getUsername().equals("admin")) {
            throw new AccessDeniedException("Unauthorized action, admin user can't be modified");
        } else {
            List<DigestConfiguration> configurations = this.digestConfigurationRepository.findByOwnerId(userId);
            List<ReportTemplate> reportTemplates = this.templateRepository.findByOwnerId(userId);
            List<UserUploadedFile> userUploadedFiles = this.adfMetaDataRepository.findByOwnerIdAndFacilityIdIsNull(userId);
            List<AnalysisJob> analysisJobs = this.analysisJobRepository.findByOwnerIdAndFacilityIdIsNull(userId);
            List<AnalysisReport> analysisReports = this.reportRepository.findByOwnerIdAndFacilityIdIsNull(userId);
            List<UserEditToken> userTokens = this.userEditTokenRepository.findByUserId(userId);

            this.digestConfigurationRepository.delete(configurations);
            this.templateRepository.delete(reportTemplates);
            userUploadedFiles.forEach((adf) -> {
                try {
                    this.adfStorage.delete(adf.getId());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            this.adfMetaDataRepository.delete(userUploadedFiles);
            this.analysisJobRepository.delete(analysisJobs);
            this.reportRepository.delete(analysisReports);
            List<Facility> facilities = this.facilityRepository.findByMembersContaining(userId);
            facilities.forEach((facility) -> facility.getMembers().remove(userId));
            this.facilityRepository.save(facilities);
            this.userEditTokenRepository.delete(userTokens);
            this.userAccountRepository.delete(userId);
            return user;
        }
    }
}
