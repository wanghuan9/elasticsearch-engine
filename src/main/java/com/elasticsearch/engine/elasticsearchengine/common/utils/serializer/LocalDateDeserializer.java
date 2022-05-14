package com.elasticsearch.engine.elasticsearchengine.common.utils.serializer;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author Tong Li
 * @since JDK1.8
 * Created on 2021/6/30.
 */
public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  @Override
  public LocalDate deserialize(com.fasterxml.jackson.core.JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    String value = jsonParser.getValueAsString();
    return LocalDate.parse(value, FORMATTER);
  }
}
