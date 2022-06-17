package com.elasticsearch.engine.common.utils;

import com.elasticsearch.engine.common.utils.serializer.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static com.fasterxml.jackson.core.JsonParser.Feature;

/**
* @author wanghuan
* @description JsonParser
* @mail 958721894@qq.com       
* @date 2022/6/17 15:30 
*/
public abstract class JsonParser {

  public static final ObjectMapper MAPPER = new ObjectMapper();
  private static final ObjectMapper INDENT_MAPPER = new ObjectMapper();
  private static final ObjectMapper SNAKE_CASE_MAPPER = new ObjectMapper();
  private static final TypeFactory TYPE_FACTORY = TypeFactory.defaultInstance();

  static {
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    df.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

    SimpleModule module = new SimpleModule();
    module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
    module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
    // 2021-06-30 增加对 LocalDate LocalTime 的支持
    module.addSerializer(LocalDate.class, new LocalDateSerializer());
    module.addSerializer(LocalTime.class, new LocalTimeSerializer());
    module.addDeserializer(LocalTime.class, new LocalTimeDeserializer());
    module.addDeserializer(LocalDate.class, new LocalDateDeserializer());

    MAPPER.registerModule(module);
    MAPPER.configure(Feature.ALLOW_COMMENTS, true);
    MAPPER.configure(Feature.ALLOW_SINGLE_QUOTES, true);
    MAPPER.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    MAPPER.setDateFormat(df);

    INDENT_MAPPER.registerModule(module);
    INDENT_MAPPER.configure(Feature.ALLOW_COMMENTS, true);
    INDENT_MAPPER.configure(Feature.ALLOW_SINGLE_QUOTES, true);
    INDENT_MAPPER.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    INDENT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    INDENT_MAPPER.configure(SerializationFeature.INDENT_OUTPUT, true);
    INDENT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    INDENT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    INDENT_MAPPER.setDateFormat(df);

    //下划线转驼峰
    SNAKE_CASE_MAPPER.registerModule(module);
    SNAKE_CASE_MAPPER.configure(Feature.ALLOW_COMMENTS, true);
    SNAKE_CASE_MAPPER.configure(Feature.ALLOW_SINGLE_QUOTES, true);
    SNAKE_CASE_MAPPER.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    SNAKE_CASE_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    SNAKE_CASE_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    SNAKE_CASE_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    SNAKE_CASE_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    SNAKE_CASE_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    SNAKE_CASE_MAPPER.setDateFormat(df);

  }

  private JsonParser() {
  }

  public static <T> T asObject(String source, Class<T> clazz) {
    try {
      return MAPPER.readValue(source, clazz);
    } catch (IOException e) {
      throw new RuntimeException("json parse error", e);
    }
  }

  public static <T> T asObject(byte[] source, Class<T> clazz) {
    try {
      return MAPPER.readValue(source, clazz);
    } catch (IOException e) {
      throw new RuntimeException("json parse error", e);
    }
  }

  public static <T> T asObject(String source, JavaType type) {
    try {
      return MAPPER.readValue(source, type);
    } catch (IOException e) {
      throw new RuntimeException("json parse error", e);
    }
  }

  public static <T> T asObject(String source, TypeReference<T> type) {
    try {
      return MAPPER.readValue(source, type);
    } catch (IOException e) {
      throw new RuntimeException("json parse error", e);
    }
  }


  public static <T> T asObject(InputStream source, TypeReference<T> type) {
    try {
      return MAPPER.readValue(source, type);
    } catch (IOException e) {
      throw new RuntimeException("json parse error", e);
    }
  }

  public static <T> T asObject(InputStream source, Class<T> clazz) {
    try {
      return MAPPER.readValue(source, clazz);
    } catch (IOException e) {
      throw new RuntimeException("json parse error", e);
    }
  }

  public static <T> T asObjectSnakeCase(String source, Class<T> clazz) {
    try {
      return SNAKE_CASE_MAPPER.readValue(source, clazz);
    } catch (IOException e) {
      throw new RuntimeException("json parse error", e);
    }
  }

  public static JsonNode asJsonNode(String source) {
    try {
      return MAPPER.readTree(source);
    } catch (IOException e) {
      throw new RuntimeException("json parse error", e);
    }
  }

