package com.x1dead.overlay;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (!isRooted()) {
            Toast.makeText(this, "РАЗРЕШИТЕ РУТ ДОСТУП! Без него софт не работает.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Button btn = new Button(this);
        btn.setText("Запустить меню nez3r");
        btn.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            } else {
                startService(new Intent(this, OverlayService.class));
                Toast.makeText(this, "Меню запущено!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        setContentView(btn);
    }

    private boolean isRooted() {
        String[] paths = {"/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su", "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"};
        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[] { "/system/xbin/which", "su" });
            java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) return true;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
        return false;
    }
}
