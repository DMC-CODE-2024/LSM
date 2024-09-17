package com.eirs.lsm.utils;

import java.time.format.DateTimeFormatter;

public interface DateFormatterConstants {

    DateTimeFormatter fileSuffixDateFormat = DateTimeFormatter.ofPattern("yyyyMMddHH");

    String simpleStringDateFormat = "yyyy-MM-dd HH:mm:ss";

    DateTimeFormatter simpleDateFormat = DateTimeFormatter.ofPattern(simpleStringDateFormat);

}
