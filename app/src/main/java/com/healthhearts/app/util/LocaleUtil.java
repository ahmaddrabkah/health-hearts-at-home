package com.healthhearts.app.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import java.util.Locale;

public final class LocaleUtil {

    private static final String PREFS_NAME = "hhah_prefs";
    private static final String KEY_LANGUAGE = "language";

    private LocaleUtil() {
    }

    public static void applySavedLocale(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String language = prefs.getString(KEY_LANGUAGE, "en");
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language));
    }

    public static boolean isArabic(Context context) {
        LocaleListCompat locales = AppCompatDelegate.getApplicationLocales();
        if (locales != null && !locales.isEmpty() && locales.get(0) != null) {
            return "ar".equalsIgnoreCase(locales.get(0).getLanguage());
        }
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String language = prefs.getString(KEY_LANGUAGE, "en");
        return "ar".equalsIgnoreCase(language);
    }

    public static boolean isArabic() {
        LocaleListCompat locales = AppCompatDelegate.getApplicationLocales();
        if (locales != null && !locales.isEmpty()) {
            Locale locale = locales.get(0);
            return locale != null && "ar".equalsIgnoreCase(locale.getLanguage());
        }
        return "ar".equalsIgnoreCase(Locale.getDefault().getLanguage());
    }

    public static void toggleLanguage(Context context) {
        setAppLanguage(context, isArabic(context) ? "en" : "ar");
    }

    public static void setAppLanguage(Context context, String languageTag) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_LANGUAGE, languageTag).commit();
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageTag));
    }
}
