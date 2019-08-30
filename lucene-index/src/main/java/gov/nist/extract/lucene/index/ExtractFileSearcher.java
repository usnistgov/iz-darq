package gov.nist.extract.lucene.index;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExtractFileSearcher {

    public List<IndexedDocument> search(String id, IndexSearcher searcher) throws IOException {
        List<IndexedDocument> documents = new ArrayList<>();

        TermQuery termQuery = new TermQuery(new Term(ExtractFileIndexer.RECORD_ID, id));
        TopDocs docs = searcher.search(termQuery, Integer.MAX_VALUE);

        for(int i = 0; i < docs.totalHits.value; i++) {
            Document hit = searcher.doc(docs.scoreDocs[i].doc);

            String ID = hit.getField(ExtractFileIndexer.RECORD_ID).stringValue();
            String content = hit.getField(ExtractFileIndexer.CONTENT).stringValue();
            int number = Integer.parseInt(hit.getField(ExtractFileIndexer.LINE_NUMBER).stringValue());

            IndexedDocument document = new IndexedDocument(ID, content, number);
            documents.add(document);
        }

        return documents;
    }

}
