package gov.nist.healthcare.iz.darq.controller.route;

import gov.nist.healthcare.auth.domain.Account;
import gov.nist.healthcare.auth.service.AccountService;
import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisReport;
import gov.nist.healthcare.iz.darq.controller.domain.ReportDescriptor;
import gov.nist.healthcare.iz.darq.model.AnalysisJob;
import gov.nist.healthcare.iz.darq.repository.AnalysisJobRepository;
import gov.nist.healthcare.iz.darq.service.FacilityService;
import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;
import gov.nist.healthcare.iz.darq.service.exception.OperationFailureException;
import gov.nist.healthcare.iz.darq.repository.AnalysisReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private AnalysisReportRepository analysisReportRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AnalysisJobRepository jobRepository;
    @Autowired
    private FacilityService facilityService;

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
            report.setViewOnly(!a.getUsername().equals(report.getOwner()) || report.isPublished());
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

            if(existing.isPublished()) {
                throw new OperationFailureException("Report "+report.getId()+" is published and can't be edited");
            }
        }
        AnalysisReport saved = this.analysisReportRepository.save(report);
        return new OpAck<>(OpAck.AckStatus.SUCCESS, "Report Successfully Saved", saved, "report-save");
    }

    //  List Of Published
    @RequestMapping(value="/published/{facilityId}", method=RequestMethod.POST)
    @ResponseBody
    public List<ReportDescriptor> published(@PathVariable("facilityId") String id) throws OperationFailureException, NotFoundException {
        Account a = this.accountService.getCurrentUser();
        if(this.facilityService.canSeeFacility(id, a)) {
            return analysisReportRepository.findByPublishedAndFacilityId(true, id).stream().map(ReportDescriptor::new).collect(Collectors.toList());
        } else {
            throw new NotFoundException("Facility "+ id + " not found");
        }
    }

    //  List Of Published
    @RequestMapping(value="/published", method=RequestMethod.POST)
    @ResponseBody
    public List<ReportDescriptor> published() throws OperationFailureException, NotFoundException {
        Account a = this.accountService.getCurrentUser();
        return analysisReportRepository.findByPublishedAndOwnerAndFacilityId(true, a.getUsername(), null).stream().map(ReportDescriptor::new).collect(Collectors.toList());
    }

    //  Publish Report Template (Owned and not Locked or New)
    @RequestMapping(value="/publish/{reportId}", method=RequestMethod.POST)
    @ResponseBody
    public OpAck<AnalysisReport> publish(@PathVariable("reportId") String id) throws OperationFailureException, NotFoundException {
        Account a = this.accountService.getCurrentUser();
        AnalysisReport report = analysisReportRepository.findByIdAndOwner(id, a.getUsername());
        if(report == null){
            throw new NotFoundException("Report Template "+id+" Not Found");
        }

        if(report.isPublished()) {
            throw new OperationFailureException("Report "+report.getId()+" is published and can't be edited");
        }
        report.setPublished(true);
        report.setPublishDate(new Date());
        AnalysisReport saved = this.analysisReportRepository.save(report);
        List<AnalysisJob> jobs = this.jobRepository.findByReportId(report.getId());
        jobs.forEach((j) -> this.jobRepository.delete(j.getId()));
        saved.setViewOnly(!a.getUsername().equals(report.getOwner()) || report.isPublished());
        return new OpAck<>(OpAck.AckStatus.SUCCESS, "Report Successfully Published", saved, "report-publish");
    }

    //  Publish Report Template (Owned and not Locked or New)
    @RequestMapping(value="/{reportId}", method=RequestMethod.DELETE)
    @ResponseBody
    public OpAck<AnalysisReport> delete(@PathVariable("reportId") String id) throws OperationFailureException, NotFoundException {
        Account a = this.accountService.getCurrentUser();
        AnalysisReport report = analysisReportRepository.findByIdAndOwnerAndPublished(id, a.getUsername(), true);
        if(report == null){
            throw new NotFoundException("Report  "+id+" Not Found");
        }

        this.analysisReportRepository.delete(id);
        return new OpAck<>(OpAck.AckStatus.SUCCESS, "Report Successfully Deleted", null, "report-delete");
    }
}
