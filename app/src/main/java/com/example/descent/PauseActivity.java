package com.example.descent;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class PauseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pause);

        int currentScore = getIntent().getIntExtra("score", 0);
        TextView tvPauseAltitude = findViewById(R.id.tvPauseAltitude);
        tvPauseAltitude.setText("Current Altitude: " + currentScore);

        MaterialButton btnResume = findViewById(R.id.btnResume);
        MaterialButton btnHome = findViewById(R.id.btnHome);

        btnResume.setOnClickListener(v -> finish());

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(PauseActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}
