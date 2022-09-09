package gov.nist.healthcare.iz.darq.boot;

import com.mongodb.gridfs.GridFSFile;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisReport;
import gov.nist.healthcare.iz.darq.repository.PartialAnalysisReportRepository;
import gov.nist.healthcare.iz.darq.service.impl.AnalysisReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class UpdateAnalysisReportRepository {

    @Autowired
    PartialAnalysisReportRepository partialAnalysisReportRepository;
    @Autowired
    AnalysisReportService analysisReportService;
    @Autowired
    private GridFsTemplate gridFsTemplate;

    public void revert() {
        List<AnalysisReport> reportList = partialAnalysisReportRepository.findAll();
        for(AnalysisReport report: reportList) {
            AnalysisReport r = analysisReportService.findOne(report.getId());
            report.setSections(r.getSections());
            partialAnalysisReportRepository.save(report);
        }
    }

    public void update() throws IOException {
        List<AnalysisReport> reportList = partialAnalysisReportRepository.findAll();
        for(AnalysisReport report: reportList) {
            GridFSFile file = gridFsTemplate.findOne(this.analysisReportService.getQueryById(report.getId()));
            if(file == null) {
                analysisReportService.save(report);
            }
        }
    }
}
