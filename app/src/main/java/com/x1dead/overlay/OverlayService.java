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
import android.widget.Toast;

public class OverlayService extends Service {
    private WindowManager windowManager;
    private LinearLayout layout;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.parseColor("#CC000000"));

        TextView title = new TextView(this);
        title.setText("by.x1dead\nStandRise AutoWin");
        title.setTextColor(Color.RED);
        title.setTextSize(20);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        Button winBtn = new Button(this);
        winBtn.setText("WIN 8:0 NOW");
        winBtn.setOnClickListener(v -> {
            try {
                // Создаем триггер-файл через су
                Process process = Runtime.getRuntime().exec("su -c touch /data/local/tmp/win_trigger");
                process.waitFor();
                Toast.makeText(this, "Счет 8:0 применен!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
        layout.addView(winBtn);

        int layoutFlag = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ?
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                WindowManager.LayoutParams.TYPE_PHONE;
        
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
