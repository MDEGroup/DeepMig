package com.llsfw.core.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class DateUtil {

    private DateUtil() {
    }

    /**
     * 对日期(时间)中的日进行加减计算. <br>
     * 例子: <br>
     * 如果Date类型的d为 2005年8月20日,那么 <br>
     * calculateByDate(d,-10)的值为2005年8月10日 <br>
     * 而calculateByDate(d,+10)的值为2005年8月30日 <br>
     * 
     * @param d
     *            日期(时间).
     * @param amount
     *            加减计算的幅度.+n=加n天;-n=减n天.
     * @return 计算后的日期(时间).
     */
    public static Date calculateByDate(Date d, int amount) {
        return calculate(d, GregorianCalendar.DATE, amount);
    }

    public static Date calculateByMinute(Date d, int amount) {
        return calculate(d, GregorianCalendar.MINUTE, amount);
    }

    public static Date calculateByYear(Date d, int amount) {
        return calculate(d, GregorianCalendar.YEAR, amount);
    }

    /**
     * 对日期(时间)中由field参数指定的日期成员进行加减计算. <br>
     * 例子: <br>
     * 如果Date类型的d为 2005年8月20日,那么 <br>
     * calculate(d,GregorianCalendar.YEAR,-10)的值为1995年8月20日 <br>
     * 而calculate(d,GregorianCalendar.YEAR,+10)的值为2015年8月20日 <br>
     * 
     * @param d
     *            日期(时间).
     * @param field
     *            日期成员. <br>
     *            日期成员主要有: <br>
     *            年:GregorianCalendar.YEAR <br>
     *            月:GregorianCalendar.MONTH <br>
     *            日:GregorianCalendar.DATE <br>
     *            时:GregorianCalendar.HOUR <br>
     *            分:GregorianCalendar.MINUTE <br>
     *            秒:GregorianCalendar.SECOND <br>
     *            毫秒:GregorianCalendar.MILLISECOND <br>
     * @param amount
     *            加减计算的幅度.+n=加n个由参数field指定的日期成员值;-n=减n个由参数field代表的日期成员值.
     * @return 计算后的日期(时间).
     */
    private static Date calculate(Date d, int field, int amount) {
        if (d == null)
            return null;
        GregorianCalendar g = new GregorianCalendar();
        g.setGregorianChange(d);
        g.add(field, amount);
        return g.getTime();
    }

    /**
     * 日期(时间)转化为字符串.
     * 
     * @param formater
     *            日期或时间的格式.
     * @param aDate
     *            java.util.Date类的实例.
     * @return 日期转化后的字符串.
     */
    public static String date2String(String formater, Date aDate) {
        if (formater == null || "".equals(formater))
            return null;
        if (aDate == null)
            return null;
        return (new SimpleDateFormat(formater)).format(aDate);
    }

    /**
     * 当前日期(时间)转化为字符串.
     * 
     * @param formater
     *            日期或时间的格式.
     * @return 日期转化后的字符串.
     */
    public static String date2String(String formater) {
        return date2String(formater, new Date());
    }

    /**
     * 获取当前日期对应的星期数. <br>
     * 1=星期天,2=星期一,3=星期二,4=星期三,5=星期四,6=星期五,7=星期六
     * 
     * @return 当前日期对应的星期数
     */
    public static int dayOfWeek() {
        GregorianCalendar g = new GregorianCalendar();
        return g.get(java.util.Calendar.DAY_OF_WEEK);
    }

    /**
     * 获取所有的时区编号. <br>
     * 排序规则:按照ASCII字符的正序进行排序. <br>
     * 排序时候忽略字符大小写.
     * 
     * @return 所有的时区编号(时区编号已经按照字符[忽略大小写]排序).
     */
    public static List<String> fecthAllTimeZoneIds() {
        List<String> v = new ArrayList<>();
        String[] ids = TimeZone.getAvailableIDs();
        for (int i = 0; i < ids.length; i++) {
            v.add(ids[i]);
        }
        Collections.sort(v, String.CASE_INSENSITIVE_ORDER);
        return v;
    }

    /**
     * 测试的main方法.
     * 
     * @param argc
     * @throws ParseException
     */
    public static void main(String[] argc) throws ParseException {

        List<String> ids = fecthAllTimeZoneIds();
        String nowDateTime = date2String("yyyy-MM-dd HH:mm:ss"); // NOSONAR
        System.out.println("The time " + TimeZone.getDefault().getID() + " is " + nowDateTime);// 程序本地运行所在时区为[Asia/Shanhai] // NOSONAR
        // 显示世界每个时区当前的实际时间
        for (int i = 0; i < ids.size(); i++) {
            System.out.println("(UTC+" + getUtcTimeZoneRawOffset(ids.get(i)) + ")" + ids.get(i) + "=" // NOSONAR
                    + string2TimezoneDefault(nowDateTime, ids.get(i))); // NOSONAR
        }
        // 显示程序运行所在地的时区
        System.out.println("TimeZone.getDefault().getID()=" + TimeZone.getDefault().getID()); // NOSONAR
    }

    /**
     * 将日期时间字符串根据转换为指定时区的日期时间.
     * 
     * @param srcFormater
     *            待转化的日期时间的格式.
     * @param srcDateTime
     *            待转化的日期时间.
     * @param dstFormater
     *            目标的日期时间的格式.
     * @param dstTimeZoneId
     *            目标的时区编号.
     * 
     * @return 转化后的日期时间.
     * @throws ParseException
     */
    public static String string2Timezone(String srcFormater, String srcDateTime, String dstFormater, // NOSONAR
            String dstTimeZoneId) throws ParseException {
        if (srcFormater == null || "".equals(srcFormater))
            return null;
        if (srcDateTime == null || "".equals(srcDateTime))
            return null;
        if (dstFormater == null || "".equals(dstFormater))
            return null;
        if (dstTimeZoneId == null || "".equals(dstTimeZoneId))
            return null;
        SimpleDateFormat sdf = new SimpleDateFormat(srcFormater);
        int diffTime = getDiffTimeZoneRawOffset(dstTimeZoneId);
        Date d = sdf.parse(srcDateTime);
        long nowTime = d.getTime();
        long newNowTime = nowTime - diffTime;
        d = new Date(newNowTime);
        return date2String(dstFormater, d);
    }

    /**
     * 获取系统当前默认时区与UTC的时间差.(单位:毫秒)
     * 
     * @return 系统当前默认时区与UTC的时间差.(单位:毫秒)
     */
    public static int getDefaultTimeZoneRawOffset() { // NOSONAR
        return TimeZone.getDefault().getRawOffset();
    }

    public static String getUtcTimeZoneRawOffset(String timeZoneId) {
        return new SimpleDateFormat("HH:mm").format(getTimeZoneRawOffset(timeZoneId));
    }

    /**
     * 获取指定时区与UTC的时间差.(单位:毫秒)
     * 
     * @param timeZoneId
     *            时区Id
     * @return 指定时区与UTC的时间差.(单位:毫秒)
     */
    public static int getTimeZoneRawOffset(String timeZoneId) { // NOSONAR
        return TimeZone.getTimeZone(timeZoneId).getRawOffset();
    }

    /**
     * 获取系统当前默认时区与指定时区的时间差.(单位:毫秒)
     * 
     * @param timeZoneId
     *            时区Id
     * @return 系统当前默认时区与指定时区的时间差.(单位:毫秒)
     */
    private static int getDiffTimeZoneRawOffset(String timeZoneId) {
        return TimeZone.getDefault().getRawOffset() - TimeZone.getTimeZone(timeZoneId).getRawOffset();
    }

    /**
     * 将日期时间字符串根据转换为指定时区的日期时间.
     * 
     * @param srcDateTime
     *            待转化的日期时间.
     * @param dstTimeZoneId
     *            目标的时区编号.
     * 
     * @return 转化后的日期时间.
     * @throws ParseException
     * @see #string2Timezone(String, String, String, String)
     */
    public static String string2TimezoneDefault(String srcDateTime, String dstTimeZoneId) throws ParseException {
        return string2Timezone("yyyy-MM-dd HH:mm:ss", srcDateTime, "yyyy-MM-dd HH:mm:ss", dstTimeZoneId);
    }

}
