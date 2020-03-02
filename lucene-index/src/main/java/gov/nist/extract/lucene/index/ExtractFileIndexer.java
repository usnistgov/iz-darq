package gov.nist.extract.lucene.index;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ExtractFileIndexer {

    public static final String RECORD_ID = "RECORD_ID";
    public static final String LINE_NUMBER = "LINE_NUMBER";
    public static final String CONTENT = "CONTENT";


    public IndexWriter index(String extract, String directory) throws IOException {
        IndexWriter index = this.createIndex(directory);
        try(Stream<String> lines = Files.lines(Paths.get(extract))) {
            Counter i = new Counter(0);

            lines.forEach(line -> {
                try {
                    index.addDocument(this.createDocument(line, i.inc()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            index.close();
            return index;
        }
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
