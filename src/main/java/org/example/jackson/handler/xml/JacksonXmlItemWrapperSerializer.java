package org.example.jackson.handler.xml;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.IOException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * @author lingshr
 * @since 2021-11-15
 */
public class JacksonXmlItemWrapperSerializer extends JsonSerializer<Object> {

    private String name;

    public JacksonXmlItemWrapperSerializer() {

    }

    public JacksonXmlItemWrapperSerializer(String name) {
        this.name = name;
    }

    @Override
    public void serialize(Object values, JsonGenerator jg, SerializerProvider serializers)
            throws IOException, JsonProcessingException {

        if (StringUtils.isNotBlank(name) && XmlMapper.class.isAssignableFrom(jg.getCodec().getClass())) {

            jg.writeStartObject();
            for (Object value : (List<Object>) values) {
                jg.writeObjectField(name, value);
            }
            jg.writeEndObject();
        } else {

            jg.writeObject(values);
        }

    }

}