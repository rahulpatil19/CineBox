package patil.rahul.cineboxtma.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by rahul on 22/2/18.
 */

public class CineDateFormat {

    public static String formatDate(String parsedDate) {
        switch (parsedDate) {
            case "":
                return "N/A";
            case "null":
                return "N/A";
            case "-":
                return "N/A";
            default:
                SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat targetFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                Date date = null;
                try {
                    if (parsedDate != null) {
                        date = originalFormat.parse(parsedDate);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return targetFormat.format(date);
        }
    }

    public static String calculateAge(String parsedDate) {
        Calendar calendar = Calendar.getInstance();

        if (parsedDate.equals("")) {
            return "N/A";
        } else if (parsedDate.equals("null")) {
            return "N/A";
        } else if (parsedDate.equals("-")) {
            return "N/A";
        } else {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
            Date date = null;
            try {
                date = originalFormat.parse(parsedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int bornYear = Integer.parseInt(targetFormat.format(date));
            int currentYear = calendar.get(Calendar.YEAR);
            int age = currentYear - bornYear;
            return String.valueOf(age);
        }
    }
}
