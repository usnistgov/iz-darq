package gov.nist.healthcare.iz.darq.service.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import gov.nist.healthcare.iz.darq.parser.model.Address;
import gov.nist.healthcare.iz.darq.parser.annotation.Code;
import gov.nist.healthcare.iz.darq.parser.model.Name;
import gov.nist.healthcare.iz.darq.parser.model.Patient;
import gov.nist.healthcare.iz.darq.parser.model.ResponsibleParty;
import gov.nist.healthcare.iz.darq.parser.model.VISInformation;
import gov.nist.healthcare.iz.darq.parser.model.VaccineRecord;
import gov.nist.healthcare.iz.darq.parser.type.DqDate;
import gov.nist.healthcare.iz.darq.parser.type.DqNumeric;
import gov.nist.healthcare.iz.darq.parser.type.DqString;
import gov.nist.healthcare.iz.darq.service.utils.CodeSetService;

@Component
public class DefaultCodeSetService implements CodeSetService {
	
	public static final List<Class<?>> SUPPORT = Arrays.asList(Patient.class, VaccineRecord.class, Name.class, Address.class, ResponsibleParty.class, VISInformation.class);

	@Override
	public List<String> patientCodes() throws IllegalArgumentException, IllegalAccessException {
		return codeSets(Patient.class);
	}

	@Override
	public List<String> vaccinationCodes() throws IllegalArgumentException, IllegalAccessException {
		return codeSets(VaccineRecord.class);
	}
	
	public List<String> codeSets(Class<?> clazz) throws IllegalArgumentException, IllegalAccessException{
		List<String> _lc = new ArrayList<>();
		
		for(Field f : clazz.getFields()){
			if(SUPPORT.contains(f.getType()) || isDataUnit(f)){
				
				String table = f.isAnnotationPresent(gov.nist.healthcare.iz.darq.parser.annotation.Field.class) ? f.getAnnotation(gov.nist.healthcare.iz.darq.parser.annotation.Field.class).table() : "";

				if(!isDataUnit(f)){
					_lc.addAll(codeSets(f.getType()));
				}
				else {
					if(!table.isEmpty())
						_lc.add(table);
				}	
			}
		}
		return _lc;
	}
	
	private static boolean isDataUnit(Field f){
		return f.getType().isAssignableFrom(DqString.class) || f.getType().isAssignableFrom(DqNumeric.class) || f.getType().isAssignableFrom(DqDate.class) || f.getType().isAssignableFrom(DqNumeric.class);
	}

}
