package com.example.descent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvHighScores;
    private MaterialButton btnPlayNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvHighScores = findViewById(R.id.tvHighScores);
        btnPlayNow = findViewById(R.id.btnPlayNow);

        btnPlayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHighScores();
    }

    private void loadHighScores() {
        List<Integer> topScores = ScoreManager.getTopScores(this);
        if (topScores.isEmpty()) {
            tvHighScores.setText("No scores yet. Play to set a record!");
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < topScores.size(); i++) {
                sb.append((i + 1)).append(". Altitude: ").append(topScores.get(i));
                if (i < topScores.size() - 1) {
                    sb.append("\n");
                }
            }
            tvHighScores.setText(sb.toString());
        }
    }
}
