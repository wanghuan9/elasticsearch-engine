package com.elasticsearch.engine.base.common.utils;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author wanghuan
 * @description DateUtils
 * @mail 958721894@qq.com
 * @date 2022/6/18 09:54
 */
public class DateUtils {

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter LOG_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final DateTimeFormatter LOCAL_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter MINI_DATE_FORMATER = DateTimeFormatter.ofPattern("yyMMdd");
    private static final DateTimeFormatter YYYY_DATE_FORMATER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter MINUTES_DATE_TIME_FORMATER = DateTimeFormatter.ofPattern("yyMMddHHmm");
    private static final DateTimeFormatter SENCOND_DATE_TIME_FORMATER = DateTimeFormatter.ofPattern("yyMMddHHmmss");
    private static final DateTimeFormatter LOCAL_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * 数字long类型
     */
    private static final Pattern NUM_PATTERN = Pattern.compile("[0-9]*");
    /**
     * yyyy-MM-dd HH:mm:ss
     */
    private static final Pattern DEFAULT_PATTERN = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}");
    /**
     * 2020-12-27T00:20:54.000Z
     * <p>
     * private static final Pattern timeLegalPattern = Pattern.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s((([0-1][0-9])|(2?[0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
     */
    private static final Pattern LOCAL_PATTERN = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3}Z");

    /**
     * 本地日期时间转换为时间戳
     *
     * @param localDateTime 日期时间
     * @return 时间戳
     */
    public static Long timeToStamp(LocalDateTime localDateTime) {
        return localDateTime == null ? null : Timestamp.valueOf(localDateTime).getTime();
    }

    /**
     * 计算当前时间距离指定日期间隔
     *
     * @param endDateTime 指定日期时间
     * @return 时间间隔
     */
    public static long intervalTime(LocalDateTime endDateTime) {
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        return Duration.between(now, endDateTime).toMillis();
    }

    /**
     * 两个时间的差值 - 毫秒差值
     *
     * @param bigDate   大时间
     * @param smallDate 小时间
     * @return
     */
    public static long interval(Date bigDate, Date smallDate) {
        return bigDate.getTime() - smallDate.getTime();
    }

    /**
     * yyyy-MM-dd HH:mm:ss字符串转换为date类型
     *
     * @param dateTime 日期时间
     * @return date
     */
    public static Date parseToDate(String dateTime) {
        return Date.from(parseToLocalDateTime(dateTime).atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalDateTime转换为Date
     *
     * @param localDateTime LocalDateTime
     * @return Date
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * yyyy-MM-dd HH:mm:ss字符串转换为LocalDateTime类型
     *
     * @param dateTime 日期时间
     * @return LocalDateTime
     */
    public static LocalDateTime parseToLocalDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, DEFAULT_FORMATTER);
    }

    /**
     * 字符串转换为LocalDateTime 自动识别类型类型
     *
     * @param dateTime 日期时间
     * @return LocalDateTime
     */
    public static LocalDateTime parseToLocalDateTimeAuto(String dateTime) {
        if (NUM_PATTERN.matcher(dateTime).matches()) {
            return dateTimeToLong(Long.parseLong(dateTime));
        } else if (DEFAULT_PATTERN.matcher(dateTime).matches()) {
            return LocalDateTime.parse(dateTime, DEFAULT_FORMATTER);
        } else if (LOCAL_PATTERN.matcher(dateTime).matches()) {
            return LocalDateTime.parse(dateTime, LOCAL_FORMATTER).plusHours(8);
        } else {
            throw new RuntimeException("LocalDateTime Format error");
        }
    }

    /**
     * yyyy-MM-dd 字符串转换为LocalDate类型
     *
     * @param dateTime 日期时间
     * @return LocalDateTime
     */
    public static LocalDate parseToLocalDate(String dateTime) {
        return LocalDate.parse(dateTime, DATE_FORMATTER);
    }

    /**
     * HH:mm:ss 字符串转换为LocalTime类型
     *
     * @param time 时间
     * @return LocalDateTime
     */
    public static LocalTime parseToLocalTime(String time) {
        return LocalTime.parse(time, LOCAL_TIME_FORMATTER);
    }

    /**
     * Date转换为LocalDateTime
     *
     * @param date 日期时间
     * @return 日期时间
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * Date默认格式化
     *
     * @param date 日期时间
     * @return 格式化后字符串
     */
    public static String formatDate(Date date) {
        return dateToLocalDateTime(date).format(DEFAULT_FORMATTER);
    }

    public static String formatDateForLog(Date date) {
        return dateToLocalDateTime(date).format(LOG_FORMATTER);
    }

    /**
     * 时刻转换为日期时间字符串
     *
     * @param timestamp 时刻
     * @return 日期时间字符串
     */
    public static String formatDate(Long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()).format(DEFAULT_FORMATTER);
    }

    /**
     * 获取当前时间增加/减少指定单位时间后的日期时间
     *
     * @param amountToAdd 增加/减少的数量，负数为减少
     * @param unit        增加/减少的时间单位
     * @return
     */
    public static LocalDateTime plus(long amountToAdd, TemporalUnit unit) {
        return LocalDateTime.now().plus(amountToAdd, unit);
    }

    /**
     * 获取当前日期时间
     *
     * @return 日期时间，如2020082012131134
     */
    public static String now() {
        return LocalDateTime.now().format(SENCOND_DATE_TIME_FORMATER);
    }

    /**
     * 获取当前日期时间
     *
     * @return 日期时间，如2020082012131134
     */
    public static String now(LocalDateTime localDateTime) {
        return localDateTime.format(SENCOND_DATE_TIME_FORMATER);
    }

    /**
     * 获取当前日期时间
     *
     * @return 日期时间，如2020-02-20 12:13:23
     */
    public static String nowDefault() {
        return LocalDateTime.now().format(DEFAULT_FORMATTER);
    }

    /**
     * 获取今日日期
     *
     * @return 今日日期，如2020-08-20
     */
    public static String getTodayDate() {
        return LocalDate.now().format(DATE_FORMATTER);
    }

    /**
     * 获取明日日期
     *
     * @return 明日日期，如2020-08-21
     */
    public static String getTomorrowDate() {
        return LocalDate.now().plusDays(1).format(DATE_FORMATTER);
    }

    /**
     * 获取后日日期
     *
     * @return 后日日期，如2020-08-22
     */
    public static String getDayAfterTomorrowDate() {
        return LocalDate.now().plusDays(2).format(DATE_FORMATTER);
    }

    /**
     * 获取月的首日
     *
     * @return 后日日期，如2020-08-01
     */
    public static LocalDateTime getFirstDayOfMonth(LocalDateTime localDateTime) {
        return localDateTime.with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * 获取月的末日
     *
     * @return 后日日期，如2020-08-30
     */
    public static LocalDateTime getLastDayOfMonth(LocalDateTime localDateTime) {
        return localDateTime.with(TemporalAdjusters.lastDayOfMonth());
    }

    public static String formatDefault(LocalDateTime localDateTime) {
        return localDateTime.format(DEFAULT_FORMATTER);
    }

    /**
     * 获取今日日期
     *
     * @return 今日日期，如20210902
     */
    public static String format(LocalDateTime localDateTime) {
        if (Objects.isNull(localDateTime)) {
            return "";
        }
        return localDateTime.format(YYYY_DATE_FORMATER);
    }

    /**
     * 获取今日日期
     *
     * @return 今日日期，如2021-09-02
     */
    public static String formatByDate(LocalDateTime localDateTime) {
        if (Objects.isNull(localDateTime)) {
            return "";
        }
        return localDateTime.format(DATE_FORMATTER);
    }


    /**
     * 获取今日日期
     *
     * @return 今日日期，如200902
     */
    public static String getMiniDate() {
        return LocalDate.now().format(MINI_DATE_FORMATER);
    }

    /**
     * 获取今日日期，精确到分钟
     *
     * @return 今日日期，如2009021113
     */
    public static String getMinuteDateTime() {
        return LocalDateTime.now().format(MINUTES_DATE_TIME_FORMATER);
    }

    /**
     * LocalDateTime 转long
     *
     * @param time
     * @return
     */
    public static Long getDateTimeLong(LocalDateTime time) {
        ZoneId zoneId = ZoneId.systemDefault();
        Instant instant = time.atZone(zoneId).toInstant();
        return instant.toEpochMilli();
    }

    /**
     * LocalDate 转 long
     *
     * @param date
     * @return
     */

    public static Long getDateLong(LocalDate date) {
        LocalDateTime dateTime = date.atStartOfDay();
        return getDateTimeLong(dateTime);
    }

    /**
     * LocalTime 转 long
     *
     * @param time
     * @return
     */
    public static Long getTimeLong(LocalTime time) {
        LocalDateTime dateTime = time.atDate(LocalDate.now());
        return getDateTimeLong(dateTime);
    }

    /**
     * long 转 LocalDateTime
     *
     * @param l
     * @return
     */
    public static LocalDateTime dateTimeToLong(long l) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(l), ZoneId.systemDefault());
    }

    /**
     * long 转 LocalDate
     *
     * @param l
     * @return
     */
    public static LocalDate dateToLong(long l) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(l), ZoneId.systemDefault()).toLocalDate();

    }

    /**
     * long 转 LocalTime
     *
     * @param l
     * @return
     */
    public static LocalTime timeToLong(long l) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(l), ZoneId.systemDefault()).toLocalTime();
    }

}
