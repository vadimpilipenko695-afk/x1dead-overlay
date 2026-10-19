        Button btnNez = new Button(this);
        btnNez.setText("Запустить x1dead");
        btnNez.setOnClickListener(v -> {
            // Запускаем родной инжектор (он требует PID игры и путь к .so)
            String cmd = "su -c \"/data/local/tmp/inj $(pidof com.standrise.game) /data/local/tmp/libpayload.so\"";
            runSuCommand(cmd);
        });
        layout.addView(btnNez);
