package com.healthhearts.app.ui.home;

import androidx.annotation.DrawableRes;

public class HomeSection {
    private final String title;
    private final @DrawableRes int iconRes;
    private final @DrawableRes int backgroundDrawableRes;

    public HomeSection(String title, @DrawableRes int iconRes, @DrawableRes int backgroundDrawableRes) {
        this.title = title;
        this.iconRes = iconRes;
        this.backgroundDrawableRes = backgroundDrawableRes;
    }

    public String getTitle() {
        return title;
    }

    public int getIconRes() {
        return iconRes;
    }

    public int getBackgroundDrawableRes() {
        return backgroundDrawableRes;
    }
}
