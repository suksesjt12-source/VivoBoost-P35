package com.vivoboost.p35;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Locale;

public class MainActivity extends Activity {
    private TextView ramValue, batteryValue, storageValue, statusValue;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildUi();
        refreshStats();
    }

    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + .5f); }

    private TextView text(String value, int size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setPadding(dp(2), dp(4), dp(2), dp(4));
        return t;
    }

    private LinearLayout card() {
        LinearLayout c = new LinearLayout(this);
        c.setOrientation(LinearLayout.VERTICAL);
        c.setPadding(dp(16), dp(12), dp(16), dp(12));
        c.setBackgroundColor(0xFFFFFFFF);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, 0, 0, dp(12));
        c.setLayoutParams(lp);
        return c;
    }

    private Button button(String label, Runnable action) {
        Button b = new Button(this);
        b.setText(label);
        b.setAllCaps(false);
        b.setOnClickListener(v -> action.run());
        return b;
    }

    private void buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(18), dp(18), dp(10));
        root.setBackgroundColor(0xFFF2F5FA);

        TextView header = text("VivoBoost P35", 26, 0xFF14243A);
        header.setTypeface(null, 1);
        root.addView(header);
        root.addView(text("Toolkit ringan untuk Vivo Y12s • Helio P35", 14, 0xFF526174));

        LinearLayout stats = card();
        stats.addView(text("STATUS PERANGKAT", 13, 0xFF516178));
        ramValue = text("RAM: membaca…", 18, 0xFF18283D);
        batteryValue = text("Baterai: membaca…", 18, 0xFF18283D);
        storageValue = text("Penyimpanan: membaca…", 18, 0xFF18283D);
        stats.addView(ramValue);
        stats.addView(batteryValue);
        stats.addView(storageValue);
        root.addView(stats);

        LinearLayout boost = card();
        boost.addView(text("QUICK CHECK", 16, 0xFF18283D));
        statusValue = text("Pilih tindakan untuk membuka pengaturan Android yang relevan.", 14, 0xFF526174);
        boost.addView(statusValue);
        boost.addView(button("Refresh status", this::refreshStats));
        boost.addView(button("Pengaturan baterai", () -> open(Settings.ACTION_BATTERY_SAVER_SETTINGS)));
        boost.addView(button("Pengelolaan aplikasi", () -> open(Settings.ACTION_APPLICATION_SETTINGS)));
        boost.addView(button("Penyimpanan", () -> open(Settings.ACTION_INTERNAL_STORAGE_SETTINGS)));
        boost.addView(button("Jangan Ganggu", () -> open(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)));
        root.addView(boost);

        LinearLayout profile = card();
        profile.addView(text("PROFIL PENGGUNAAN", 16, 0xFF18283D));
        profile.addView(text("Gaming: tutup aplikasi yang tidak digunakan secara manual, aktifkan Jangan Ganggu, turunkan grafis game bila FPS turun, dan hindari bermain sambil mengisi daya jika perangkat panas.", 14, 0xFF526174));
        profile.addView(button("Buka pengaturan layar", () -> open(Settings.ACTION_DISPLAY_SETTINGS)));
        root.addView(profile);

        setContentView(root);
    }

    private void open(String action) {
        try {
            startActivity(new Intent(action));
        } catch (Exception e) {
            if (statusValue != null) statusValue.setText("Pengaturan ini tidak tersedia pada firmware perangkat.");
        }
    }

    private void refreshStats() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        long total = mi.totalMem / 1024 / 1024;
        long avail = mi.availMem / 1024 / 1024;
        long used = total - avail;
        ramValue.setText(String.format(Locale.getDefault(), "RAM: %d / %d MB digunakan", used, total));

        Intent bi = registerReceiver(null, new android.content.IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (bi != null) {
            int level = bi.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = bi.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
            int temp = bi.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            int pct = scale > 0 ? level * 100 / scale : level;
            String t = temp >= 0 ? String.format(Locale.getDefault(), " • %.1f°C", temp / 10f) : "";
            batteryValue.setText("Baterai: " + pct + "%" + t);
        }

        StatFs sf = new StatFs(Environment.getDataDirectory().getPath());
        long free = sf.getAvailableBytes() / 1024 / 1024 / 1024;
        long totalStorage = sf.getTotalBytes() / 1024 / 1024 / 1024;
        storageValue.setText(String.format(Locale.getDefault(), "Penyimpanan: %d GB tersedia / %d GB", free, totalStorage));
        statusValue.setText("Data diperbarui. Aplikasi tidak mematikan proses sistem atau mengubah clock CPU/GPU.");
    }
}
