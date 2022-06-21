package com.elasticsearch.engine.base.common.utils.serializer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
* @author wanghuan
* @description LocalDateTimeUtils
* @mail 958721894@qq.com       
* @date 2022/6/18 09:54 
*/
public class LocalDateTimeUtils {
    public static LocalDateTime fromDate(Date date) {
        if (date == null) {
            return null;
        }

        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDateTime();
    }

    public static LocalDateTime fromTimeStamp(Long timestamp) {
        if (timestamp == null) {
            return null;
        }

        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    public static Long toTimeStamp(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }


        return toDate(localDateTime).getTime();
    }


    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }

        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        return Date.from(zdt.toInstant());
    }
}
