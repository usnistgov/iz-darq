package gov.nist.healthcare.iz.darq.digest.service.impl;

import com.google.common.io.Files;
import gov.nist.extract.lucene.index.ExtractFileIndexer;
import gov.nist.extract.lucene.index.ExtractFileSearcher;
import gov.nist.healthcare.iz.darq.digest.service.PatientRecordIterator;
import gov.nist.healthcare.iz.darq.digest.service.exception.InvalidPatientRecord;
import gov.nist.healthcare.iz.darq.parser.model.Patient;
import gov.nist.healthcare.iz.darq.parser.model.VaccineRecord;
import gov.nist.healthcare.iz.darq.parser.service.RecordParser;
import gov.nist.healthcare.iz.darq.parser.service.model.AggregateParsedRecord;
import gov.nist.healthcare.iz.darq.parser.service.model.ParseError;
import gov.nist.healthcare.iz.darq.parser.service.model.ParsedRecord;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LucenePatientRecordIterator extends PatientRecordIterator {

    final static Logger logger = LoggerFactory.getLogger(LucenePatientRecordIterator.class.getName());
    private IndexSearcher vaccinations;
    private Iterator<String> linesIterator;
    private RecordParser parser;
    private ExtractFileSearcher searcher;
    private int lineNumber;
    private Stream<String> lines;
    private String tmpDir;

    public LucenePatientRecordIterator(Path patientFile, Path vaccinationFile) throws IOException {
        super(patientFile, vaccinationFile);
        logger.info("[RECORD ITERATOR] Initialization");

        // Index Vaccination File
        ExtractFileIndexer luceneIndexer = new ExtractFileIndexer();
        logger.info("Creating Temporary directory");
        tmpDir = Files.createTempDir().getAbsolutePath();
        logger.info("Directory created");
        logger.info("[LUCENE] Indexing Vaccination File");
        luceneIndexer.index(vaccinationFile.toString(), tmpDir);
        logger.info("[LUCENE] Vaccination File Indexed");
        vaccinations = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(tmpDir))));

        // Init Attributes
        this.lineNumber = 1;
        parser = new RecordParser();
        searcher = new ExtractFileSearcher();

        // Get patients file iterator
        logger.info("Creating lines iterator on patients file");
        lines = java.nio.file.Files.lines(patientFile);
        linesIterator = lines.iterator();
        logger.info("[RECORD ITERATOR] Initialized");
    }

    @Override
    public boolean hasNext() {
        return linesIterator.hasNext();
    }

    @Override
    public AggregateParsedRecord next() throws IOException, InvalidPatientRecord {
        logger.info("[RECORD ITERATOR] Reading record at line "+this.lineNumber);

        if(!this.hasNext()) {
            logger.info("[RECORD ITERATOR] No elements left");
            throw new NoSuchElementException();
        } else {
            String line = linesIterator.next();
            ParsedRecord<Patient> patient = parser.parse(Patient.class, line, lineNumber);
            if(patient.getID() != null) {
                List<ParsedRecord<VaccineRecord>> vaccinations = this.searcher.search(patient.getID(), this.vaccinations).stream()
                        .map(document -> parser.parse(VaccineRecord.class, document.getContent(), document.getLine()))
                        .collect(Collectors.toList());
                logger.info("[RECORD ITERATOR] Record at line "+this.lineNumber+" parsed");
                lineNumber++;
                return new AggregateParsedRecord(patient, vaccinations);
            } else {
                logger.info("[RECORD ITERATOR] ID not found for record at line "+ lineNumber);
                lineNumber++;
                throw new InvalidPatientRecord(
                        patient
                                .getIssues()
                                .stream()
                                .map(issue ->
                                        new ParseError(patient.getID(), issue.getField(), issue.getRecord(), issue.getMessage(), issue.isCritical(), patient.getLine()))
                                .collect(Collectors.toList()));
            }
        }
    }

    @Override
    public int progress() {
        return lineNumber - 1;
    }


    @Override
    public void close() throws IOException {
        this.lines.close();
        if(java.nio.file.Files.exists(Paths.get(this.tmpDir))) {
            java.nio.file.Files.walk(Paths.get(this.tmpDir))
                    .filter(path -> path.toFile().isFile())
                    .forEach(path -> {
                        try {
                            java.nio.file.Files.delete(path);
                        } catch (IOException e) {
                            logger.error("[CLOSING]",e);
                        }
                    });
            java.nio.file.Files.deleteIfExists(Paths.get(this.tmpDir));
        }
    }

}
