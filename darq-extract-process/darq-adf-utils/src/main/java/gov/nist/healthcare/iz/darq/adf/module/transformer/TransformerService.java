package gov.nist.healthcare.iz.darq.adf.module.transformer;

import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.iz.darq.adf.model.ADFVersion;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFReader;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFTransformer;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

public class TransformerService {
	Map<ADFVersion, Set<ADFTransformer>> transformers = new HashMap<>();

	public void register(ADFTransformer transformer) {
		transformers.compute(transformer.getFrom(), (k, v) -> {
			if(v != null) {
				v.add(transformer);
				return v;
			} else {
				return new HashSet<>(Collections.singletonList(transformer));
			}
		});
	}

	public boolean canTransform(ADFVersion from, ADFVersion to) {
		return getTransformationPath(from, to).size() > 0;
	}

	public List<ADFTransformer> getTransformationPath(ADFVersion from, ADFVersion to) {
		List<ADFTransformer> steps = new ArrayList<>();
		get(from, to, steps);
		return steps;
	}

	private void get(ADFVersion from, ADFVersion to, List<ADFTransformer> acc) {
		Set<ADFTransformer> possible = transformers.get(from);
		if(possible != null) {
			Optional<ADFTransformer> step = possible.stream().filter((p) -> p.getTo().equals(to) || canTransform(p.getTo(), to)).findFirst();
			if(step.isPresent()) {
				acc.add(step.get());
				get(step.get().getTo(), to, acc);
			}
		}
	}

	public void transform(ADFVersion from, ADFVersion to, CryptoKey origin, CryptoKey target, File source, Path destination, boolean replace) throws Exception {
		List<ADFTransformer> steps = getTransformationPath(from, to);
		if(steps.size() > 0) {
			int stepCounter = 0;
			for(ADFTransformer step: steps) {
				boolean lastStep = (steps.size() - 1) == stepCounter;
				step.transform(origin, target, source, destination, !lastStep || replace);
				stepCounter++;
			}
		} else {
			throw new Exception("No transformation path exists from " + from + " to "+ to);
		}
	}

	public void transform(ADFVersion from, ADFVersion to, CryptoKey target, ADFReader source, Path destination, boolean replace) throws Exception {
		List<ADFTransformer> steps = getTransformationPath(from, to);
		if(steps.size() > 0) {
			int stepCounter = 0;
			for(ADFTransformer step: steps) {
				boolean lastStep = (steps.size() - 1) == stepCounter;
				step.transform(target, source, destination, !lastStep || replace);
				stepCounter++;
			}
		} else {
			throw new Exception("No transformation path exists from " + from + " to "+ to);
		}
	}
}
