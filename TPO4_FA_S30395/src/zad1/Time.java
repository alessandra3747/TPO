/**
 *
 *  @author Fus Aleksandra S30395
 *
 */

package zad1;


import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Time {

    public static String passed(String from, String to){

        try {
            StringBuilder builder = new StringBuilder();

            boolean isTimeIncluded = from.contains("T") && to.contains("T");

            LocalDateTime fromDateTime = parseDateTime(from);
            LocalDateTime toDateTime = parseDateTime(to);

            String fromDateWeekday = fromDateTime.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("pl"));
            String toDateWeekday = toDateTime.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("pl"));

            long daysBetweenDates = ChronoUnit.DAYS.between(fromDateTime.toLocalDate(), toDateTime.toLocalDate());
            double weeksBetweenDates = daysBetweenDates / 7.0;

            DecimalFormat decimalFormatter = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.ENGLISH));
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("pl"));

            String fromFormatted = isTimeIncluded
                    ? String.format("%s (%s)", fromDateTime.format(dateFormatter), fromDateWeekday) + " godz. " + fromDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                    : String.format("%s (%s)", fromDateTime.format(dateFormatter), fromDateWeekday);

            String toFormatted = isTimeIncluded
                    ? String.format("%s (%s)", toDateTime.format(dateFormatter), toDateWeekday) + " godz. " + toDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                    : String.format("%s (%s)", toDateTime.format(dateFormatter), toDateWeekday);


            builder.append(String.format("Od %s do %s\n - mija: %d %s, tygodni %s",
                    fromFormatted,
                    toFormatted,
                    daysBetweenDates,
                    conjugateDay((int) daysBetweenDates),
                    decimalFormatter.format(weeksBetweenDates)
            ));


            ZonedDateTime fromZonedDateTime = fromDateTime.atZone(ZoneId.of("Europe/Warsaw"));
            ZonedDateTime toZonedDateTime = toDateTime.atZone(ZoneId.of("Europe/Warsaw"));


            if (isTimeIncluded) {

                long hoursBetweenDates = ChronoUnit.HOURS.between(fromZonedDateTime, toZonedDateTime);
                long minutesBetweenDates = ChronoUnit.MINUTES.between(fromZonedDateTime, toZonedDateTime);

                builder.append(String.format("\n - godzin: %d, minut: %d",
                        hoursBetweenDates,
                        minutesBetweenDates));

            }


            if (daysBetweenDates >= 1) {

                LocalDate tempFromDateTime = fromDateTime.toLocalDate();
                LocalDate tempToDateTime = toDateTime.toLocalDate();

                long years = ChronoUnit.YEARS.between(tempFromDateTime, tempToDateTime);
                tempFromDateTime = tempFromDateTime.plusYears(years);

                long months = ChronoUnit.MONTHS.between(tempFromDateTime, tempToDateTime);
                tempFromDateTime = tempFromDateTime.plusMonths(months);

                long days = ChronoUnit.DAYS.between(tempFromDateTime, tempToDateTime);


                List<String> calendarParts = new ArrayList<>();

                if (years > 0) {
                    calendarParts.add(years + " " + conjugateYear((int) years));
                }

                if (months > 0) {
                    calendarParts.add(months + " " + conjugateMonth((int) months));
                }

                if (days > 0) {
                    calendarParts.add(days + " " + conjugateDay((int) days));
                }

                if (!calendarParts.isEmpty()) {
                    builder.append("\n - kalendarzowo: ");
                    builder.append(String.join(", ", calendarParts));
                }


            }

            return builder.toString();

        } catch (Exception e) {
            return "*** " + e.getClass().getName() + ": " + e.getMessage();
        }
    }


    private static String conjugateDay(int days) {
        if (days == 1)
            return "dzień";
        else
            return "dni";
    }

    private static String conjugateMonth(int months) {
        if (months == 1)
            return "miesiąc";
        else if (months%10 >= 2 && months%10 <= 4 && !(months%100 >= 12 && months%100 <= 14))
            return "miesiące";
        else
            return " miesięcy";
    }

    private static String conjugateYear(int years){
        if (years == 1)
            return "rok";
        else if (years%10 >= 2 && years%10 <= 4 && !(years%100 >= 12 && years%100 <= 14))
            return "lata";
        else
            return "lat";
    }


    private static LocalDateTime parseDateTime(String date) {

        if(date.contains("T")) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            return LocalDateTime.parse(date, formatter);
        }
        else {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
            return LocalDate.parse(date, formatter).atStartOfDay();
        }
    }


}
