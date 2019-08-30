package gov.nist.healthcare.iz.darq.digest.service.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import gov.nist.healthcare.iz.darq.parser.model.Address;
import gov.nist.healthcare.iz.darq.parser.annotation.Code;
import gov.nist.healthcare.iz.darq.parser.service.model.DataElement;
import gov.nist.healthcare.iz.darq.parser.annotation.DummyValue;
import gov.nist.healthcare.iz.darq.parser.annotation.FieldName;
import gov.nist.healthcare.iz.darq.parser.model.Name;
import gov.nist.healthcare.iz.darq.parser.model.Patient;
import gov.nist.healthcare.iz.darq.parser.model.ResponsibleParty;
import gov.nist.healthcare.iz.darq.parser.model.VISInformation;
import gov.nist.healthcare.iz.darq.parser.model.VaccineRecord;
import gov.nist.healthcare.iz.darq.parser.type.DataUnit;
import gov.nist.healthcare.iz.darq.parser.type.DqDate;
import gov.nist.healthcare.iz.darq.parser.type.DqNumeric;
import gov.nist.healthcare.iz.darq.parser.type.DqString;

@Component
public class LineItemizer {
	
	public static final List<Class<?>> SUPPORT = Arrays.asList(Patient.class, VaccineRecord.class, Name.class, Address.class, ResponsibleParty.class, VISInformation.class);

	
	public List<DataElement> itemizePatient(Patient patient) throws IllegalArgumentException, IllegalAccessException{
		return process(Patient.class, patient, "", "Patient");
	}

	
	public List<DataElement> itemizeVax(VaccineRecord vax) throws IllegalArgumentException, IllegalAccessException{
		return process(VaccineRecord.class, vax, "", "Vaccination");
	}
	
	public List<DataElement> process(Class<?> clazz, Object obj, String path, String name) throws IllegalArgumentException, IllegalAccessException{
		List<DataElement> _lc = new ArrayList<>();
		
		for(Field f : clazz.getFields()){
			if(f.isAnnotationPresent(gov.nist.healthcare.iz.darq.parser.annotation.Field.class) || SUPPORT.contains(f.getType()) || isDataUnit(f)){

				gov.nist.healthcare.iz.darq.parser.annotation.Field metadata = f.getAnnotation(gov.nist.healthcare.iz.darq.parser.annotation.Field.class);
				String _name_ = metadata.name();
				String table = metadata.table();
				String dummy = f.isAnnotationPresent(DummyValue.class) ? f.getAnnotation(DummyValue.class).value() : "";
				String _path = path.isEmpty() ? f.getName() : path+"/"+f.getName();
				String _name = name.isEmpty() ? _name_ : name + " - " + _name_;

				if(!isDataUnit(f)){
					_lc.addAll(process(f.getType(), f.getType().cast(f.get(obj)), _path, _name));
				}
				else {
					_lc.add(new DataElement(_path, _name, !table.isEmpty(), table, dummy, (DataUnit<?>) f.get(obj)));
				}	
			}
		}
		
		return _lc;
	}
	
	public List<String> codeSets(Class<?> clazz) throws IllegalArgumentException, IllegalAccessException{
		List<String> _lc = new ArrayList<>();
		
		for(Field f : clazz.getFields()){
			if(f.isAnnotationPresent(gov.nist.healthcare.iz.darq.parser.annotation.Field.class) || SUPPORT.contains(f.getType()) || isDataUnit(f)){

				gov.nist.healthcare.iz.darq.parser.annotation.Field metadata = f.getAnnotation(gov.nist.healthcare.iz.darq.parser.annotation.Field.class);
				String table = metadata.table();

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
