package com.elasticsearch.engine.elasticsearchengine.common.utils.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author Tong Li
 * @since JDK1.8
 * Created on 2021/6/30.
 */
public class LocalDateSerializer extends JsonSerializer<LocalDate> {
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  @Override
  public void serialize(LocalDate localDate, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
    jsonGenerator.writeString(localDate.format(DATE_FORMATTER));
  }
}
