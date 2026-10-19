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
        layout.setPadding(30, 30, 30, 30);

        TextView title = new TextView(this);
        title.setText("by.x1dead\nnez3r Loader");
        title.setTextColor(Color.RED);
        title.setTextSize(22);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        Button btnNez = new Button(this);
        btnNez.setText("Внедрить nez3r");
        // Вызываем оригинальный скрипт инжекта
        btnNez.setOnClickListener(v -> runSuCommand("sh /data/local/tmp/inj.sh"));
        layout.addView(btnNez);

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

    private void runSuCommand(String command) {
        try {
            Process process = Runtime.getRuntime().exec("su -c " + command);
            process.waitFor();
            Toast.makeText(this, "Запуск...", Toast.LENGTH_SHORT).show();
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
