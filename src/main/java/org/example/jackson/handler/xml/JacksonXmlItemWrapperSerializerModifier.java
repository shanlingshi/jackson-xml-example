package org.example.jackson.handler.xml;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author lingshr
 * @since 2021-11-15
 */
public class JacksonXmlItemWrapperSerializerModifier extends BeanSerializerModifier {

    private static final Map<String, JacksonXmlItemWrapperSerializer> cache = new ConcurrentHashMap<>();

    private static final Function<BeanPropertyWriter, JacksonXmlItemWrapperSerializer> create = arrayWriter -> {
        JacksonXmlItemWrapper jacksonXmlItemWrapper = arrayWriter.getAnnotation(JacksonXmlItemWrapper.class);
        return new JacksonXmlItemWrapperSerializer(JacksonXmlItemWrapperUtil.wrapName(jacksonXmlItemWrapper));
    };

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc,
            List<BeanPropertyWriter> beanProperties) {

        beanProperties.stream().filter(JacksonXmlItemWrapperUtil::usingItemWrapper).forEach(writer -> {

            String key = beanDesc.getBeanClass().getName() + writer.getType().getRawClass().getName();
            JacksonXmlItemWrapperSerializer serializer = cache.computeIfAbsent(key, (k) -> create.apply(writer));

            writer.assignSerializer(serializer);
        });

        return beanProperties;
    }

}
