package com.rlearsi.apps.zipcode.whatsthezipcode;

import android.content.SharedPreferences;

import java.text.DecimalFormat;

public class UserInfo {

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private final String FLOW_REVIEW = "flow_review";

    UserInfo(SharedPreferences sharedPref) {
        this.sharedPref = sharedPref;
    }

    public boolean getWarnOfflineMode() {

        return sharedPref.getBoolean("w_offline_mode", false);

    }

    public void setHasOfflineMode() {

        editor = sharedPref.edit();
        editor.putBoolean("w_offline_mode", true);
        editor.apply();

    }

    public boolean ifEvaluate() {

        return sharedPref.getBoolean("evaluate", false);

    }

    public void setEvaluate() {

        editor = sharedPref.edit();
        editor.putBoolean("evaluate", true);
        editor.apply();

    }

    boolean getFlowReview() {

        return sharedPref.getBoolean(FLOW_REVIEW, false);

    }

    void setFlowReview() {

        editor = sharedPref.edit();
        editor.putBoolean(FLOW_REVIEW, true);
        editor.apply();

    }

}
