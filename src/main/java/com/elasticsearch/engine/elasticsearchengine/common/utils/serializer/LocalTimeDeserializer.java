package com.elasticsearch.engine.elasticsearchengine.common.utils.serializer;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Tong Li
 * @since JDK1.8
 * Created on 2021/6/30.
 */
public class LocalTimeDeserializer extends JsonDeserializer<LocalTime> {
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

  @Override
  public LocalTime deserialize(com.fasterxml.jackson.core.JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    String value = jsonParser.getValueAsString();
    return LocalTime.parse(value, formatter);
  }
}
