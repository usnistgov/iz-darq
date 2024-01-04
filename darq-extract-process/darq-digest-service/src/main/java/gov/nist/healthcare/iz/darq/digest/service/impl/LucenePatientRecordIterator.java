package gov.nist.healthcare.iz.darq.digest.service.impl;

import gov.nist.extract.lucene.index.ExtractFileIndexer;
import gov.nist.extract.lucene.index.ExtractFileSearcher;
import gov.nist.healthcare.iz.darq.digest.service.PatientRecordIterator;
import gov.nist.healthcare.iz.darq.digest.service.exception.InvalidPatientRecord;
import gov.nist.extract.lucene.model.FormatIssue;
import gov.nist.healthcare.iz.darq.parser.annotation.Record;
import gov.nist.healthcare.iz.darq.parser.model.Patient;
import gov.nist.healthcare.iz.darq.parser.model.VaccineRecord;
import gov.nist.healthcare.iz.darq.parser.service.RecordParser;
import gov.nist.healthcare.iz.darq.parser.service.model.AggregateParsedRecord;
import gov.nist.healthcare.iz.darq.parser.service.model.ParseError;
import gov.nist.healthcare.iz.darq.parser.service.model.ParsedRecord;
import gov.nist.healthcare.iz.darq.parser.type.DqDateFormat;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LucenePatientRecordIterator extends PatientRecordIterator {

    final static Logger logger = LoggerFactory.getLogger(LucenePatientRecordIterator.class.getName());
    private final IndexSearcher vaccinations;
    private final Iterator<String> linesIterator;
    private final RecordParser parser;
    private final ExtractFileSearcher searcher;
    private int lineNumber;
    private final Stream<String> lines;
    private List<FormatIssue> sanityCheckErrors;
    private final String indexDirectoryName = "QDAR_INDEX";

    public LucenePatientRecordIterator(Path patientFile, Path vaccinationFile, Path directory, DqDateFormat dateFormat) throws Exception {
        super(patientFile, vaccinationFile);
        logger.info("[RECORD ITERATOR] Initialization");
        this.sanityCheckErrors = new ArrayList<>();
        // Index Vaccination File
        ExtractFileIndexer vaccinationLuceneIndexer = this.createFileIndexer(VaccineRecord.class);
        logger.info("Created Vaccination Indexer");
        Path index = Paths.get(directory.toAbsolutePath().toString(), indexDirectoryName);
        logger.info("Creating Temporary directory at " + index);
        index.toFile().mkdirs();
        logger.info("[LUCENE] Indexing Vaccination File");
        vaccinationLuceneIndexer.index(vaccinationFile.toString(), index.toAbsolutePath().toString());
        this.sanityCheckErrors = vaccinationLuceneIndexer.getSanityCheckErrors();
        logger.info("[LUCENE] Vaccination File Indexed");
        vaccinations = new IndexSearcher(DirectoryReader.open(FSDirectory.open(index)));

        // Init Attributes
        this.lineNumber = 1;
        parser = new RecordParser(dateFormat);
        searcher = new ExtractFileSearcher();

        // Get patients file iterator
        logger.info("Creating lines iterator on patients file");
        lines = java.nio.file.Files.lines(patientFile);
        linesIterator = lines.iterator();
        logger.info("[RECORD ITERATOR] Initialized");
    }

    private ExtractFileIndexer createFileIndexer(Class<?> clazz) throws Exception {
        logger.info("Creating Indexer");

        Record annotation = clazz.getAnnotation(Record.class);
        if(annotation == null) {
            logger.error("No record annotation in class" + clazz.getName() + " sanity check skipped skipped");
            throw new Exception("Invalid class to index " + clazz.getName());
        } else {
            logger.error("Class " + clazz.getName() + " expects " + annotation.size() + " fields separated by '"+ RecordParser.SEPARATOR +"'");
            return new ExtractFileIndexer(annotation.size(), RecordParser.SEPARATOR);
        }
    }

    public List<FormatIssue> getSanityCheckErrors() {
        return sanityCheckErrors;
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
        this.vaccinations.getIndexReader().close();
        this.lines.close();
    }

}
