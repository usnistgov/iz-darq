package gov.nist.healthcare.iz.darq.boot;

import gov.nist.healthcare.iz.darq.service.impl.ADFMergeJobManagementService;
import gov.nist.healthcare.iz.darq.service.impl.AnalysisJobManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DataBootstrap {

    @Autowired
    UserAccountAndRoleDataIntegrityInitializer userAccountAndRoleDataIntegrityInitializer;
    @Autowired
    UpdateAnalysisReportRepository updateAnalysisReportRepository;
    @Autowired
    SetUserIdIDataIntegrityInitializer setUserIdIDataIntegrityInitializer;
    @Autowired
    ConfigurationVerificationIntegrityCheck configurationVerificationIntegrityCheck;
	@Autowired
	AnalysisJobManagementService analysisJobManagementService;
	@Autowired
	ADFMergeJobManagementService adfMergeJobManagementService;

    @PostConstruct
	public void dataIntegrityChecksAndUpdates() throws Exception {
		this.updateAnalysisReportRepository.update();
		this.userAccountAndRoleDataIntegrityInitializer.initializeAndFixAccounts();
		this.setUserIdIDataIntegrityInitializer.setUserId();
		this.configurationVerificationIntegrityCheck.check();
		this.analysisJobManagementService.updateStaleRunningAndQueuedJobStatusOnStartup();
		this.adfMergeJobManagementService.updateStaleRunningAndQueuedJobStatusOnStartup();
	}

}
