/**
 *
 *  @author Fus Aleksandra S30395
 *
 */

package zad1;


import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class Time {

    public static String passed(String from, String to){

        try {
            LocalDateTime fromDateTime = parseDateTime(from);
            LocalDateTime toDateTime = parseDateTime(to);

            String fromDateWeekday = fromDateTime.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("pl"));
            String toDateWeekday = toDateTime.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("pl"));

            long daysBetweenDates = ChronoUnit.DAYS.between(fromDateTime.toLocalDate(), toDateTime.toLocalDate());
            double weeksBetweenDates = daysBetweenDates / 7.0;

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("pl"));
            DecimalFormat decimalFormatter = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.ENGLISH));


            String result = String.format("Od %s (%s) do %s (%s)\n - mija: %d dni, tygodni %s",
                    fromDateTime.format(dateTimeFormatter),
                    fromDateWeekday,
                    toDateTime.format(dateTimeFormatter),
                    toDateWeekday,
                    daysBetweenDates,
                    decimalFormatter.format(weeksBetweenDates)
                    );



            ZonedDateTime fromZonedDateTime = fromDateTime.atZone(ZoneId.of("Europe/Warsaw"));
            ZonedDateTime toZonedDateTime = toDateTime.atZone(ZoneId.of("Europe/Warsaw"));


            if (from.contains("T") && to.contains("T")) {

                long hoursBetweenDates = ChronoUnit.HOURS.between(fromZonedDateTime, toZonedDateTime);
                long minutesBetweenDates = ChronoUnit.MINUTES.between(fromZonedDateTime, toZonedDateTime);

                result += String.format("\n - godzin: %d, minut: %d",
                        hoursBetweenDates,
                        minutesBetweenDates);

            }


            if (daysBetweenDates >= 1) {

                long years = ChronoUnit.YEARS.between(fromDateTime, toDateTime);
                long months = ChronoUnit.MONTHS.between(fromDateTime, toDateTime);
                long days = ChronoUnit.DAYS.between(fromDateTime, toDateTime);

                if (years > 0 || months > 0 || days > 0)
                    result += "\n - kalendarzowo: ";


                if (years > 0) {
                    if (years == 1)
                        result += years + " rok";
                    else if (years%10 >= 2 && years%10 <= 4 && !(years%100 >= 12 && years%100 <= 14))
                        result += years + " lata";
                    else
                        result += years + " lat";

                    if (months > 0) result += ", ";
                }

                if (months > 0) {
                    if (months == 1)
                        result += months + " miesiąc";
                    else if (months%10 >= 2 && months%10 <= 4 && !(months%100 >= 12 && months%100 <= 14))
                        result += months + " miesiące";
                    else
                        result += months + " miesięcy";

                    if (days > 0) result += ", ";
                }

                if (days > 0) {
                    if (days == 1)
                        result += days + " dzień";
                    else
                        result += days + " dni";
                }



            }


            System.out.println(result);
        } catch (Exception e) {
            return "*** " + e.getClass().getName() + ": " + e.getMessage();
        }

        return "";
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
