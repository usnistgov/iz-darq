package gov.nist.healthcare.iz.darq.controller.route;

import com.google.common.base.Strings;
import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.iz.darq.access.security.CustomSecurityExpressionRoot;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisReport;
import gov.nist.healthcare.iz.darq.analyzer.model.comparison.ReportComparisonResult;
import gov.nist.healthcare.iz.darq.analyzer.service.comparison.ReportComparisonService;
import gov.nist.healthcare.iz.darq.controller.service.DescriptorService;
import gov.nist.healthcare.iz.darq.model.*;
import gov.nist.healthcare.iz.darq.repository.AnalysisJobRepository;
import gov.nist.healthcare.iz.darq.repository.DigestConfigurationRepository;
import gov.nist.healthcare.iz.darq.service.exception.OperationFailureException;
import gov.nist.healthcare.iz.darq.service.impl.AnalysisReportService;
import gov.nist.healthcare.iz.darq.service.impl.SimpleEmailService;
import gov.nist.healthcare.iz.darq.service.utils.ConfigurationService;
import gov.nist.healthcare.iz.darq.users.domain.User;
import gov.nist.healthcare.iz.darq.users.facility.service.FacilityService;
import gov.nist.healthcare.iz.darq.users.service.impl.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private AnalysisReportService analysisReportService;
    @Autowired
    private AnalysisJobRepository jobRepository;
    @Autowired
    private FacilityService facilityService;
    @Autowired
    private DescriptorService descriptorService;
    @Autowired
    private ConfigurationService configService;
    @Autowired
    private DigestConfigurationRepository confRepo;
    @Autowired
    private SimpleEmailService emailService;
    @Autowired
    private UserManagementService userManagementService;
    @Autowired
    private ReportComparisonService reportComparisonService;

    //  Get Report Template by Id (Owned or Published [viewOnly])
    @RequestMapping(value="/{id}", method=RequestMethod.GET)
    @ResponseBody
    @PreAuthorize("AccessResource(#request, REPORT, VIEW, #id)")
    public AnalysisReport get(
            HttpServletRequest request,
            @PathVariable("id") String id) {
        return (AnalysisReport) request.getAttribute(CustomSecurityExpressionRoot.RESOURCE_ATTRIBUTE);
    }

    //  Save Report Template (Owned and not Locked or New)
    @RequestMapping(value="/", method=RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("AccessResource(#request, REPORT, COMMENT, #report.id)")
    public OpAck<AnalysisReport> save(
            HttpServletRequest request,
            @RequestBody AnalysisReport report) throws OperationFailureException, IOException {
        AnalysisReport existing = (AnalysisReport) request.getAttribute(CustomSecurityExpressionRoot.RESOURCE_ATTRIBUTE);
        if(existing.isPublished()) {
            throw new OperationFailureException("Report "+report.getId()+" is published and can't be edited");
        }
        // Non Overridable fields
        report.setOwnerId(existing.getOwnerId());
        report.setFacilityId(existing.getFacilityId());
        report.setOwner(existing.getOwner());
        report.setPublished(existing.isPublished());
        report.setPublishDate(existing.getPublishDate());
        report.setLastUpdated(new Date());
        AnalysisReport saved = this.analysisReportService.save(report);
        return new OpAck<>(OpAck.AckStatus.SUCCESS, "Report Successfully Saved", saved, "report-save");
    }

    //  List Of Published
    @RequestMapping(value="/published/{facilityId}", method=RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("AccessOperation(REPORT, VIEW, FACILITY(#id), PUBLIC())")
    public List<ReportDescriptor> published(
            @AuthenticationPrincipal User user,
            @PathVariable("facilityId") String id) {
        return getReportDescriptorList(analysisReportService.findByPublishedAndFacilityId(true, id), user);
    }

    //  Publish Report Template (Owned and not Locked or New)
    @RequestMapping(value="/publish/{reportId}", method=RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("AccessResource(#request, REPORT, PUBLISH, #id)")
    public OpAck<AnalysisReport> publish(
            HttpServletRequest request,
            @PathVariable("reportId") String id) throws OperationFailureException, IOException {
        AnalysisReport report = (AnalysisReport) request.getAttribute(CustomSecurityExpressionRoot.RESOURCE_ATTRIBUTE);
        if(report.isPublished()) {
            throw new OperationFailureException("Report "+report.getId()+" is published and can't be edited");
        }
        report.setPublished(true);
        report.setPublishDate(new Date());
        AnalysisReport saved = this.analysisReportService.save(report);
        List<AnalysisJob> jobs = this.jobRepository.findByReportId(report.getId());
        jobs.forEach((j) -> this.jobRepository.delete(j.getId()));

        try {
            Facility facility = this.facilityService.getFacilityById(report.getFacilityId());
            Stream.concat(facility
                                    .getMembers()
                                    .stream()
                                    .map(this.userManagementService::findUserById),
                            this.userManagementService.getAllUsers().stream().filter(User::isAdministrator)
                    )
                    .filter(u -> u != null && !Strings.isNullOrEmpty(u.getEmail()))
                    .forEach((user) -> {
                        HashMap<String, String> params = new HashMap<>();
                        params.put("USERNAME", user.getScreenName());
                        params.put("IIS", facility.getName());
                        try {
                            this.emailService.sendIfEnabled(EmailType.REPORT_PUBLISHED, user.getEmail(), params);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new OpAck<>(OpAck.AckStatus.SUCCESS, "Report Successfully Published", saved, "report-publish");
    }

    //  Delete Report Template (Owned and not Locked or New)
    @RequestMapping(value="/{reportId}", method=RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("AccessResource(#request, REPORT, DELETE, #id)")
    public OpAck<AnalysisReport> delete(
            HttpServletRequest request,
            @PathVariable("reportId") String id) {
        this.analysisReportService.delete(id);
        return new OpAck<>(OpAck.AckStatus.SUCCESS, "Report Successfully Deleted", null, "report-delete");
    }

    // Compare Two Saved Reports by ID
    @RequestMapping(value="/compare", method=RequestMethod.GET)
    @ResponseBody
    @PreAuthorize("#ids != null and #ids.size() >= 2 and AccessResource(#request, REPORT, VIEW, #ids[0]) and AccessResource(#request, REPORT, VIEW, #ids[1])")
    public ReportComparisonResult compareByIds(
            HttpServletRequest request,
            @RequestParam("id") List<String> ids) throws OperationFailureException {

        if (ids == null || ids.size() < 2) {
            throw new OperationFailureException("Report IDs must contain at least two elements");
        }
        String baseId = ids.get(0);
        String newId = ids.get(1);

        // Assumes analysisReportService exposes a standard findById (or similar get) method
        AnalysisReport baseReport = this.analysisReportService.findOne(baseId);
        AnalysisReport newReport = this.analysisReportService.findOne(newId);

        if (baseReport == null) {
            throw new OperationFailureException("Base report for comparison (" + baseId + ") could not be found.");
        }
        if (newReport == null) {
            throw new OperationFailureException("Target report for comparison (" + newId + ") could not be found.");
        }

        return this.reportComparisonService.compare(baseReport, newReport);
    }

    // Compare Saved Report by ID with Uploaded Request Body
    @RequestMapping(value="/compare/{baseId}", method=RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("AccessResource(#request, REPORT, VIEW, #baseId)")
    public ReportComparisonResult compareWithUploaded(
            HttpServletRequest request,
            @PathVariable("baseId") String baseId,
            @RequestBody AnalysisReport uploadedReport) {

        AnalysisReport baseReport = (AnalysisReport) request.getAttribute(CustomSecurityExpressionRoot.RESOURCE_ATTRIBUTE);

        return this.reportComparisonService.compare(baseReport, uploadedReport);
    }

    List<ReportDescriptor> getReportDescriptorList(List<AnalysisReport> reports, User user) {
        List<ReportDescriptor> result = new ArrayList<>();
        List<DigestConfiguration> configurations = this.confRepo.findAccessibleTo(user.getId());
        for(AnalysisReport report : reports){
            result.add(this.descriptorService.getReportDescriptor(report, this.configService.compatibilities(report.getConfiguration(), configurations)));
        }
        return result;
    }
}