  public static JsonNode asJsonNode(Object obj, String... ignoreProperties) {
    return asJsonNode(asJson(obj, ignoreProperties));
  }

  public static Map<String, Object> object2Map(Object object, String... ignoreProperties) {
    if (object == null) {
      return null;
    }
    try {
      String jsonStr = asJson(object, ignoreProperties);
      return MAPPER.readValue(jsonStr, Map.class);
    } catch (Exception e) {
      throw new RuntimeException("json parse error", e);
    }
  }

  public static List asList(String json) {
    return MAPPER.convertValue(asJsonNode(json), List.class);
  }

  public static <T> List<T> asList(String json, Class<T> tClass) {
    try {
      JavaType type = MAPPER.getTypeFactory().constructCollectionType(List.class, tClass);
      return MAPPER.readValue(json, type);
    } catch (Exception e) {
      throw new RuntimeException("json parse error", e);
    }
  }

  public static <T> T map2Obj(Map map, Class<T> clazz, String... ignoreProperties) {
    return filterMapper(ignoreProperties).convertValue(map, clazz);
  }

  public static Map<String, Object> asMap(Object object, String... ignoreProperties) {
    return filterMapper(ignoreProperties).convertValue(object, Map.class);
  }

  public static Map<String, Object> asMap(String json) {
    return MAPPER.convertValue(asJsonNode(json), Map.class);
  }

  public static Map<String, String> asMapStr(Object object, String... ignoreProperties) {
    return mapObject2MapStr(object2Map(object, ignoreProperties));
  }

  public static Map<String, String> mapObject2MapStr(Map<String, Object> data) {
    Map<String, String> map = new HashMap<>(64);
    if (data != null) {
      data.forEach((key, value) -> map.put(key, value.toString()));
    }
    return map;
  }

  public static Map<String, Object> jsonNodeToMap(JsonNode jsonNode) {
    return MAPPER.convertValue(jsonNode, Map.class);
  }

  public static <T> String asJson(T obj, String... ignoreProperties) {
    try {
      return filterMapper(ignoreProperties).writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      throw new RuntimeException("json parse error", e);
    }
  }

  public static <T> String asJsonSnakeCase(T obj, String... ignoreProperties) {
    try {
      return filterSnakeCaseMapper(ignoreProperties).writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      throw new RuntimeException("json parse error", e);
    }
  }

  public static <T> String asJsonFormat(T obj) {
    try {
      return INDENT_MAPPER.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      throw new RuntimeException("json parse error", e);
    }
  }

  /**
   * 忽略指定字段
   * 配合@JsonFilter("ignore")
   *
   * @param ignoreProperties
   * @return
   */
  public static ObjectMapper filterMapper(String... ignoreProperties) {
    if (ignoreProperties.length == 0) {
      return MAPPER.setFilterProvider(null);
    }
    return MAPPER.setFilterProvider(ignorePropertiesFilter(ignoreProperties));
  }

  /**
   * 忽略指定字段
   * 配合@JsonFilter("ignore")
   *
   * @param ignoreProperties
   * @return
   */
  public static ObjectMapper filterIndentMapper(String... ignoreProperties) {
    if (ignoreProperties.length == 0) {
      return INDENT_MAPPER.setFilterProvider(null);
    }
    return INDENT_MAPPER.setFilterProvider(ignorePropertiesFilter(ignoreProperties));
  }

  /**
   * 忽略指定字段
   * 配合@JsonFilter("ignore")
   *
   * @param ignoreProperties
   * @return
   */
  public static ObjectMapper filterSnakeCaseMapper(String... ignoreProperties) {
    if (ignoreProperties.length == 0) {
      return SNAKE_CASE_MAPPER.setFilterProvider(null);
    }
    return SNAKE_CASE_MAPPER.setFilterProvider(ignorePropertiesFilter(ignoreProperties));
  }

  /**
   * 序列化时指定忽略某些属性
   *
   * @param properties
   * @return
   */
  private static FilterProvider ignorePropertiesFilter(String... properties) {
    SimpleBeanPropertyFilter sfilter = SimpleBeanPropertyFilter.serializeAllExcept(properties);
    FilterProvider filterProvider = new SimpleFilterProvider()
            .addFilter("ignore", sfilter);
    return filterProvider;
  }

}
