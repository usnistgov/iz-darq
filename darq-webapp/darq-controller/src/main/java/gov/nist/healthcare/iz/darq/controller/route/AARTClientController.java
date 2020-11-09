package gov.nist.healthcare.iz.darq.controller.route;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisReport;
import gov.nist.healthcare.iz.darq.auth.aart.JWTAuthenticationAARTClientFilter;
import gov.nist.healthcare.iz.darq.model.Facility;
import gov.nist.healthcare.iz.darq.model.FacilityDescriptor;
import gov.nist.healthcare.iz.darq.repository.AnalysisReportRepository;
import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;
import gov.nist.healthcare.iz.darq.users.facility.service.FacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/aart/client")
public class AARTClientController {

    @Autowired
    private FacilityService facilityService;
    @Autowired
    private AnalysisReportRepository analysisReportRepository;
    @Autowired
    private Environment environment;

    @RequestMapping(value = "/facilities", method = RequestMethod.GET)
    @ResponseBody
    public List<FacilityDescriptor> getFacilities() {
        return this.facilityService.getFacilities();
    }

    @RequestMapping(value = "/report/facility/name/{name}", method = RequestMethod.GET)
    @ResponseBody
    public List<AnalysisReport> getReportsByName(@PathVariable("name") String name) throws NotFoundException {
        Facility facility = this.facilityService.findByName(name);
        if(facility == null) {
            throw new NotFoundException("No facility with name " + name + " found");
        } else {
            return this.analysisReportRepository.findByPublishedAndFacilityId(true, facility.getId());
        }
    }

    @RequestMapping(value = "/report/facility/{id}", method = RequestMethod.GET)
    @ResponseBody
    public List<AnalysisReport> getReportsById(@PathVariable("id") String id) {
        return this.analysisReportRepository.findByPublishedAndFacilityId(true, id);
    }

    @RequestMapping(value = "/report/all", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, List<AnalysisReport>> getAllReports() {
        return this.analysisReportRepository.findByPublishedAndFacilityIdNotNull(true).stream().collect(
                Collectors.groupingBy(AnalysisReport::getFacilityId, Collectors.toList())
        );
    }

}
