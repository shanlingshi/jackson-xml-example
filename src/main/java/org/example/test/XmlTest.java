package org.example.test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlAnnotationIntrospector;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import javax.xml.stream.XMLOutputFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.jackson.handler.xml.JacksonXmlItemWrapper;
import org.example.jackson.handler.xml.JacksonXmlItemWrapperDeserializerModifier;
import org.example.jackson.handler.xml.JacksonXmlItemWrapperSerializerModifier;
import org.example.util.ObjectMapperInit;

/**
 * @author lingshr
 * @since 2021-11-15
 */
public class XmlTest {

    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private static final XmlMapper xmlMapper = new XmlMapper();

    static {
        ObjectMapperInit.init(jsonMapper);

        ObjectMapperInit.init(xmlMapper);
        // 带有xml头
        xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
        xmlMapper.getFactory().getXMLOutputFactory().setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, false);
        // 去掉xml的，默认wrapper 就是 @JacksonXmlElementWrapper(useWrapping = false) 我们不需要再写了
        xmlMapper.setAnnotationIntrospector(new JacksonXmlAnnotationIntrospector(false));
        SimpleModule jacksonXmlItemWrapperModule = new SimpleModule("JacksonXmlItemWrapperModule");
        jacksonXmlItemWrapperModule.setSerializerModifier(new JacksonXmlItemWrapperSerializerModifier());
        jacksonXmlItemWrapperModule.setDeserializerModifier(new JacksonXmlItemWrapperDeserializerModifier());
        xmlMapper.registerModule(jacksonXmlItemWrapperModule);
    }


    public static void main(String[] args) throws Exception {
        System.out.println(Integer.MAX_VALUE);

        TestBeanSubItem testBeanSubItem1 = new TestBeanSubItem();
        testBeanSubItem1.setItemCode("01");
        testBeanSubItem1.setItemName("zhangsan");
        TestBeanSubItem testBeanSubItem2 = new TestBeanSubItem();
        testBeanSubItem2.setItemCode("02");
        testBeanSubItem2.setItemName("lisi");

        TestBeanItem testBeanItem1 = new TestBeanItem();
        testBeanItem1.setItemCode("1");
        testBeanItem1.setItemName("zhangsan");
        testBeanItem1.setTestSubList(Arrays.asList(testBeanSubItem1, testBeanSubItem2));
        TestBeanItem testBeanItem2 = new TestBeanItem();
        testBeanItem2.setItemCode("2");
        testBeanItem2.setItemName("lisi");
        testBeanItem2.setTestSubList(Arrays.asList(testBeanSubItem1, testBeanSubItem2));

        TestBeanItem2 testBeanItem21 = new TestBeanItem2();
        testBeanItem21.setItemName("zhangsan");
        testBeanItem21.setItemPath("http://zhangsan");
        TestBeanItem2 testBeanItem22 = new TestBeanItem2();
        testBeanItem22.setItemName("lisi");
        testBeanItem22.setItemPath("http://lisi");

        TestBean testBean = new TestBean();
        testBean.setTestList(Arrays.asList(testBeanItem1, testBeanItem2));
        testBean.setTestList2(Arrays.asList(testBeanItem21, testBeanItem22));

        String xml = xmlMapper.writeValueAsString(TestResponse.success(testBean));
        System.out.println(xml);
        System.out.println(xmlMapper.readValue(xml, new TypeReference<TestResponse<TestBean>>() {
        }));

        String json = jsonMapper.writeValueAsString(TestResponse.success(testBean));
        System.out.println(json);
        System.out.println(jsonMapper.readValue(json, new TypeReference<TestResponse<TestBean>>() {
        }));

    }


    @Getter
    @Setter
    @ToString
    @JsonRootName("response")
    public static class TestResponse<T> implements Serializable {

        public static final int DEFAULT_SUCCESS_CODE = 0;

        private int code;

        private String message;

        private T data;

        public static <T> TestResponse<T> success(T data) {
            TestResponse<T> testResponse = new TestResponse<>();
            testResponse.setCode(DEFAULT_SUCCESS_CODE);
            testResponse.setMessage("成功");
            testResponse.setData(data);
            return testResponse;
        }

    }

    @Getter
    @Setter
    @ToString
    public static class TestBean implements Serializable {

        @JsonProperty("test_list")
        @JacksonXmlItemWrapper("item")
        private List<TestBeanItem> testList;

        @JsonProperty("test_list2")
        @JacksonXmlItemWrapper("item")
        private List<TestBeanItem2> testList2;

    }

    @Getter
    @Setter
    @ToString
    public static class TestBeanItem implements Serializable {

        @JsonProperty("item_code")
        private String itemCode;

        @JsonProperty("item_name")
        private String itemName;

        @JsonProperty("test_sub_list")
        @JacksonXmlItemWrapper("item1")
        private List<TestBeanSubItem> testSubList;
    }

    @Getter
    @Setter
    @ToString
    public static class TestBeanItem2 implements Serializable {

        @JsonProperty("name")
        private String itemName;

        @JsonProperty("path")
        private String itemPath;

    }

    @Getter
    @Setter
    @ToString
    public static class TestBeanSubItem implements Serializable {

        @JsonProperty("sub_item_code")
        private String itemCode;

        @JsonProperty("sub_item_name")
        private String itemName;
    }

}
