package com.example.descent;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class DescentGame extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Random random = new Random();

    private int screenWidth;
    private int screenHeight;

    // Player
    private float playerX;
    private float playerY;
    private float targetX;
    private float targetY;

    private final float playerWidth = 65f;
    private final float playerHeight = 100f;

    // Touch tracking for relative drag
    private float lastTouchX;
    private float lastTouchY;

    // Cached drawing objects to prevent memory leaks in onDraw
    private final RectF playerRect = new RectF();
    private final RectF obstacleRect = new RectF();
    private final RectF parachuteRect = new RectF();
    private final RectF buttonRect = new RectF();
    private final Path missileNosePath = new Path();
    private final Path missileFlamePath = new Path();
    private final Path missileFinLeftPath = new Path();
    private final Path missileFinRightPath = new Path();
    private final Path birdWingPath = new Path();

    // Game state
    private boolean gameStarted = false;
    private boolean gameOver = false;
    private boolean isPaused = false;

    private int score = 0;
    private int highScore = 0;

    // Callback listener for game events (Game Over)
    public interface GameEventListener {
        void onGameOver(int finalScore);
    }

    private GameEventListener gameEventListener;

    public void setGameEventListener(GameEventListener listener) {
        this.gameEventListener = listener;
    }

    public void setPaused(boolean paused) {
        this.isPaused = paused;
        invalidate();
    }

    public int getCurrentScore() {
        return score;
    }

    // Obstacles
    private final ArrayList<Obstacle> obstacles = new ArrayList<>();
    private long lastObstacleTime = 0;
    private long obstacleSpawnDelay = 850;
    private float obstacleSpeed = 7f;

    // Background
    private float cloudScrollY = 0f;
    private final float[] cloudX = {120f, 550f, 250f, 850f, 400f, 700f};
    private final float[] cloudBaseY = {150f, 400f, 650f, 900f, 1150f, 1400f};

    public DescentGame(Context context) {
        this(context, null);
    }

    public DescentGame(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DescentGame(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        setFocusable(true);
        setClickable(true);
        setFocusableInTouchMode(true);
        requestFocus();
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        screenWidth = width;
        screenHeight = height;
        resetGame();
    }

    private void resetGame() {
        obstacles.clear();
        score = 0;
        gameStarted = false;
        gameOver = false;
        isPaused = false;
        obstacleSpeed = 7f;
        obstacleSpawnDelay = 850;

        playerX = screenWidth / 2f - playerWidth / 2f;
        playerY = screenHeight / 2f - playerHeight / 2f;
        targetX = playerX;
        targetY = playerY;

        lastObstacleTime = System.currentTimeMillis();
        cloudScrollY = 0f;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!gameStarted && !gameOver) {
                    gameStarted = true;
                    lastTouchX = x;
                    lastTouchY = y;
                    return true;
                }
                lastTouchX = x;
                lastTouchY = y;
                break;

            case MotionEvent.ACTION_MOVE:
                if (gameStarted && !gameOver && !isPaused) {
                    float dx = x - lastTouchX;
                    float dy = y - lastTouchY;
                    targetX += dx;
                    targetY += dy;
                    lastTouchX = x;
                    lastTouchY = y;
                }
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawSky(canvas);
        drawClouds(canvas);

        if (!gameStarted && !gameOver) {
            drawPlayer(canvas);
            drawStartScreen(canvas);
            invalidate();
            return;
        }

        if (!gameOver && !isPaused) {
            updateGame();
        }

        drawObstacles(canvas);
        drawPlayer(canvas);
        drawHUD(canvas);

        invalidate();
    }

    // =========================================================
    // SKY
    // =========================================================
    private void drawSky(Canvas canvas) {
        if (screenHeight <= 0) return;
        LinearGradient gradient = new LinearGradient(
                0, 0, 0, screenHeight,
                new int[] {
                        Color.rgb(80, 160, 240),   // Rich blue at top
                        Color.rgb(180, 225, 255),  // Soft light blue in middle
                        Color.rgb(255, 255, 255)   // Pure white at bottom
                },
                new float[] { 0.0f, 0.6f, 1.0f },
                Shader.TileMode.CLAMP
        );
        paint.setShader(gradient);
        canvas.drawRect(0, 0, screenWidth, screenHeight, paint);
        paint.setShader(null);
    }

    // =========================================================
    // CLOUDS
    // =========================================================
    private void drawClouds(Canvas canvas) {
        if (gameStarted && !gameOver && !isPaused) {
            cloudScrollY += obstacleSpeed * 0.6f;
        }
        paint.setColor(Color.WHITE);
        for (int i = 0; i < cloudBaseY.length; i++) {
            float totalSpan = screenHeight + 400;
            float y = (cloudBaseY[i] - (cloudScrollY % totalSpan) + totalSpan) % totalSpan - 200;
            float x = cloudX[i] % Math.max(1, screenWidth);
            drawCloud(canvas, x, y);
        }
    }

    private void drawCloud(Canvas canvas, float x, float y) {
        canvas.drawCircle(x, y, 35, paint);
        canvas.drawCircle(x + 35, y - 18, 45, paint);
        canvas.drawCircle(x + 75, y, 35, paint);
        canvas.drawRect(x - 5, y, x + 80, y + 30, paint);
    }

    // =========================================================
    // PLAYER
    // =========================================================
    private void drawPlayer(Canvas canvas) {
        float centerX = playerX + playerWidth / 2f;

        // Parachute
        paint.setColor(Color.rgb(235, 55, 70));
        parachuteRect.set(centerX - 65, playerY - 55, centerX + 65, playerY + 30);
        canvas.drawArc(parachuteRect, 180, 180, true, paint);

        // White parachute stripe
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(5);
        canvas.drawLine(centerX, playerY - 52, centerX, playerY + 10, paint);

        // Parachute strings
        paint.setColor(Color.DKGRAY);
        paint.setStrokeWidth(3);
        canvas.drawLine(centerX - 55, playerY - 15, centerX - 20, playerY + 42, paint);
        canvas.drawLine(centerX + 55, playerY - 15, centerX + 20, playerY + 42, paint);

        // Head
        paint.setColor(Color.rgb(255, 210, 175));
        canvas.drawCircle(centerX, playerY + 50, 15, paint);

        // Body
        paint.setColor(Color.rgb(35, 65, 125));
        canvas.drawRoundRect(centerX - 18, playerY + 62, centerX + 18, playerY + 95, 8, 8, paint);

        // Arms
        paint.setColor(Color.rgb(255, 210, 175));
        paint.setStrokeWidth(7);
        canvas.drawLine(centerX - 18, playerY + 68, centerX - 32, playerY + 82, paint);
        canvas.drawLine(centerX + 18, playerY + 68, centerX + 32, playerY + 82, paint);
    }

    // =========================================================
    // OBSTACLES
    // =========================================================
    private void drawObstacles(Canvas canvas) {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.type == 0) {
                drawMissile(canvas, obstacle);
            } else {
                drawBirdFlock(canvas, obstacle);
            }
        }
    }

    private void drawMissile(Canvas canvas, Obstacle obstacle) {
        float x = obstacle.x;
        float y = obstacle.y;
        float w = obstacle.width;
        float h = obstacle.height;

        // 1. Exhaust Flame at bottom
        paint.setColor(Color.rgb(255, 120, 0));
        missileFlamePath.reset();
        missileFlamePath.moveTo(x + w * 0.3f, y + h * 0.85f);
        missileFlamePath.lineTo(x + w * 0.5f, y + h + 25f);
        missileFlamePath.lineTo(x + w * 0.7f, y + h * 0.85f);
        missileFlamePath.close();
        canvas.drawPath(missileFlamePath, paint);

        paint.setColor(Color.YELLOW);
        missileFlamePath.reset();
        missileFlamePath.moveTo(x + w * 0.4f, y + h * 0.85f);
        missileFlamePath.lineTo(x + w * 0.5f, y + h + 12f);
        missileFlamePath.lineTo(x + w * 0.6f, y + h * 0.85f);
        missileFlamePath.close();
        canvas.drawPath(missileFlamePath, paint);

        // 2. Missile Body (Dark Graphite Metal)
        paint.setColor(Color.rgb(55, 60, 72));
        canvas.drawRoundRect(x + w * 0.2f, y + h * 0.25f, x + w * 0.8f, y + h * 0.85f, 6, 6, paint);

        // Highlight stripe
        paint.setColor(Color.rgb(85, 92, 108));
        canvas.drawRect(x + w * 0.25f, y + h * 0.25f, x + w * 0.4f, y + h * 0.85f, paint);

        // 3. Yellow Warning Hazard Band
        paint.setColor(Color.rgb(255, 195, 0));
        canvas.drawRect(x + w * 0.2f, y + h * 0.45f, x + w * 0.8f, y + h * 0.53f, paint);

        // 4. Crimson Red Nose Cone
        paint.setColor(Color.rgb(230, 45, 55));
        missileNosePath.reset();
        missileNosePath.moveTo(x + w * 0.5f, y);
        missileNosePath.lineTo(x + w * 0.8f, y + h * 0.28f);
        missileNosePath.lineTo(x + w * 0.2f, y + h * 0.28f);
        missileNosePath.close();
        canvas.drawPath(missileNosePath, paint);

        // 5. Dark Red Tail Fins
        paint.setColor(Color.rgb(170, 35, 45));
        // Left Fin
        missileFinLeftPath.reset();
        missileFinLeftPath.moveTo(x + w * 0.2f, y + h * 0.62f);
        missileFinLeftPath.lineTo(x, y + h * 0.85f);
        missileFinLeftPath.lineTo(x + w * 0.2f, y + h * 0.85f);
        missileFinLeftPath.close();
        canvas.drawPath(missileFinLeftPath, paint);

        // Right Fin
        missileFinRightPath.reset();
        missileFinRightPath.moveTo(x + w * 0.8f, y + h * 0.62f);
        missileFinRightPath.lineTo(x + w, y + h * 0.85f);
        missileFinRightPath.lineTo(x + w * 0.8f, y + h * 0.85f);
        missileFinRightPath.close();
        canvas.drawPath(missileFinRightPath, paint);
    }

    private void drawBirdFlock(Canvas canvas, Obstacle obstacle) {
        float x = obstacle.x;
        float y = obstacle.y;
        float w = obstacle.width;
        float h = obstacle.height;

        drawSingleBird(canvas, x + w * 0.5f, y + h * 0.15f, 22f);   // Lead bird
        drawSingleBird(canvas, x + w * 0.25f, y + h * 0.5f, 18f);   // Left bird
        drawSingleBird(canvas, x + w * 0.75f, y + h * 0.5f, 18f);   // Right bird
        drawSingleBird(canvas, x + w * 0.5f, y + h * 0.85f, 16f);   // Back bird
    }

    private void drawSingleBird(Canvas canvas, float cx, float cy, float size) {
        paint.setColor(Color.rgb(40, 45, 55));
        canvas.drawCircle(cx, cy, size * 0.35f, paint);

        paint.setColor(Color.rgb(30, 35, 45));
        paint.setStrokeWidth(size * 0.25f);
        paint.setStyle(Paint.Style.STROKE);

        birdWingPath.reset();
        birdWingPath.moveTo(cx - size * 0.9f, cy + size * 0.3f);
        birdWingPath.quadTo(cx - size * 0.4f, cy - size * 0.5f, cx, cy);
        birdWingPath.quadTo(cx + size * 0.4f, cy - size * 0.5f, cx + size * 0.9f, cy + size * 0.3f);
        canvas.drawPath(birdWingPath, paint);

        paint.setStyle(Paint.Style.FILL);

        paint.setColor(Color.rgb(255, 170, 0));
        birdWingPath.reset();
        birdWingPath.moveTo(cx - size * 0.15f, cy - size * 0.35f);
        birdWingPath.lineTo(cx, cy - size * 0.7f);
        birdWingPath.lineTo(cx + size * 0.15f, cy - size * 0.35f);
        birdWingPath.close();
        canvas.drawPath(birdWingPath, paint);
    }

    // =========================================================
    // GAME UPDATE
    // =========================================================
    private void updateGame() {
        if (targetX < 5) targetX = 5;
        if (targetX + playerWidth > screenWidth - 5) targetX = screenWidth - playerWidth - 5;
        if (targetY < 130) targetY = 130;
        if (targetY + playerHeight > screenHeight - 20) targetY = screenHeight - playerHeight - 20;

        playerX += (targetX - playerX) * 0.18f;
        playerY += (targetY - playerY) * 0.18f;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastObstacleTime >= obstacleSpawnDelay) {
            spawnObstacle();
            lastObstacleTime = currentTime;
        }

        Iterator<Obstacle> iterator = obstacles.iterator();
        while (iterator.hasNext()) {
            Obstacle obstacle = iterator.next();
            obstacle.y -= obstacleSpeed;

            if (checkCollision(obstacle)) {
                gameOver = true;
                if (score > highScore) {
                    highScore = score;
                }
                ScoreManager.saveScore(getContext(), score);
                if (gameEventListener != null) {
                    gameEventListener.onGameOver(score);
                }
                return;
            }

            if (obstacle.y + obstacle.height < 0) {
                iterator.remove();
                score++;

                if (score % 5 == 0) obstacleSpeed += 0.5f;
                if (score % 10 == 0 && obstacleSpawnDelay > 400) {
                    obstacleSpawnDelay -= 50;
                }
            }
        }
    }

    // =========================================================
    // SPAWN OBSTACLE
    // =========================================================
    private void spawnObstacle() {
        Obstacle obstacle = new Obstacle();
        obstacle.type = random.nextInt(2); // 0 = Missile, 1 = Bird Flock

        if (obstacle.type == 0) {
            obstacle.width = 45 +
                    random.nextInt(15);
            obstacle.height = 90 + random.nextInt(25);
        } else {
            obstacle.width = 110 + random.nextInt(40);
            obstacle.height = 70 + random.nextInt(20);
        }

        int maxX = Math.max(1, screenWidth - (int) obstacle.width);
        obstacle.x = random.nextInt(maxX);
        obstacle.y = screenHeight + obstacle.height;

        obstacles.add(obstacle);
    }

    // =========================================================
    // COLLISION
    // =========================================================
    private boolean checkCollision(Obstacle obstacle) {
        playerRect.set(playerX + 10, playerY + 25, playerX + playerWidth - 10, playerY + playerHeight);
        obstacleRect.set(obstacle.x, obstacle.y, obstacle.x + obstacle.width, obstacle.y + obstacle.height);
        return RectF.intersects(playerRect, obstacleRect);
    }

    // =========================================================
    // HUD
    // =========================================================
    private void drawHUD(Canvas canvas) {
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.WHITE);
        paint.setShadowLayer(5, 2, 2, Color.DKGRAY);
        paint.setTextSize(20);
        canvas.drawText("ALTITUDE", 25, 110, paint);
        paint.setTextSize(36);
        canvas.drawText(String.valueOf(score), 25, 150, paint);
        paint.setTextSize(16);
        canvas.drawText("DESCENT SPEED: " + String.format("%.1f", obstacleSpeed), 25, 180, paint);
        paint.clearShadowLayer();
    }

    // =========================================================
    // START SCREEN
    // =========================================================
    private void drawStartScreen(Canvas canvas) {
        paint.setColor(Color.argb(130, 0, 0, 0));
        canvas.drawRect(0, 0, screenWidth, screenHeight, paint);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.WHITE);
        paint.setTextSize(72);
        canvas.drawText("DESCENT", screenWidth / 2f, screenHeight / 2f - 160, paint);
        paint.setTextSize(24);
        canvas.drawText("THE SKY IS YOUR BATTLEFIELD", screenWidth / 2f, screenHeight / 2f - 110, paint);

        paint.setColor(Color.rgb(230, 55, 70));
        buttonRect.set(screenWidth / 2f - 180, screenHeight / 2f - 50, screenWidth / 2f + 180, screenHeight / 2f + 40);
        canvas.drawRoundRect(buttonRect, 25, 25, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(36);
        canvas.drawText("TAP TO START", screenWidth / 2f, screenHeight / 2f + 10, paint);
        paint.setTextSize(22);
        canvas.drawText("Drag to move the player", screenWidth / 2f, screenHeight / 2f + 95, paint);
        canvas.drawText("Dodge incoming missiles and bird flocks!", screenWidth / 2f, screenHeight / 2f + 130, paint);
        paint.setTextAlign(Paint.Align.LEFT);
    }

    // =========================================================
    // OBSTACLE CLASS
    // =========================================================
    public static class Obstacle {
        int type;
        float x;
        float y;
        float width;
        float height;
    }
}
