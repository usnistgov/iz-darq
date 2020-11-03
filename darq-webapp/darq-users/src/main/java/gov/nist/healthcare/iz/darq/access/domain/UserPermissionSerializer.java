package gov.nist.healthcare.iz.darq.access.domain;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class PermissionSerializer extends StdSerializer<Permission> {
    
    public PermissionSerializer() {
        super(Permission.class);
    }

    @Override
    public void serialize(Permission value, JsonGenerator generator, SerializerProvider provider) throws IOException, JsonGenerationException {
        generator.writeStartObject();
        generator.writeFieldName("name");
        generator.writeString(value.name());
        generator.writeFieldName("authorize");
        generator.writeObject(value.scopes);
        generator.writeEndObject();
    }
}
