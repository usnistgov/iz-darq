package gov.nist.healthcare.iz.darq.generator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class TestStream {

	public class InspectionElement {
		String type;
		String value;
		
	}
	public static void main(String[] args) throws IOException {
//		OutputStream testFile = new FileOutputStream(Paths.get("/Users/hnt5/generated_data/x.data").toFile()); 
//		PrintWriter writer = new PrintWriter(testFile);
//		Stream.iterate(0, i -> i).limit(10000000).forEach(i -> {
//			writer.println((new Random()).nextInt(100));
//		});
//		writer.close();
		
//		FileInputStream f = new FileInputStream(Paths.get("/Users/hnt5/generated_data/x.data").toFile()); 
		Stream<String> strs = Files.lines(Paths.get("/Users/hnt5/generated_data/x.data"));
		Pattern p = Pattern.compile("[,\\.\\-;]");
//		p.splitAsStream(input)
//		strs.f
		
		
		List<Integer> ints = strs.map((x) -> {
			return Integer.parseInt(x);
		}).collect(Collectors.toList());
		System.out.println(ints.size());
		strs.close();
	}
}
