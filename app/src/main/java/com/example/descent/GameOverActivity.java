package com.example.descent;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class GameOverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        int score = getIntent().getIntExtra("score", 0);
        List<Integer> topScores = ScoreManager.getTopScores(this);
        int best = topScores.isEmpty() ? score : topScores.get(0);

        TextView tvGameOverAltitude = findViewById(R.id.tvGameOverAltitude);
        TextView tvGameOverBest = findViewById(R.id.tvGameOverBest);
        tvGameOverAltitude.setText("Altitude: " + score);
        tvGameOverBest.setText("Best: " + best);

        MaterialButton btnPlayAgain = findViewById(R.id.btnPlayAgain);
        MaterialButton btnGameOverHome = findViewById(R.id.btnGameOverHome);

        btnPlayAgain.setOnClickListener(v -> {
            Intent intent = new Intent(GameOverActivity.this, GameActivity.class);
            startActivity(intent);
            finish();
        });

        btnGameOverHome.setOnClickListener(v -> {
            Intent intent = new Intent(GameOverActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}
