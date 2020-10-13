package com.fpt.gta.util;

import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.widget.EditText;

import java.math.BigDecimal;
import java.util.StringTokenizer;

public class BudgetTextWatcher implements TextWatcher {
    private EditText etAmount;
    private String oldValue = "";
    private int scale = 2;
    private int numberSize;


    public BudgetTextWatcher(EditText etAmount, int scale) {
        this.etAmount = etAmount;
        this.scale = scale;
    }

    public BudgetTextWatcher(EditText etAmount) {
        this.etAmount = etAmount;
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        int cursorPosition = etAmount.getSelectionEnd();
        String originalStr = etAmount.getText().toString();
        //To restrict only two digits after decimal place
        etAmount.setFilters(new InputFilter[]{new BudgetTextWatcher.MoneyValueFilter(scale)});

        try {
            etAmount.removeTextChangedListener(this);
            String value = etAmount.getText().toString();

            if (value != null && !value.equals("")) {
//                if (value.startsWith(".")) {
//                    etAmount.setText("0.");
//                }
//                if (value.startsWith("0") && !value.startsWith("0.")) {
//                    etAmount.setText("");
//                }
                String str = etAmount.getText().toString().replaceAll(",", "");
                if (!value.equals("")) {
                    BigDecimal bigDecimal = BigDecimal.ZERO;
                    if (new BigDecimal(str).compareTo(new BigDecimal("200000000")) < 0) {
                        etAmount.setText(getDecimalFormattedString(str));
                    } else {
                        etAmount.setText(oldValue);
                    }
                }

                int diff = etAmount.getText().toString().length() - originalStr.length();
                etAmount.setSelection(cursorPosition + diff);
            }
            oldValue = etAmount.getText().toString();
            etAmount.addTextChangedListener(this);
        } catch (Exception ex) {
            ex.printStackTrace();
            etAmount.addTextChangedListener(this);
        }
    }


    public static String getDecimalFormattedString(String value) {
        if (value != null && !value.equalsIgnoreCase("")) {
            StringTokenizer lst = new StringTokenizer(value, ".");
            String str1 = value;
            String str2 = "";
            if (lst.countTokens() > 1) {
                str1 = lst.nextToken();
                str2 = lst.nextToken();
            }
            String str3 = "";
            int i = 0;
            int j = -1 + str1.length();
            if (str1.charAt(-1 + str1.length()) == '.') {
                j--;
                str3 = ".";
            }
            for (int k = j; ; k--) {
                if (k < 0) {
                    if (str2.length() > 0)
                        str3 = str3 + "." + str2;
                    return str3;
                }
                if (i == 3) {
                    str3 = "," + str3;
                    i = 0;
                }
                str3 = str1.charAt(k) + str3;
                i++;
            }
        }
        return "";
    }


    class MoneyValueFilter extends DigitsKeyListener {
        private int digits;

        public MoneyValueFilter(int i) {
            super(false, true);
            digits = i;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            CharSequence out = super.filter(source, start, end, dest, dstart, dend);

            // if changed, replace the source
            if (out != null) {
                source = out;
                start = 0;
                end = out.length();
            }

            int len = end - start;

            // if deleting, source is empty
            // and deleting can't break anything
            if (len == 0) {
                return source;
            }

            int dlen = dest.length();

            // Find the position of the decimal .
            for (int i = 0; i < dstart; i++) {
                if (dest.charAt(i) == '.') {
                    // being here means, that a number has
                    // been inserted after the dot
                    // check if the amount of digits is right
                    return getDecimalFormattedString((dlen - (i + 1) + len > digits) ? "" : String.valueOf(new SpannableStringBuilder(source, start, end)));
                }
            }

            for (int i = start; i < end; ++i) {
                if (source.charAt(i) == '.') {
                    // being here means, dot has been inserted
                    // check if the amount of digits is right
                    if ((dlen - dend) + (end - (i + 1)) > digits)
                        return "";
                    else
                        break; // return new SpannableStringBuilder(source,
                    // start, end);
                }
            }

            // if the dot is after the inserted part,
            // nothing can break
            return getDecimalFormattedString(String.valueOf(new SpannableStringBuilder(source, start, end)));
        }
    }
}
