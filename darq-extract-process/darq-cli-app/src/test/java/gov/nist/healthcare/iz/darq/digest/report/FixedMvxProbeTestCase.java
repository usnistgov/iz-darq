package gov.nist.healthcare.iz.darq.digest.report;

import gov.nist.healthcare.iz.darq.digest.common.CLITestRunnerUtils;
import gov.nist.healthcare.iz.darq.digest.mock.FixedMvxReportMock;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class FixedMvxProbeTestCase {
	static TemporaryFolder folder = new TemporaryFolder();
	static FixedMvxReportMock mock;
	static CLITestRunnerUtils utils;

	@BeforeClass
	public static void setup() throws Exception {
		folder.create();
		mock = FixedMvxReportMock.get();
		utils = new CLITestRunnerUtils(mock, folder);
		utils.createFiles();
		try {
			utils.runCLI();
		} catch (Exception e) {
			System.out.println("MVXDUMP runCLI threw " + e.getClass().getSimpleName());
		}
	}

	@Test
	public void dump() throws Exception {
		try (Stream<Path> paths = Files.walk(folder.getRoot().toPath())) {
			paths.filter(Files::isRegularFile).forEach((p) -> {
				System.out.println("MVXDUMP file " + folder.getRoot().toPath().relativize(p));
			});
		}
		try (Stream<Path> paths = Files.walk(folder.getRoot().toPath())) {
			paths.filter(Files::isRegularFile)
					.filter((p) -> p.getFileName().toString().endsWith(".csv")
							|| p.getFileName().toString().toLowerCase().contains("issue")
							|| p.getFileName().toString().endsWith(".tsv")
							|| p.getFileName().toString().endsWith(".txt"))
					.forEach((p) -> {
						System.out.println("MVXDUMP ---- " + p.getFileName());
						try {
							Files.readAllLines(p).forEach((l) -> System.out.println("MVXDUMP   " + l));
						} catch (Exception e) {
							System.out.println("MVXDUMP   <unreadable> " + e);
						}
					});
		}
	}
}
