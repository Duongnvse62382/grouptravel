package com.fpt.gta.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ChangeValue {

    public static String formatDecimalPrice(double number) {
        DecimalFormat formatter = new DecimalFormat("###,###,###,###.##");
        return formatter.format(number);
    }

    public static String priceWithoutDecimal(Double price) {
        DecimalFormat formatter = new DecimalFormat("###,###,###.##");
        return formatter.format(price);
    }

    public static int formatFloatToInt(Float number) {
        return number.intValue();
    }

    public static String changePercentFloat(float cate_percent, float total) {
        float result = Math.round((cate_percent / total) * 100);
        return result + " % ";
    }

    public static String fomatStringDate(String date1) {
        String newFormatData;

        SimpleDateFormat oldformat = new SimpleDateFormat(
                "dd/MM/yyyy");
        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date old = null;
        try {
            old = oldformat.parse(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        newFormatData = newFormat.format(old);
        return newFormatData;
    }

    public static String formatCurrency(BigDecimal currency) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
        String pattern = "##,###,###.##";
        DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
        String bigDecimalConvertedValue = decimalFormat.format(currency);
        return bigDecimalConvertedValue;
    }

    public static String formatOcrCurrency(Double currency) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
        String pattern = "##,###,###.##";
        DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
        String bigDecimalConvertedValue = decimalFormat.format(currency);
        return bigDecimalConvertedValue;
    }

    public static String formatBigCurrency(BigDecimal currency) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
        String pattern = "#,###,###,###,###.##";
        DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
        String bigDecimalConvertedValue = decimalFormat.format(currency);
        return bigDecimalConvertedValue;
    }

    public static String formatOcrMoney(BigDecimal currency) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
        String pattern = "#,###,###,###,###.#";
        DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
        String bigDecimalConvertedValue = decimalFormat.format(currency);
        return bigDecimalConvertedValue;
    }



    public static String formatRateCurrency(BigDecimal currency) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
        String pattern = "##,###,###.#########";
        DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
        String bigDecimalConvertedValue = decimalFormat.format(currency);
        return bigDecimalConvertedValue;
    }

}
