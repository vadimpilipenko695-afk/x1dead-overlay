package com.x1dead.overlay;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OverlayService extends Service {
    private WindowManager windowManager;
    private LinearLayout layout;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // Создаем контейнер для меню
        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.parseColor("#CC000000")); // Черный полупрозрачный фон

        // Заголовок
        TextView title = new TextView(this);
        title.setText("by.x1dead\nStandRise AutoWin");
        title.setTextColor(Color.RED);
        title.setTextSize(20);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        // Кнопка Вин
        Button winBtn = new Button(this);
        winBtn.setText("WIN 8:0 NOW");
        winBtn.setOnClickListener(v -> {
            // Здесь будет вызов Root-команды для записи в память
            // Мы вызовем наш бинарник, который сделает всё за нас
            Process process = null;
            try {
                process = Runtime.getRuntime().exec("su -c /data/local/tmp/x1dead_win");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        layout.addView(winBtn);

        // Настройки окна (поверх всех приложений)
        int layoutFlag;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            layoutFlag = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutFlag = WindowManager.LayoutParams.TYPE_PHONE;
        }
        
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutFlag,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 50;
        params.y = 200;

        windowManager.addView(layout, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (layout != null) windowManager.removeView(layout);
    }
}
