package gov.nist.healthcare.iz.darq.digest.common;
import gov.nist.healthcare.iz.darq.test.data.DataExtractMock;
import gov.nist.healthcare.iz.darq.test.data.mocks.SimpleExampleMock;
import gov.nist.healthcare.iz.darq.test.data.mocks.SimpleExamplePartOneMock;
import gov.nist.healthcare.iz.darq.test.data.mocks.SimpleExamplePartTwoMock;
import gov.nist.healthcare.iz.darq.test.helper.Constants;
import org.apache.commons.io.FileUtils;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class MockADFGenerator {
	private static Map<String, DataExtractMock> mocks = new HashMap<String, DataExtractMock>() {{
		put(Constants.SIMPLE_EXAMPLE_ADF, SimpleExampleMock.get());
		put(Constants.SIMPLE_EXAMPLE_PART_ONE_ADF, SimpleExamplePartOneMock.get());
		put(Constants.SIMPLE_EXAMPLE_PART_TWO_ADF, SimpleExamplePartTwoMock.get());
	}};

	public static void main(String[] args) throws Exception {
		String resourcesFolderLocation = args[0];
		for(Map.Entry<String, DataExtractMock> mock: mocks.entrySet()) {
			String target = Paths.get(resourcesFolderLocation, mock.getKey()).toAbsolutePath().toString();
			generateADF(mock.getValue(), new TemporaryFolder(), target);
		}
	}

	private static void generateADF(DataExtractMock mock, TemporaryFolder folder, String location) throws Exception {
		folder.create();
		CLITestRunnerUtils utils = new CLITestRunnerUtils(mock, folder);
		utils.createFiles();
		utils.runCLI();
		String ADF = Paths.get(folder.getRoot().getAbsolutePath(), "darq-analysis", "ADF.data").toAbsolutePath().toString();
		FileUtils.copyFile(new File(ADF), new File(location));
		folder.delete();
	}
}
