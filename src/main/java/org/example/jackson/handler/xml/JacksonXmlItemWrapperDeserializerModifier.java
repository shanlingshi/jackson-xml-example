package org.example.jackson.handler.xml;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.example.util.StreamUtil;

/**
 * @author shanlingshi
 * @since 2021-11-10
 */
public class JacksonXmlItemWrapperDeserializerModifier extends BeanDeserializerModifier {

    private static final Map<String, JacksonXmlItemWrapperDeserializer> cache = new ConcurrentHashMap<>();

    private static final Function<SettableBeanProperty, JacksonXmlItemWrapperDeserializer> create = settable -> {
        JacksonXmlItemWrapper jacksonXmlItemWrapper = settable.getAnnotation(JacksonXmlItemWrapper.class);

        String[] unwrapNames = JacksonXmlItemWrapperUtil.unwrapName(jacksonXmlItemWrapper);
        return new JacksonXmlItemWrapperDeserializer(unwrapNames, settable.getType());
    };

    @Override
    public BeanDeserializerBuilder updateBuilder(DeserializationConfig config, BeanDescription beanDesc,
            BeanDeserializerBuilder builder) {

        StreamUtil.filter(builder.getProperties(), JacksonXmlItemWrapperUtil::usingItemWrapper).forEach(settable -> {
            Class<?> itemClass = findItemClass(beanDesc, settable);
            String itemClassName = itemClass != null ? itemClass.getName() : settable.getName();

            String key = beanDesc.getBeanClass().getName() + settable.getType().getRawClass().getName() + itemClassName;
            JacksonXmlItemWrapperDeserializer deserializer = cache.computeIfAbsent(key, (k) -> create.apply(settable));

            builder.addOrReplaceProperty(settable.withValueDeserializer(deserializer), true);
        });

        return super.updateBuilder(config, beanDesc, builder);
    }

    private Class<?> findItemClass(BeanDescription beanDesc, SettableBeanProperty settable) {
        Field[] fields = beanDesc.getBeanClass().getDeclaredFields();
        Field field = StreamUtil.find(fields, it -> settable.getName().equals(getName(it)));

        Type genericType = field.getGenericType();
        // 如果是泛型参数的类型
        if (genericType != null && genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            //得到泛型里的class类型对象
            Class<?> genericClazz = (Class<?>) pt.getActualTypeArguments()[0];
            return genericClazz;
        }
        return null;
    }

    private String getName(Field field) {
        JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
        if (jsonProperty != null) {
            return jsonProperty.value();
        }
        JacksonXmlProperty jacksonXmlProperty = field.getAnnotation(JacksonXmlProperty.class);
        if (jacksonXmlProperty != null) {
            return jacksonXmlProperty.localName();
        }
        return field.getName();
    }

}
