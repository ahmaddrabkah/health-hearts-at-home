package com.healthhearts.app.util;

import android.content.Context;
import android.content.SharedPreferences;

public final class Prefs {
    private static final String FILE = "hh_prefs";
    private static final String KEY_ROLE = "role";

    private Prefs() {
    }

    private static SharedPreferences sp(Context c) {
        return c.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    public static void setRole(Context c, String role) {
        sp(c).edit().putString(KEY_ROLE, role).apply();
    }

    public static String getRole(Context c) {
        return sp(c).getString(KEY_ROLE, "user");
    }

    public static boolean isAdmin(Context c) {
        return "admin".equalsIgnoreCase(getRole(c));
    }
}
