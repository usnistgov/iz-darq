package gov.nist.healthcare.iz.darq.digest.service.impl;

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
import gov.nist.healthcare.iz.darq.parser.type.DqDateFormat;
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
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
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
    private String tmpDir;

    public LucenePatientRecordIterator(Path patientFile, Path vaccinationFile, Optional<String> directory, DqDateFormat dateFormat) throws IOException {
        super(patientFile, vaccinationFile);
        logger.info("[RECORD ITERATOR] Initialization");
        tmpDir = null;
        // Index Vaccination File
        ExtractFileIndexer luceneIndexer = new ExtractFileIndexer();
        logger.info("Creating Temporary directory");
        if(directory.isPresent()) {
            logger.info("Directory location provided");
            File location = new File(directory.get());
            if(!location.exists()) {
                logger.error("[LUCENE TMP DIRECTORY] provided location'" + directory.get() + "' does not exist");
                throw new FileNotFoundException("provided location'" + directory.get() + "' does not exist");
            }

            if(!location.isDirectory()) {
                logger.error("[LUCENE TMP DIRECTORY] provided location'" + directory.get() + "' is not directory");
                throw new FileNotFoundException("provided location'" + directory.get() + "' is not directory");
            }
        }

        tmpDir = this.createDirectory(directory).toString();
        logger.info("Directory created at " + tmpDir);
        logger.info("[LUCENE] Indexing Vaccination File");
        luceneIndexer.index(vaccinationFile.toString(), tmpDir);
        logger.info("[LUCENE] Vaccination File Indexed");
        vaccinations = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(tmpDir))));

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

    private Path createDirectory(Optional<String> location) {
        String name = RandomStringUtils.random(10, true, true);
        Path path = location.map(s -> Paths.get(s, name)).orElseGet(() -> Paths.get(name));
        path.toFile().mkdir();
        return path.toAbsolutePath();
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
