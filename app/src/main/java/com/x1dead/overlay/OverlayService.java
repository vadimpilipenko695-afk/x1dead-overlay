package com.x1dead.overlay;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class OverlayService extends Service {
    private WindowManager windowManager;
    private LinearLayout layout;
    private WindowManager.LayoutParams params;
    
    // Переменные для перемещения
    private float initialX, initialY;
    private int initialTouchX, initialTouchY;
    private boolean isDragging = false;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.parseColor("#CC000000"));
        layout.setPadding(30, 30, 30, 30);

        TextView title = new TextView(this);
        title.setText("by.x1dead\nnez3r Loader");
        title.setTextColor(Color.RED);
        title.setTextSize(22);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        Button btnNez = new Button(this);
        btnNez.setText("Внедрить nez3r");
        btnNez.setOnClickListener(v -> runSuCommand("sh /data/local/tmp/inj_auto.sh"));
        layout.addView(btnNez);

        int layoutFlag = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ?
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                WindowManager.LayoutParams.TYPE_PHONE;
        
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutFlag,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 50;
        params.y = 200;

        // --- ЛОГИКА ПЕРЕТАСКИВАНИЯ ---
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = (int) event.getRawX();
                        initialTouchY = (int) event.getRawY();
                        isDragging = false;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        int deltaX = (int) event.getRawX() - initialTouchX;
                        int deltaY = (int) event.getRawY() - initialTouchY;
                        
                        // Если сдвинули больше чем на 10 пикселей, считаем это перетаскиванием
                        if (Math.abs(deltaX) > 10 || Math.abs(deltaY) > 10) {
                            isDragging = true;
                            params.x = (int) initialX + deltaX;
                            params.y = (int) initialY + deltaY;
                            windowManager.updateViewLayout(layout, params);
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        // Если это было просто нажатие (а не перетаскивание)
                        if (!isDragging) {
                            // Здесь можно добавить логику, но пока пусть кнопка работает как обычно
                            v.performClick();
                        }
                        return true;
                }
                return false;
            }
        });

        // --- ДОЛГОЕ НАЖАТИЕ ДЛЯ СКРЫТИЯ ---
        layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(OverlayService.this, "Меню скрыто", Toast.LENGTH_SHORT).show();
                stopSelf(); // Останавливаем сервис и убираем оверлей
                return true;
            }
        });

        windowManager.addView(layout, params);
    }

    private void runSuCommand(String command) {
        try {
            Process process = Runtime.getRuntime().exec("su -c " + command);
            process.waitFor();
            Toast.makeText(this, "Запуск nez3r...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (layout != null) windowManager.removeView(layout);
    }
}
