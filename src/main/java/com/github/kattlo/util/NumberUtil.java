package com.github.kattlo.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fabiojose
 */
@Slf4j
@UtilityClass
public class NumberUtil {

    private static final int FIRST_INDEX = 0;
    private static final int SECOND_INDEX = 1;
    private static final int ONE_KIB = 1024;
    private static final String PERCENT_SYMBOL = "%";
    private static final String GIB_SYMBOL = "gib";
    private static final String MIB_SYMBOL = "mib";
    private static final String KIB_SYMBOL = "kib";

    private static Pattern SECONDS_PATTERN;
    private static Pattern MINUTES_PATTERN;
    private static Pattern HOURS_PATTERN;
    private static Pattern DAYS_PATTERN;

    private static Pattern getSecondsPattern(){
        if(Objects.isNull(SECONDS_PATTERN)){
            SECONDS_PATTERN = Pattern.compile("([0-9]+)seconds?", Pattern.CASE_INSENSITIVE);
        }
        return SECONDS_PATTERN;
    }

    private static Pattern getMinutesPattern(){
        if(Objects.isNull(MINUTES_PATTERN)){
            MINUTES_PATTERN = Pattern.compile("([0-9]+)minutes?", Pattern.CASE_INSENSITIVE);
        }
        return MINUTES_PATTERN;
    }

    private static Pattern getHoursPattern() {
        if(Objects.isNull(HOURS_PATTERN)){
            HOURS_PATTERN = Pattern.compile("([0-9]+)hours?", Pattern.CASE_INSENSITIVE);
        }
        return HOURS_PATTERN;
    }

    private static Pattern getDaysPattern() {
        if(Objects.isNull(DAYS_PATTERN)){
            DAYS_PATTERN = Pattern.compile("([0-9]+)days?", Pattern.CASE_INSENSITIVE);
        }
        return DAYS_PATTERN;
    }

    private static final List<String> HUMAN_READABLE_SYMBOLS = List.of(
        "gib",
        "mib",
        "kib",
        "%",
        "second",
        "minute",
        "hour",
        "day"
    );

    public static boolean isNumber(Object value) {

        return (value instanceof Integer)
            || (value instanceof Short)
            || (value instanceof Long)
            || (value instanceof Float)
            || (value instanceof Double);
    }

    public static String formatted(Object operand) {

        var type = Objects.requireNonNull(operand,
            "provide a non-null operand argument").getClass();

        return (
            type.equals(Double.class) || type.equals(Float.class)
            ? new DecimalFormat("##0.0#########", new DecimalFormatSymbols(Locale.US)).format(operand)
            : operand.toString()
        );

    }

    private static Object toMillis(String amount, TemporalUnit unit){
        return Duration.of(Long.parseLong(amount), unit).toMillis();
    }

    private static Optional<Object> checkAndParse(Pattern pattern,
            String value, TemporalUnit unit,
            BiFunction<String, TemporalUnit, Object> parser){

        var matcher = pattern.matcher(value);

        if(matcher.matches()){
            return Optional.of(parser.apply(matcher.group(SECOND_INDEX), unit));
        }

        return Optional.empty();
    }

    private static Object parseTime(String value){

        var result = checkAndParse(getSecondsPattern(), value, ChronoUnit.SECONDS, NumberUtil::toMillis);

        if(result.isEmpty()){
            result = checkAndParse(getMinutesPattern(), value, ChronoUnit.MINUTES, NumberUtil::toMillis);
        }

        if(result.isEmpty()){
            result = checkAndParse(getHoursPattern(), value, ChronoUnit.HOURS, NumberUtil::toMillis);
        }

        if(result.isEmpty()){
            result = checkAndParse(getDaysPattern(), value, ChronoUnit.DAYS, NumberUtil::toMillis);
        }

        if(result.isEmpty()){
            throw new IllegalArgumentException(value);
        }

        return result.get();
    }

    public static boolean isHumanReadableCandidate(String value) {
        var toCompare = Objects.requireNonNull(value, "provide a not null value argument")
            .trim().toLowerCase();

        for(String symbol : HUMAN_READABLE_SYMBOLS){
            if(toCompare.contains(symbol)){
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
    }

    /**
     * Size to bytes: GiB, MiB, KiB
     * Time to millis: second(s), minute(s), hour(s), day(s)
     * Math to floating point: %
     * @param value
     * @return
     */
    public static Object fromHumanReadable(String value) {
        StringUtil.requireNonBlank(value);
        log.debug("Try to parse the human readable: {}", value);

        var human = value.toLowerCase().trim();

        if(human.endsWith(PERCENT_SYMBOL)){
            human = human.substring(FIRST_INDEX,
                human.indexOf(PERCENT_SYMBOL)).trim();
            log.debug("Parsing as percentage: {}", human);

            return Double.parseDouble(human) / 100;

        } else if(human.endsWith("ib")) {

            var multiplier = 1;
            var endIndex = 0;
            if(human.endsWith(GIB_SYMBOL)) {
                multiplier = ONE_KIB * ONE_KIB * ONE_KIB;
                endIndex = human.indexOf(GIB_SYMBOL);

            } else if(human.endsWith(MIB_SYMBOL)){
                multiplier = ONE_KIB * ONE_KIB;
                endIndex = human.indexOf(MIB_SYMBOL);

            } else if(human.endsWith(KIB_SYMBOL)){
                multiplier = ONE_KIB;
                endIndex = human.indexOf(KIB_SYMBOL);

            } else {
                throw new IllegalArgumentException(value);
            }

            human = human.substring(FIRST_INDEX, endIndex).trim();
            log.debug("Parsing as bytes: {}", human);

            return Long.parseLong(human) * multiplier;

        } else {
            return parseTime(value);
        }

    }
}
