package gov.nist.healthcare.iz.darq.access.domain;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class UserPermissionSerializer extends StdSerializer<UserPermission> {
    
    public UserPermissionSerializer() {
        super(UserPermission.class);
    }

    @Override
    public void serialize(UserPermission value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeStartObject();

        generator.writeFieldName("facilities");
        generator.writeObject(value.getFacilities());

        generator.writeFieldName("permissions");
        generator.writeObject(value.getPermissions());

        generator.writeFieldName("authorize");
        generator.writeStartObject();

        for (Map.Entry<QualifiedScope, Map<ResourceType, Map<QualifiedAccessToken, Set<Action>>>> entry : value.entrySet()) {
            generator.writeFieldName(entry.getKey().toString());
            generator.writeObject(entry.getValue());
        }

        generator.writeEndObject();
        generator.writeEndObject();
    }
}
