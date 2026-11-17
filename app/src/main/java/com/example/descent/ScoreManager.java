package com.example.descent;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ScoreManager {
    private static final String PREF_NAME = "DescentScores";
    private static final String KEY_SCORES_CSV = "high_scores_csv";
    private static final String KEY_OLD_SCORES_SET = "high_scores";

    public static void saveScore(Context context, int score) {
        if (score <= 0) return;
        try {
            List<Integer> scores = getTopScores(context);
            scores.add(score);
            Collections.sort(scores, Collections.reverseOrder());

            // Keep top 5
            List<String> topScoresStr = new ArrayList<>();
            for (int i = 0; i < Math.min(5, scores.size()); i++) {
                topScoresStr.add(String.valueOf(scores.get(i)));
            }

            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            prefs.edit().putString(KEY_SCORES_CSV, TextUtils.join(",", topScoresStr)).apply();
        } catch (Exception ignored) {}
    }

    public static List<Integer> getTopScores(Context context) {
        List<Integer> scores = new ArrayList<>();
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String csv = prefs.getString(KEY_SCORES_CSV, null);

            if (csv != null && !csv.isEmpty()) {
                String[] parts = csv.split(",");
                for (String part : parts) {
                    try {
                        scores.add(Integer.parseInt(part.trim()));
                    } catch (NumberFormatException ignored) {}
                }
            } else {
                // Backward compatibility: check old getStringSet
                try {
                    Set<String> oldSet = prefs.getStringSet(KEY_OLD_SCORES_SET, null);
                    if (oldSet != null) {
                        for (String s : oldSet) {
                            try {
                                scores.add(Integer.parseInt(s));
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}

        Collections.sort(scores, Collections.reverseOrder());
        return scores;
    }
}
