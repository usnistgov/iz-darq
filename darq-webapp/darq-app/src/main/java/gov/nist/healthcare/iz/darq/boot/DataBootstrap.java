package gov.nist.healthcare.iz.darq.boot;

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

    @PostConstruct
	public void dataIntegrityChecksAndUpdates() throws Exception {
		this.updateAnalysisReportRepository.update();
		this.userAccountAndRoleDataIntegrityInitializer.initializeAndFixAccounts();
		this.setUserIdIDataIntegrityInitializer.setUserId();
		this.configurationVerificationIntegrityCheck.check();
	}

}
