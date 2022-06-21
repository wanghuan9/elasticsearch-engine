package com.elasticsearch.engine.base.common.utils.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
* @author wanghuan
* @description LocalDateTimeSerializer
* @mail 958721894@qq.com       
* @date 2022/6/18 09:54 
*/
public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  @Override
  public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
    jsonGenerator.writeString(localDateTime.format(DATE_TIME_FORMATTER));
  }
}
