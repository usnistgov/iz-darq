package gov.nist.healthcare.iz.darq.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSFile;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisReport;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.ReportSectionResult;
import gov.nist.healthcare.iz.darq.repository.PartialAnalysisReportRepository;
import gov.nist.healthcare.iz.darq.service.domain.AnalysisReportMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnalysisReportService {

    @Autowired
    private PartialAnalysisReportRepository analysisReportRepository;
    @Autowired
    private GridFsTemplate gridFsTemplate;

    private final ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private final String TYPE = "ANALYSIS_REPORT_SECTIONS";

    DBObject createDBObjectMetadata(AnalysisReportMetadata analysisReportMetadata) {
        DBObject metaData = new BasicDBObject();
        metaData.put("id", analysisReportMetadata.getId());
        metaData.put("ownerId", analysisReportMetadata.getOwnerId());
        metaData.put("templateId", analysisReportMetadata.getTemplateId());
        metaData.put("published", analysisReportMetadata.isPublished());
        metaData.put("type", TYPE);
        return metaData;
    }

    public Query getQueryById(String id) {
        Criteria criteria = new Criteria();
        criteria.andOperator(
                Criteria.where("metadata.id").is(id),
                Criteria.where("metadata.type").is(TYPE)
        );
        return new Query(criteria);
    }

    public AnalysisReport save(AnalysisReport report) throws IOException {
        List<ReportSectionResult> sections = report.getSections();
        report.setSections(null);
        AnalysisReport saved = this.analysisReportRepository.save(report);
        Query query = getQueryById(saved.getId());

        // Delete from GridFS If exists
        GridFSFile file = gridFsTemplate.findOne(query);
        if(file != null) {
            gridFsTemplate.delete(query);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mapper.writeValue(baos, sections);
        DBObject metadata = createDBObjectMetadata(new AnalysisReportMetadata(saved));
        gridFsTemplate.store(new ByteArrayInputStream(baos.toByteArray()), saved.getId() + ".json", metadata);
        saved.setSections(sections);
        return saved;
    }

    public void remove(AnalysisReport report) {
        gridFsTemplate.delete(getQueryById(report.getId()));
        analysisReportRepository.delete(report.getId());
    }

    public void delete(String id) {
        gridFsTemplate.delete(getQueryById(id));
        analysisReportRepository.delete(id);
    }

    AnalysisReport updateSectionsFromGridFS(AnalysisReport analysisReport) throws IOException {
        GridFSFile file = gridFsTemplate.findOne(getQueryById(analysisReport.getId()));
        if(file != null) {
            GridFsResource resource = gridFsTemplate.getResource(file.getFilename());
            List<ReportSectionResult> sections = mapper.readValue(resource.getInputStream(), new TypeReference<List<ReportSectionResult>>(){});
            analysisReport.setSections(sections);
        } else {
            analysisReport.setSections(new ArrayList<>());
        }
        return analysisReport;
    }

    public void delete(List<AnalysisReport> reports) {
        for(AnalysisReport ar: reports) {
            this.remove(ar);
        }
    }

    public AnalysisReport findOne(String id) {
        AnalysisReport report = this.analysisReportRepository.findOne(id);
        if(report != null) {
            try {
                return this.updateSectionsFromGridFS(report);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<AnalysisReport> findAll() {
        List<AnalysisReport> reports = this.analysisReportRepository.findAll();
        return this.getFromGridFs(reports);
    }

    public AnalysisReport findByIdAndOwnerId(String id, String ownerId) {
        AnalysisReport report = this.analysisReportRepository.findByIdAndOwnerId(id, ownerId);
        if(report != null) {
            try {
                return this.updateSectionsFromGridFS(report);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public AnalysisReport findByIdAndOwnerIdAndPublished(String id, String ownerId, boolean published) {
        AnalysisReport report = this.analysisReportRepository.findByIdAndOwnerIdAndPublished(id, ownerId, published);
        if(report != null) {
            try {
                return this.updateSectionsFromGridFS(report);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public AnalysisReport findByIdAndPublished(String id, boolean published) {
        AnalysisReport report = this.analysisReportRepository.findByIdAndPublished(id, published);
        if(report != null) {
            try {
                return this.updateSectionsFromGridFS(report);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<AnalysisReport> findByPublishedAndOwnerIdAndFacilityId(boolean published, String ownerId, String facility) {
        List<AnalysisReport> reports = this.analysisReportRepository.findByPublishedAndOwnerIdAndFacilityId(published, ownerId, facility);
        return this.getFromGridFs(reports);
    }

    public List<AnalysisReport> findByPublishedAndFacilityId(boolean published, String facilityId) {
        List<AnalysisReport> reports = this.analysisReportRepository.findByPublishedAndFacilityId(published, facilityId);
        return this.getFromGridFs(reports);
    }

    public int countByPublishedAndFacilityId(boolean published, String facilityId) {
        return this.analysisReportRepository.countByPublishedAndFacilityId(published, facilityId);
    }

    public List<AnalysisReport> findByOwnerIdAndFacilityIdIsNull(String id) {
        List<AnalysisReport> reports = this.analysisReportRepository.findByOwnerIdAndFacilityIdIsNull(id);
        return this.getFromGridFs(reports);
    }

    public List<AnalysisReport> findByPublishedAndFacilityIdNotNull(boolean published) {
        List<AnalysisReport> reports = this.analysisReportRepository.findByPublishedAndFacilityIdNotNull(published);
        return this.getFromGridFs(reports);
    }

    public List<AnalysisReport> getFromGridFs(List<AnalysisReport> reports) {
        List<AnalysisReport> fileReports = new ArrayList<>();
        for(AnalysisReport report: reports) {
            try {
                fileReports.add(updateSectionsFromGridFS(report));
            } catch (IOException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        return fileReports;
    }

}
