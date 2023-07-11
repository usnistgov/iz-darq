package gov.nist.healthcare.iz.darq.digest.app;

import gov.nist.healthcare.iz.darq.digest.domain.Fraction;

import java.util.Arrays;

public class Timer {
	private final static int TOTAL_TIME_POINTS = 200;
	private final static int RECORDS_PER_TIME_POINT = 500;
	int total;
	int processed;
	int lastTimePointRecords;
	long lastTimePointTime;
	int currentTimePointIndex = - 1;
	long[] timePoints = new long[TOTAL_TIME_POINTS];

	public Timer(int total) {
		this.total = total;
	}

	void setProcessed(Fraction progress, long time) {
		processed = progress.getCount();
		total = progress.getTotal();
		if(lastTimePointTime == 0 && total > 0) {
			lastTimePointTime = System.currentTimeMillis();
		}
		int windowSize = processed - lastTimePointRecords;
		if(windowSize >= RECORDS_PER_TIME_POINT) {
			long elapsed = time - lastTimePointTime;
			addTimePoint(elapsed / windowSize, time, processed);
		}
	}

	double getVelocityRecordsPerSecond() {
		return currentTimePointIndex >=0 ? ((double) 1 / timePoints[currentTimePointIndex % TOTAL_TIME_POINTS]) * 1000 : 0;
	}

	double getRemainingEstimate() {
		return Arrays.stream(timePoints).filter((p) -> p != 0).average().orElse(0) * (total - processed);
	}

	private void addTimePoint(long timePoint, long at, int records) {
		timePoints[(++currentTimePointIndex) % TOTAL_TIME_POINTS] = timePoint;
		lastTimePointRecords = records;
		lastTimePointTime = at;
	}
}
