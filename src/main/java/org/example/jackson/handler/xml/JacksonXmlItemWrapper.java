package org.example.jackson.handler.xml;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

/**
 * @author lingshr
 * @since 2021-11-15
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JacksonXmlItemWrapper {

    /**
     * 序列化 包装名称；
     */
    @AliasFor(attribute = "wrapName")
    String value();

    /**
     * 序列化 包装名称；
     */
    @AliasFor(attribute = "value")
    String wrapName() default "";

    /**
     * 反列化 打开包装名称；默认不填时则使用 序列化包装名称 进行 反序列化打开包装
     * 可定义多个，依次寻找，取第一个找到且不为空的结果
     */
    String[] unwrapName() default {};

}
