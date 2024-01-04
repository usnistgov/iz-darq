package gov.nist.extract.lucene.index;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.ScoreMode;
import org.apache.lucene.search.SimpleCollector;

import java.util.ArrayList;
import java.util.List;

public class DocumentCollector extends SimpleCollector {
	private final List<Integer> docIds = new ArrayList<>();
	private LeafReaderContext currentLeafReaderContext;

	@Override
	protected void doSetNextReader(LeafReaderContext context) {
		this.currentLeafReaderContext = context;
	}

	@Override
	public void collect(int localDocId) {
		docIds.add(currentLeafReaderContext.docBase + localDocId);
	}

	public List<Integer> getDocIds() {
		return this.docIds;
	}

	@Override
	public ScoreMode scoreMode() {
		return ScoreMode.COMPLETE_NO_SCORES;
	}
}