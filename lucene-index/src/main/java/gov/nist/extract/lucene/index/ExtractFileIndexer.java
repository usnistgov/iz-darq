package gov.nist.extract.lucene.index;
import gov.nist.extract.lucene.model.FormatIssue;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ExtractFileIndexer {

    public static final String RECORD_ID = "RECORD_ID";
    public static final String LINE_NUMBER = "LINE_NUMBER";
    public static final String CONTENT = "CONTENT";
    private final Integer size;
    private final String separator;
    private final boolean sanitize;
    private List<FormatIssue> sanityCheckErrors = new ArrayList<>();

    public ExtractFileIndexer(int size, String separator) {
        this.size = size;
        this.separator = separator;
        this.sanitize = true;
    }

    public ExtractFileIndexer() {
        this.size = null;
        this.separator = null;
        this.sanitize = false;
    }

    public IndexWriter index(String extract, String directory) throws IOException {
        IndexWriter index = this.createIndex(directory);
        try(Stream<String> lines = Files.lines(Paths.get(extract))) {
            Counter i = new Counter(0);
            if(!this.sanitize) {
                lines.forEach(line -> {
                    try {
                        index.addDocument(this.createDocument(line, i.inc()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } else {
                lines.forEach(line -> {
                    try {
                        if(line == null || line.isEmpty()) {
                            sanityCheckErrors.add(new FormatIssue(i.value() + 1, "Empty line"));
                        } else {
                            int fields = line.split(this.separator).length;
                            if(fields == this.size) {
                                index.addDocument(this.createDocument(line, i.inc()));
                            } else {
                                sanityCheckErrors.add(new FormatIssue(i.inc(), "Number of fields expected " + this.size + " found : " + fields + " separator '" + this.separator +"'"));
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            index.close();
            return index;
        }
    }

    public List<FormatIssue> getSanityCheckErrors() {
        return sanityCheckErrors;
    }

    private IndexWriter createIndex(String indexDirectoryPath) throws IOException {
        Directory indexDirectory =
                FSDirectory.open(Paths.get(indexDirectoryPath));
        IndexWriter writer = new IndexWriter(indexDirectory, new IndexWriterConfig());
        return writer;
    }

    private Document createDocument(String line, int number) {
        Document document = new Document();
        String ID = this.readId(line);

        Field record_id = new StringField(RECORD_ID, ID, Field.Store.YES);
        Field content = new StringField(CONTENT, line, Field.Store.YES);
        Field line_number = new StringField(LINE_NUMBER, number+"", Field.Store.YES);

        document.add(record_id);
        document.add(content);
        document.add(line_number);
        return document;
    }

    private String readId(String line) {
        int size = line.length();
        int i = 0;
        StringBuilder ID = new StringBuilder();
        while(size > i && line.charAt(i) != '\t') {
            ID.append(line.charAt(i));
            i++;
        }
        return ID.toString();
    }


}
