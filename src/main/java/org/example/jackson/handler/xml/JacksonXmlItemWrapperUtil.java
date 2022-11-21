package org.example.jackson.handler.xml;

import com.fasterxml.jackson.databind.introspect.ConcreteBeanPropertyBase;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

/**
 * @author lingshr
 * @since 2021-11-15
 */
class JacksonXmlItemWrapperUtil {

    public static boolean usingItemWrapper(ConcreteBeanPropertyBase beanPropertyBase) {
        JacksonXmlItemWrapper jacksonXmlItemWrapper = beanPropertyBase.getAnnotation(JacksonXmlItemWrapper.class);
        Class<?> clazz = beanPropertyBase.getType().getRawClass();
        return jacksonXmlItemWrapper != null && JacksonXmlItemWrapperUtil.isArrayType(clazz);
    }

    public static boolean isArrayType(Class<?> clazz) {

        return clazz.isArray() || List.class.isAssignableFrom(clazz) || Set.class.isAssignableFrom(clazz);
    }

    public static String wrapName(JacksonXmlItemWrapper jacksonXmlItemWrapper) {
        String value = jacksonXmlItemWrapper.value();
        if (StringUtils.isNotBlank(value)) {
            return value;
        }

        return jacksonXmlItemWrapper.wrapName();
    }

    public static String[] unwrapName(JacksonXmlItemWrapper jacksonXmlItemWrapper) {
        String[] unwrapName = jacksonXmlItemWrapper.unwrapName();
        if (unwrapName != null && unwrapName.length > 0) {
            return unwrapName;
        }

        String wrapName = wrapName(jacksonXmlItemWrapper);
        return StringUtils.isNotBlank(wrapName) ? new String[]{wrapName} : new String[]{};
    }

}
