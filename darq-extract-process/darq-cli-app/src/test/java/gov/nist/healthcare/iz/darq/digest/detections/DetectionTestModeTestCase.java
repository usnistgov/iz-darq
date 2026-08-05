package gov.nist.healthcare.iz.darq.digest.detections;

import gov.nist.healthcare.iz.darq.digest.app.CLIApp;
import gov.nist.healthcare.iz.darq.digest.app.exception.TerminalException;
import gov.nist.healthcare.iz.darq.digest.common.CLITestRunnerUtils;
import gov.nist.healthcare.iz.darq.test.data.mocks.SimpleExampleMock;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DetectionTestModeTestCase {

	static TemporaryFolder folder = new TemporaryFolder();
	static CLITestRunnerUtils utils;

	@BeforeClass
	public static void setup() throws Exception {
		folder.create();
		utils = new CLITestRunnerUtils(SimpleExampleMock.get(), folder);
		utils.createFiles();
	}

	@Test
	public void testModeWritesPatientAndVaccinationLevelResults() throws Exception {
		CLIApp.run(new String[] {
				"test",
				"-p=" + utils.getPatientsFilePath().toAbsolutePath(),
				"-v=" + utils.getVaccinationsFilePath().toAbsolutePath(),
				"-c=" + utils.getConfigurationFilePath().toAbsolutePath(),
				"-out=" + folder.getRoot().getAbsolutePath(),
				"--detection", "MQE0683",
				"--detection", "MQE0559"
		});
		CLIApp.cleanUp();

		Path details = latestDetectionTestOutput().resolve("detection-test-results.tsv");
		assertTrue(details.toString(), Files.exists(details));

		List<String> lines = Files.readAllLines(details);
		assertFalse(lines.isEmpty());
		assertEquals("source_row\tpatient_id\tvaccination_id\tdetection_id\ttarget\tresult\tobserved_value\texplanation", lines.get(0));
		assertEquals(14, lines.size());

		List<Map<String, String>> rows = lines.stream()
				.skip(1)
				.map(this::parseRow)
				.collect(Collectors.toList());

		assertResultCounts(rows, "MQE0683", 4, 2, 2, 0);
		assertResultCounts(rows, "MQE0559", 9, 3, 6, 0);

		assertTrue(rows.stream()
				.filter(row -> "MQE0683".equals(row.get("detection_id")))
				.allMatch(row -> "PATIENT".equals(row.get("target")) && row.get("vaccination_id").isEmpty()));
		assertTrue(rows.stream()
				.filter(row -> "MQE0559".equals(row.get("detection_id")))
				.allMatch(row -> "VACCINATION".equals(row.get("target")) && !row.get("vaccination_id").isEmpty()));
	}

	@Test
	public void testModeRejectsUnknownDetection() throws Exception {
		try {
			CLIApp.run(new String[] {
					"test",
					"-p=" + utils.getPatientsFilePath().toAbsolutePath(),
					"-v=" + utils.getVaccinationsFilePath().toAbsolutePath(),
					"-c=" + utils.getConfigurationFilePath().toAbsolutePath(),
					"-out=" + folder.getRoot().getAbsolutePath(),
					"--detection", "NOT_A_DETECTION"
			});
			fail("Expected unknown detection to fail");
		} catch(TerminalException e) {
			assertEquals(23, e.getExitCode());
			assertTrue(e.getPrint(), e.getPrint().contains("NOT_A_DETECTION"));
		} finally {
			CLIApp.cleanUp();
		}
	}

	@Test
	public void testModeReportsInvalidPatientBirthDateDetection() throws Exception {
		TemporaryFolder localFolder = new TemporaryFolder();
		localFolder.create();
		try {
			CLITestRunnerUtils localUtils = new CLITestRunnerUtils(SimpleExampleMock.get(), localFolder);
			localUtils.createFiles();
			List<String> patients = Files.readAllLines(localUtils.getPatientsFilePath(), StandardCharsets.UTF_8);
			String[] firstPatient = patients.get(0).split("\t", -1);
			firstPatient[8] = "2020-10-00";
			patients.set(0, String.join("\t", firstPatient));
			Files.write(localUtils.getPatientsFilePath(), patients, StandardCharsets.UTF_8);

			CLIApp.run(new String[] {
					"test",
					"-p=" + localUtils.getPatientsFilePath().toAbsolutePath(),
					"-v=" + localUtils.getVaccinationsFilePath().toAbsolutePath(),
					"-c=" + localUtils.getConfigurationFilePath().toAbsolutePath(),
					"-out=" + localFolder.getRoot().getAbsolutePath(),
					"--detection", "MQE0117"
			});
			CLIApp.cleanUp();

			Path details = latestDetectionTestOutput(localFolder).resolve("detection-test-results.tsv");
			List<Map<String, String>> rows = Files.readAllLines(details, StandardCharsets.UTF_8)
					.stream()
					.skip(1)
					.map(this::parseRow)
					.collect(Collectors.toList());

			assertResultCounts(rows, "MQE0117", 4, 1, 3, 0);
			assertTrue(rows.stream()
					.anyMatch(row ->
							"MQE0117".equals(row.get("detection_id")) &&
									"DETECTED".equals(row.get("result")) &&
									row.get("explanation").contains("Date Of Birth")));
		} finally {
			CLIApp.cleanUp();
			localFolder.delete();
		}
	}

	private void assertResultCounts(
			List<Map<String, String>> rows,
			String detection,
			int total,
			int detected,
			int notDetected,
			int notEvaluable
	) {
		List<Map<String, String>> matches = rows.stream()
				.filter(row -> detection.equals(row.get("detection_id")))
				.collect(Collectors.toList());
		assertEquals(total, matches.size());
		assertEquals(detected, matches.stream().filter(row -> "DETECTED".equals(row.get("result"))).count());
		assertEquals(notDetected, matches.stream().filter(row -> "NOT_DETECTED".equals(row.get("result"))).count());
		assertEquals(notEvaluable, matches.stream().filter(row -> "NOT_EVALUABLE".equals(row.get("result"))).count());
	}

	private Map<String, String> parseRow(String line) {
		String[] values = line.split("\t", -1);
		assertEquals(line, 8, values.length);
		return Stream.of(new String[][] {
				{"source_row", values[0]},
				{"patient_id", values[1]},
				{"vaccination_id", values[2]},
				{"detection_id", values[3]},
				{"target", values[4]},
				{"result", values[5]},
				{"observed_value", values[6]},
				{"explanation", values[7]}
		}).collect(Collectors.toMap(entry -> entry[0], entry -> entry[1]));
	}

	private Path latestDetectionTestOutput() throws IOException {
		return latestDetectionTestOutput(folder);
	}

	private Path latestDetectionTestOutput(TemporaryFolder targetFolder) throws IOException {
		try(Stream<Path> paths = Files.list(targetFolder.getRoot().toPath())) {
			return paths
					.filter(Files::isDirectory)
					.filter(path -> path.getFileName().toString().startsWith("darq-detection-test_"))
					.max(Comparator.comparing(path -> path.toFile().lastModified()))
					.orElseThrow(() -> new IOException("No detection test output found"));
		}
	}

	@AfterClass
	public static void close() {
		folder.delete();
	}
}
