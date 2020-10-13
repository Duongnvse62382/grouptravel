package com.fpt.gta.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

public class LanguageUtil {
    public Set getAvailableLanguageFromCountryCode(String countryCode) {
        Set<String> language = new HashSet<>();
        language.add("af");
        language.add("sq");
        language.add("am");
        language.add("ar");
        language.add("hy");
        language.add("az");
        language.add("eu");
        language.add("be");
        language.add("bn");
        language.add("bs");
        language.add("bg");
        language.add("my");
        language.add("ca");
        language.add("zh");
        language.add("zh-CN");
        language.add("zh-HK");
        language.add("zh-TW");
        language.add("hr");
        language.add("cs");
        language.add("da");
        language.add("nl");
        language.add("en");
        language.add("en-AU");
        language.add("en-GB");
        language.add("et");
        language.add("fa");
        language.add("fi");
        language.add("fil");
        language.add("fr");
        language.add("fr-CA");
        language.add("gl");
        language.add("ka");
        language.add("de");
        language.add("el");
        language.add("gu");
        language.add("iw");
        language.add("hi");
        language.add("hu");
        language.add("is");
        language.add("id");
        language.add("it");
        language.add("ja");
        language.add("kn");
        language.add("kk");
        language.add("km");
        language.add("ko");
        language.add("ky");
        language.add("lo");
        language.add("lv");
        language.add("lt");
        language.add("mk");
        language.add("ms");
        language.add("ml");
        language.add("mr");
        language.add("mn");
        language.add("ne");
        language.add("no");
        language.add("pl");
        language.add("pt");
        language.add("pt-BR");
        language.add("pt-PT");
        language.add("pa");
        language.add("ro");
        language.add("ru");
        language.add("sr");
        language.add("si");
        language.add("sk");
        language.add("sl");
        language.add("es");
        language.add("es-419");
        language.add("sw");
        language.add("sv");
        language.add("ta");
        language.add("te");
        language.add("th");
        language.add("tr");
        language.add("uk");
        language.add("ur");
        language.add("uz");
        language.add("vi");
        language.add("zu");
        Set<String> getLocalLanguage = new HashSet<>();
        Locale[] all = Locale.getAvailableLocales();
        for (Locale locale : all) {
            String country = locale.getCountry();
            if (country.equalsIgnoreCase(countryCode)) {
                for (Iterator<String> it = language.iterator(); it.hasNext(); ) {
                    String f = it.next();
                    if (f.equals(locale.getLanguage()))
                        getLocalLanguage.add(locale.getLanguage());
                }
            }
        }
        return getLocalLanguage;
    }
}
