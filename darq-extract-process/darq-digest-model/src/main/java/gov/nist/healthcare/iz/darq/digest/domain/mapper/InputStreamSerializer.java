package gov.nist.healthcare.iz.darq.digest.domain.mapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamSerializer extends JsonSerializer<InputStream> {

    @Override
    public void serialize(InputStream is,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider)
            throws IOException {
        byte[] bytes = IOUtils.toByteArray(is);
        jsonGenerator.writeBinary(bytes, 0 , bytes.length);
    }
}
