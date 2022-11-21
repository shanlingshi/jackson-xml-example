package org.example.jackson.handler.xml;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.IOException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author shanlingshi
 * @since 2021-11-10
 */
public class JacksonXmlItemWrapperDeserializer extends JsonDeserializer<Object> {

    private String[] names;
    private JavaType javaType;

    private final ObjectMapper objectMapper = JacksonXmlItemWrapperObjectMapperBuilder.buildObjectMapper();

    public JacksonXmlItemWrapperDeserializer() {
    }

    public JacksonXmlItemWrapperDeserializer(String[] names, JavaType javaType) {
        this.names = names;
        this.javaType = javaType;
    }

    @Override
    public Object deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode jsonNode = jp.readValueAsTree();

        if (jsonNode.isArray() || ArrayUtils.isEmpty(names) || !isXmlDeserializer(jp.getCodec())) {
            return objectMapper.readValue(jsonNode.asText(), javaType);
        }

        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (String name : names) {
            if (StringUtils.isBlank(name)) {
                continue;
            }

            JsonNode value = jsonNode.findValue(name);
            if (value == null) {
                value = JsonNodeFactory.instance.arrayNode();
            }
            arrayNode = value.isArray() ? (ArrayNode) value : arrayNode.add(value);

            if (!arrayNode.isEmpty()) {
                break;
            }
        }
        return objectMapper.readValue(arrayNode.toString(), javaType);
    }

    private boolean isXmlDeserializer(ObjectCodec codec) {

        return XmlMapper.class.isAssignableFrom(codec.getClass());
    }

}
