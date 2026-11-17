package com.example.descent;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class GameActivity extends AppCompatActivity {

    private DescentGame gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameView = findViewById(R.id.gameView);
        MaterialButton btnPause = findViewById(R.id.btnPause);

        btnPause.setOnClickListener(v -> {
            gameView.setPaused(true);
            Intent intent = new Intent(GameActivity.this, PauseActivity.class);
            intent.putExtra("score", gameView.getCurrentScore());
            startActivity(intent);
        });

        gameView.setGameEventListener(score -> {
            Intent intent = new Intent(GameActivity.this, GameOverActivity.class);
            intent.putExtra("score", score);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameView != null) {
            gameView.setPaused(false);
        }
    }
}
