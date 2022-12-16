package gov.nist.healthcare.iz.darq.parser.service;

import gov.nist.healthcare.iz.darq.parser.annotation.Field;
import gov.nist.healthcare.iz.darq.parser.annotation.Record;
import gov.nist.healthcare.iz.darq.parser.exception.InvalidValueException;
import gov.nist.healthcare.iz.darq.parser.model.Issue;
import gov.nist.healthcare.iz.darq.parser.service.model.ParsedRecord;
import gov.nist.healthcare.iz.darq.parser.type.*;

import static org.reflections.ReflectionUtils.getAllFields;
import static org.reflections.ReflectionUtils.withAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RecordParser {
    public static final char SEPARATOR = '\t';
    private final DqDateFormat dateFormat;

    public RecordParser(DqDateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }


    public <T extends gov.nist.healthcare.iz.darq.parser.service.model.Record> ParsedRecord<T> parse(Class<T> clazz, String line, int lineNumber) {
        ParsedRecord<T> parsed = new ParsedRecord<>();
        List<Issue> issues = new ArrayList<>();
        parsed.setIssues(issues);
        parsed.setLine(lineNumber);

        if(clazz.isAnnotationPresent(Record.class)) {

            // Read record metadata
            Record recordAnnotation = clazz.getAnnotation(Record.class);
            int size = recordAnnotation.size();
            String name = recordAnnotation.name();

            // Split and sanity check
            String[] payloads = line.split(SEPARATOR + "", -1);

            if(payloads.length == size || (payloads.length == size + 1 && payloads[size].isEmpty())) {
                // Get annotated fields
                Set<java.lang.reflect.Field> fields = getAllFields(clazz, withAnnotation(Field.class));

                try {

                    // Create an instance of the target class
                    T record = clazz.newInstance();

                    // For each field parse
                    for(java.lang.reflect.Field field: fields) {
                        this.parseField(field, record, payloads, 0, issues, name);
                    }

                    parsed.setRecord(record);

                } catch (InstantiationException | IllegalAccessException e) {
                    issues.add(new Issue("", name, "Exception while instantiating value for record : "+e.getMessage(), true));
                }
            } else {
                issues.add(new Issue("", name, "Invalid number of fields in record (expected = "+recordAnnotation.size()+", found = "+ payloads.length +") ", true));
            }

            return parsed;
        }
        else {
            throw new IllegalArgumentException("Class "+clazz.getName()+" is not a valid record class");
        }
    }

    private <T> void  parseField(java.lang.reflect.Field field, T container, String[] payloads, int startIndex, List<Issue> issues, String name) {
        Field metadata = field.getAnnotation(Field.class);
        if(isPrimitive(field)) {
            String raw = payloads[startIndex + metadata.index()];
            String value = metadata.transform().transform(raw);
            DataUnit<?> dataUnit;
            try {

                dataUnit = instantiateValue(field.getType(), value, metadata);
                if(!dataUnit.hasValue() && metadata.required()) {
                    issues.add(new Issue(metadata.name(), name, "Field is required but was not valued", true));
                }

                field.set(container, dataUnit);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException e ) {
                issues.add(new Issue(metadata.name(), name, "Exception while instantiating value for field : "+e.getMessage(), true));
            } catch (InvocationTargetException e ) {
                issues.add(new Issue(metadata.name(), name, "Exception while instantiating value for field : "+e.getTargetException().getMessage(), true));
            }
            catch (InvalidValueException e) {
                issues.add(new Issue(metadata.name(), name, "Invalid value for field "+e.getMessage(), true));
            }
        }
        else {
            Set<java.lang.reflect.Field> fields = getAllFields(field.getType(), withAnnotation(Field.class));

            Object complexField;
            try {

                complexField = field.getType().newInstance();
                for(java.lang.reflect.Field child: fields) {
                    this.parseField(child, complexField, payloads, startIndex + metadata.index(), issues, name);
                }
                field.set(container, complexField);

            } catch (IllegalAccessException | InstantiationException e ) {
                e.printStackTrace();
                issues.add(new Issue(metadata.name(), name, "Exception while instantiating value for field : "+e.getMessage(), true));
            }
        }
    }

    private DataUnit<?> instantiateValue(Class<?> clazz, String value, Field metadata) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, InvalidValueException {
        if(clazz.isAssignableFrom(DqString.class)) {
            Constructor<DqString> constructor = DqString.class.getConstructor(String.class, String.class);
            return constructor.newInstance(value, metadata.dummyStringValue());
        } else if(clazz.isAssignableFrom(DqNumeric.class)) {
            Constructor<DqNumeric> constructor = DqNumeric.class.getConstructor(String.class);
            return constructor.newInstance(value);
        } else if(clazz.isAssignableFrom(DqDate.class)) {
            Constructor<DqDate> constructor = DqDate.class.getConstructor(String.class, DqDateFormat.class);
            return constructor.newInstance(value, this.dateFormat);
        }

        throw new IllegalArgumentException("Not a primitive field");
    }

    private boolean isPrimitive(java.lang.reflect.Field f){
        return f.getType().isAssignableFrom(DqString.class) || f.getType().isAssignableFrom(DqNumeric.class) || f.getType().isAssignableFrom(DqDate.class);
    }

}
