package gov.nist.healthcare.iz.darq.digest.domain.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputStreamDeserializer  extends JsonDeserializer<InputStream> {

    @Override
    public InputStream deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return new ByteArrayInputStream(jsonParser.getBinaryValue());
    }
}