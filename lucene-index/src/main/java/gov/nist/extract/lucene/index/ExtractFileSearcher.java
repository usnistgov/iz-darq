package gov.nist.extract.lucene.index;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExtractFileSearcher {

    public List<IndexedDocument> search(String id, IndexSearcher searcher) throws IOException {
        List<IndexedDocument> documents = new ArrayList<>();

        TermQuery termQuery = new TermQuery(new Term(ExtractFileIndexer.RECORD_ID, id));
        DocumentCollector collector = new DocumentCollector();
        searcher.search(termQuery, collector);
        List<Integer> hits = collector.getDocIds();

        for (Integer integer : hits) {
            Document hit = searcher.doc(integer);

            String ID = hit.getField(ExtractFileIndexer.RECORD_ID).stringValue();
            String content = hit.getField(ExtractFileIndexer.CONTENT).stringValue();
            int number = Integer.parseInt(hit.getField(ExtractFileIndexer.LINE_NUMBER).stringValue());

            IndexedDocument document = new IndexedDocument(ID, content, number);
            documents.add(document);
        }

        return documents;
    }

}
