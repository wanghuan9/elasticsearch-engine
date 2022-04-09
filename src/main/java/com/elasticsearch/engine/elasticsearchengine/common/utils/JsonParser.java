package com.elasticsearch.engine.elasticsearchengine.common.utils;

import com.elasticsearch.engine.elasticsearchengine.common.utils.serializer.*;
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
 * User：David Young
 * Date：2020/02/26
 */
public abstract class JsonParser {

  public static final ObjectMapper mapper = new ObjectMapper();
  private static final ObjectMapper indentMapper = new ObjectMapper();
  private static final TypeFactory typeFactory = TypeFactory.defaultInstance();

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

    mapper.registerModule(module);
    mapper.configure(Feature.ALLOW_COMMENTS, true);
    mapper.configure(Feature.ALLOW_SINGLE_QUOTES, true);
    mapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    mapper.setDateFormat(df);

    indentMapper.registerModule(module);
    indentMapper.configure(Feature.ALLOW_COMMENTS, true);
    indentMapper.configure(Feature.ALLOW_SINGLE_QUOTES, true);
    indentMapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    indentMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    indentMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    indentMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    indentMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    indentMapper.setDateFormat(df);

  }

  private JsonParser() {
  }

  public static <T> T asObject(String source, Class<T> clazz) {
    try {
      return mapper.readValue(source, clazz);
    } catch (IOException e) {
      throw new RuntimeException("json parse error", e);
    }
  }

  public static <T> T asObject(String source, Class<T> clazz, PropertyNamingStrategy propertyNamingStrategy) {
    PropertyNamingStrategy oldPropertyNamingStrategy = mapper.getPropertyNamingStrategy();
    try {
      mapper.setPropertyNamingStrategy(propertyNamingStrategy);
      return mapper.readValue(source, clazz);
    } catch (IOException e) {
      throw new RuntimeException("json parse error", e);
    } finally {
      mapper.setPropertyNamingStrategy(oldPropertyNamingStrategy);
    }
  }

  public static <T> T asObjectSnakeCase(String source, Class<T> clazz) {
    try {
      mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
      return mapper.readValue(source, clazz);
    } catch (IOException e) {
      throw new RuntimeException("json parse error", e);
    }
  }

  public static <T> T asObject(byte[] source, Class<T> clazz) {
    try {
      return mapper.readValue(source, clazz);
    } catch (IOException e) {
      throw new RuntimeException("json parse error", e);
    }
  }

  public static <T> T asObject(String source, JavaType type) {
    try {
      return mapper.readValue(source, type);
    } catch (IOException e) {
      throw new RuntimeException("json parse error", e);
    }
  }

  public static <T> T asObject(String source, TypeReference<T> type) {
    try {
      return mapper.readValue(source, type);
    } catch (IOException e) {
      throw new RuntimeException("json parse error", e);
    }
  }


  public static <T> T asObject(InputStream source, TypeReference<T> type) {
    try {
      return mapper.readValue(source, type);
    } catch (IOException e) {
      throw new RuntimeException("json parse error", e);
    }
  }

  public static <T> T asObject(InputStream source, Class<T> clazz) {
    try {
      return mapper.readValue(source, clazz);
    } catch (IOException e) {
      throw new RuntimeException("json parse error", e);
    }
  }

  public static JsonNode asJsonNode(String source) {
    try {
      return mapper.readTree(source);
    } catch (IOException e) {
      throw new RuntimeException("json parse error", e);
    }
  }

  public static JsonNode asJsonNode(Object obj, String... ignoreProperties) {
    return asJsonNode(asJson(obj, ignoreProperties));
  }

  public static Map<String, Object> object2Map(Object object, String... ignoreProperties) {
    if (object == null) return null;
    try {
      String jsonStr = asJson(object, ignoreProperties);
      return mapper.readValue(jsonStr, Map.class);
    } catch (Exception e) {
      throw new RuntimeException("json parse error", e);
    }
  }

  public static List asList(String json) {
    return mapper.convertValue(asJsonNode(json), List.class);
  }

  public static <T> List<T> asList(String json, Class<T> tClass) {
    try {
      JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, tClass);
      return mapper.readValue(json, type);
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
    return mapper.convertValue(asJsonNode(json), Map.class);
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
    return mapper.convertValue(jsonNode, Map.class);
  }

  public static <T> String asJson(T obj, String... ignoreProperties) {
    try {
      return filterMapper(ignoreProperties).writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      throw new RuntimeException("json parse error", e);
    }
  }

  public static <T> String asJsonFormat(T obj) {
    try {
      return indentMapper.writeValueAsString(obj);
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
      return mapper.setFilterProvider(null);
    }
    return mapper.setFilterProvider(ignorePropertiesFilter(ignoreProperties));
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
      return indentMapper.setFilterProvider(null);
    }
    return indentMapper.setFilterProvider(ignorePropertiesFilter(ignoreProperties));
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
