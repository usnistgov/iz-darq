package gov.nist.healthcare.iz.darq.test.helper;

import java.util.Map;
import java.util.Random;

abstract public class LineBuilder<T extends LineBuilder> {
	protected Random random = new Random();
	protected int SIZE;
	protected int FIELD_ID;
	protected LineWriter writer;
	protected String defaultValue = "[[EX]]";
	protected String ID;

	public LineBuilder(int ID, int SIZE) {
		this.FIELD_ID = ID;
		this.SIZE = SIZE;
		this.writer = new LineWriter(SIZE);
		this.ID = getRandomString(5);
	}

	abstract protected Map<String, Integer> getTables();

	public T withID(String ID) {
		this.ID = ID;
		return (T) this;
	}
	public T withVocabulary(String table, String value) {
		if(getTables().containsKey(table)) {
			writer.put(value, getTables().get(table));
		}
		return (T) this;
	}
	public T withValue(int i, String value) {
		writer.put(value, i);
		return (T) this;
	}
	public T withDefaultValue(String value) {
		this.defaultValue = value;
		return (T) this;
	}

	protected String getRandomString(int length) {
		int leftLimit = 97; // letter 'a'
		int rightLimit = 122; // letter 'z'


		return random.ints(leftLimit, rightLimit + 1)
				.limit(length)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
	}

	abstract protected void close();

	public String getLine() {
		this.close();
		writer.put(ID, FIELD_ID);
		writer.fill(defaultValue);
		return writer.getLine();
	}
}
