package org.example.jackson.handler.xml;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.example.util.ObjectMapperInit;
import org.example.util.StreamUtil;

/**
 * @author shanlingshi
 * @since 2021-11-19
 */
public class JacksonXmlItemWrapperObjectMapperBuilder {

    public static ObjectMapper buildObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectMapperInit.init(objectMapper);
        SimpleModule jacksonXmlItemWrapperModule = new SimpleModule("JsonItemWrapperModule");
        jacksonXmlItemWrapperModule.setDeserializerModifier(new JsonItemWrapperDeserializerModifier());
        objectMapper.registerModule(jacksonXmlItemWrapperModule);
        return objectMapper;
    }

    private static class JsonItemWrapperDeserializerModifier extends BeanDeserializerModifier {

        private static final Map<String, JsonItemWrapperDeserializer> cache = new ConcurrentHashMap<>();

        private static final Function<SettableBeanProperty, JsonItemWrapperDeserializer> create = settable -> {
            JacksonXmlItemWrapper jacksonXmlItemWrapper = settable.getAnnotation(JacksonXmlItemWrapper.class);

            String[] unwrapNames = JacksonXmlItemWrapperUtil.unwrapName(jacksonXmlItemWrapper);
            return new JsonItemWrapperDeserializer(unwrapNames, settable.getType());
        };

        @Override
        public BeanDeserializerBuilder updateBuilder(DeserializationConfig config, BeanDescription beanDesc,
                BeanDeserializerBuilder builder) {

            StreamUtil.filter(builder.getProperties(), JacksonXmlItemWrapperUtil::usingItemWrapper).forEach(settable -> {

                String key = beanDesc.getBeanClass().getName() + settable.getType().getRawClass().getName();
                JsonItemWrapperDeserializer deserializer = cache.computeIfAbsent(key, (k) -> create.apply(settable));

                builder.addOrReplaceProperty(settable.withValueDeserializer(deserializer), true);
            });

            return super.updateBuilder(config, beanDesc, builder);
        }

    }

    private static class JsonItemWrapperDeserializer extends JsonDeserializer<Object> {

        private String[] names;
        private JavaType javaType;

        private final ObjectMapper objectMapper = JacksonXmlItemWrapperObjectMapperBuilder.buildObjectMapper();

        public JsonItemWrapperDeserializer() {
        }

        public JsonItemWrapperDeserializer(String[] names, JavaType javaType) {
            this.names = names;
            this.javaType = javaType;
        }

        @Override
        public Object deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            JsonNode jsonNode = jp.readValueAsTree();

            if (jsonNode.isArray() || ArrayUtils.isEmpty(names)) {
                return objectMapper.readValue(jsonNode.asText(), javaType);
            }

            ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
            for (String name : names) {
                if (StringUtils.isBlank(name)) {
                    continue;
                }

                JsonNode value = jsonNode.findValue(name);
                if (value == null || value.isNull()) {
                    continue;
                }

                arrayNode = value.isArray() ? (ArrayNode) value : arrayNode.add(value);

                if (!arrayNode.isEmpty()) {
                    break;
                }
            }

            return objectMapper.readValue(arrayNode.toString(), javaType);
        }

    }

}
