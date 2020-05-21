package gov.nist.healthcare.iz.darq.controller.route;

import gov.nist.healthcare.auth.domain.Account;
import gov.nist.healthcare.auth.service.AccountService;
import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisReport;
import gov.nist.healthcare.iz.darq.controller.exception.NotFoundException;
import gov.nist.healthcare.iz.darq.controller.exception.OperationFailureException;
import gov.nist.healthcare.iz.darq.repository.AnalysisReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private AnalysisReportRepository analysisReportRepository;
    @Autowired
    private AccountService accountService;

    //  Get Report Template by Id (Owned or Published [viewOnly])
    @RequestMapping(value="/{id}", method=RequestMethod.GET)
    @ResponseBody
    public AnalysisReport get(@PathVariable("id") String id) throws NotFoundException {
        Account a = this.accountService.getCurrentUser();
        AnalysisReport report = analysisReportRepository.findOne(id);
        if(report == null || (!report.getOwner().equals(a.getUsername()) && !report.isPublished())){
            throw new NotFoundException("Report Template "+id+" Not Found");
        }
        else {
            report.setViewOnly(!a.getUsername().equals(report.getOwner()));
            return report;
        }
    }

    //  Save Report Template (Owned and not Locked or New)
    @RequestMapping(value="/", method=RequestMethod.POST)
    @ResponseBody
    public OpAck<AnalysisReport> save(@RequestBody AnalysisReport report) throws OperationFailureException {
        Account a = this.accountService.getCurrentUser();
        if(report.getId() == null || report.getId().isEmpty()){
            throw new OperationFailureException("Can't save new report");
        } else {
            AnalysisReport existing = this.analysisReportRepository.findByIdAndOwner(report.getId(), a.getUsername());
            if(existing == null) {
                throw new OperationFailureException("Can't save report "+report.getId());
            }
        }
        AnalysisReport saved = this.analysisReportRepository.save(report);
        return new OpAck<>(OpAck.AckStatus.SUCCESS, "Report Successfully Saved", saved, "report-save");
    }
}
