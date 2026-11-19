package com.vivoboost.p35;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StatFs;
import android.provider.Settings;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final String PREFS = "vivoboost_preferences";
    private static final String PROFILE_KEY = "performance_profile";
    private static final int INK = 0xFF17263B;
    private static final int MUTED = 0xFF627187;
    private static final int BLUE = 0xFF2357C6;
    private static final int GREEN = 0xFF16845B;
    private static final int AMBER = 0xFFB66A13;
    private static final int RED = 0xFFC13D3D;

    private TextView ramValue, ramDetail, batteryValue, batteryDetail;
    private TextView storageValue, storageDetail, thermalValue, thermalDetail;
    private TextView profileSummary, healthSummary, routineSummary;
    private ProgressBar ramBar, storageBar;
    private String selectedProfile = "Seimbang";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedProfile = getPreferences(MODE_PRIVATE).getString(PROFILE_KEY, "Seimbang");
        buildUi();
        refreshStats();
        showProfile();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ramValue != null) refreshStats();
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private TextView text(String value, int size, int color) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setPadding(dp(2), dp(4), dp(2), dp(4));
        return view;
    }

    private TextView heading(String value) {
        TextView view = text(value, 16, INK);
        view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return view;
    }

    private LinearLayout card() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(16), dp(13), dp(16), dp(13));
        layout.setBackgroundColor(0xFFFFFFFF);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, dp(12));
        layout.setLayoutParams(params);
        return layout;
    }

    private Button button(String label, Runnable action) {
        Button view = new Button(this);
        view.setText(label);
        view.setAllCaps(false);
        view.setOnClickListener(v -> action.run());
        return view;
    }

    private void buildUi() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(17), dp(18), dp(17), dp(18));
        root.setBackgroundColor(0xFFF1F4F9);

        TextView title = text("VivoBoost P35", 27, INK);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        root.addView(title);
        TextView subtitle = text("PRO PERFORMANCE SUITE  •  VIVO Y12s / HELIO P35", 11, BLUE);
        subtitle.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        root.addView(subtitle);
        root.addView(text("Toolkit diagnostik dan panduan optimasi perangkat.", 14, MUTED));

        LinearLayout health = card();
        health.addView(heading("Kondisi perangkat"));
        healthSummary = text("Menganalisis status perangkat…", 13, MUTED);
        health.addView(healthSummary);

        ramValue = heading("RAM: membaca…");
        ramDetail = text("", 13, MUTED);
        ramBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        health.addView(ramValue);
        health.addView(ramDetail);
        health.addView(ramBar);

        storageValue = heading("Penyimpanan: membaca…");
        storageDetail = text("", 13, MUTED);
        storageBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        health.addView(storageValue);
        health.addView(storageDetail);
        health.addView(storageBar);

        batteryValue = heading("Baterai: membaca…");
        batteryDetail = text("", 13, MUTED);
        thermalValue = heading("Suhu: membaca…");
        thermalDetail = text("", 13, MUTED);
        health.addView(batteryValue);
        health.addView(batteryDetail);
        health.addView(thermalValue);
        health.addView(thermalDetail);
        health.addView(button("Perbarui diagnostik", this::refreshStats));
        root.addView(health);

        LinearLayout profiles = card();
        profiles.addView(heading("Profil performa Pro"));
        profiles.addView(text("Pilih sasaran penggunaan. Profil disimpan di aplikasi dan memberi rekomendasi yang sesuai; Android tetap mengendalikan CPU, GPU, dan proses latar.", 13, MUTED));
        LinearLayout profileButtons = new LinearLayout(this);
        profileButtons.setOrientation(LinearLayout.HORIZONTAL);
        profileButtons.addView(button("Gaming", () -> setProfile("Gaming")),
                new LinearLayout.LayoutParams(0, -2, 1));
        profileButtons.addView(button("Seimbang", () -> setProfile("Seimbang")),
                new LinearLayout.LayoutParams(0, -2, 1));
        profileButtons.addView(button("Hemat", () -> setProfile("Hemat")),
                new LinearLayout.LayoutParams(0, -2, 1));
        profiles.addView(profileButtons);
        profileSummary = text("", 14, INK);
        profiles.addView(profileSummary);
        routineSummary = text("", 13, MUTED);
        profiles.addView(routineSummary);
        root.addView(profiles);

        LinearLayout gaming = card();
        gaming.addView(heading("Gaming session"));
        gaming.addView(text("Siapkan perangkat sebelum bermain tanpa memaksa perubahan sistem yang berisiko.", 13, MUTED));
        gaming.addView(button("Atur akses Jangan Ganggu", () -> open(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)));
        gaming.addView(button("Kelola aplikasi latar", () -> open(Settings.ACTION_APPLICATION_SETTINGS)));
        gaming.addView(button("Pengaturan layar", () -> open(Settings.ACTION_DISPLAY_SETTINGS)));
        root.addView(gaming);

        LinearLayout maintenance = card();
        maintenance.addView(heading("Perawatan sistem"));
        maintenance.addView(text("Gunakan menu resmi Android untuk mengelola konsumsi daya dan ruang penyimpanan.", 13, MUTED));
        maintenance.addView(button("Pengaturan baterai", () -> open(Settings.ACTION_BATTERY_SAVER_SETTINGS)));
        maintenance.addView(button("Analisis penyimpanan", () -> open(Settings.ACTION_INTERNAL_STORAGE_SETTINGS)));
        maintenance.addView(button("Info aplikasi VivoBoost", () -> openAppDetails()));
        root.addView(maintenance);

        LinearLayout note = card();
        note.addView(heading("Tentang fitur Pro"));
        note.addView(text("Fitur Pro di versi ini tersedia langsung tanpa pembayaran. Aplikasi tidak melakukan overclock, root, membersihkan RAM secara paksa, atau menjanjikan kenaikan FPS. Penghematan daya dan pembatasan aplikasi dilakukan melalui kontrol resmi Android.", 13, MUTED));
        root.addView(note);

        scroll.addView(root);
        setContentView(scroll);
    }

    private void setProfile(String profile) {
        selectedProfile = profile;
        getPreferences(MODE_PRIVATE).edit().putString(PROFILE_KEY, profile).apply();
        showProfile();
        Toast.makeText(this, "Profil " + profile + " dipilih", Toast.LENGTH_SHORT).show();
    }

    private void showProfile() {
        if (profileSummary == null || routineSummary == null) return;
        profileSummary.setText("Profil aktif: " + selectedProfile);
        if ("Gaming".equals(selectedProfile)) {
            routineSummary.setText("Saran: aktifkan Jangan Ganggu, tutup hanya aplikasi yang tidak diperlukan, pilih grafis game yang stabil, dan hentikan sesi bila perangkat terlalu panas.");
        } else if ("Hemat".equals(selectedProfile)) {
            routineSummary.setText("Saran: aktifkan Penghemat Baterai, kurangi kecerahan layar, dan batasi aktivitas aplikasi yang jarang digunakan melalui Pengaturan.");
        } else {
            routineSummary.setText("Saran: gunakan pengaturan default, jaga ruang kosong penyimpanan, dan sesuaikan grafis game berdasarkan kestabilan frame rate.");
        }
    }

    private void refreshStats() {
        try {
            ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memory = new ActivityManager.MemoryInfo();
            manager.getMemoryInfo(memory);
            long totalRam = memory.totalMem / 1024 / 1024;
            long freeRam = memory.availMem / 1024 / 1024;
            long usedRam = Math.max(0, totalRam - freeRam);
            int ramPercent = totalRam > 0 ? (int) (usedRam * 100 / totalRam) : 0;
            ramValue.setText(String.format(Locale.getDefault(), "RAM digunakan: %d%%", ramPercent));
            ramDetail.setText(String.format(Locale.getDefault(), "%,d MB digunakan • %,d MB tersedia dari %,d MB",
                    usedRam, freeRam, totalRam));
            ramBar.setMax(100);
            ramBar.setProgress(ramPercent);

            StatFs stats = new StatFs(Environment.getDataDirectory().getPath());
            long totalBytes = stats.getTotalBytes();
            long freeBytes = stats.getAvailableBytes();
            long usedBytes = Math.max(0, totalBytes - freeBytes);
            int storagePercent = totalBytes > 0 ? (int) (usedBytes * 100 / totalBytes) : 0;
            storageValue.setText(String.format(Locale.getDefault(), "Penyimpanan terpakai: %d%%", storagePercent));
            storageDetail.setText(String.format(Locale.getDefault(), "%.1f GB tersedia dari %.1f GB",
                    freeBytes / 1073741824.0, totalBytes / 1073741824.0));
            storageBar.setMax(100);
            storageBar.setProgress(storagePercent);

            Intent battery = registerReceiver(null, new android.content.IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            int temperature = -1;
            if (battery != null) {
                int level = battery.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = battery.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
                temperature = battery.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
                int percent = scale > 0 && level >= 0 ? level * 100 / scale : -1;
                int status = battery.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                boolean charging = status == BatteryManager.BATTERY_STATUS_CHARGING
                        || status == BatteryManager.BATTERY_STATUS_FULL;
                batteryValue.setText("Baterai: " + (percent >= 0 ? percent + "%" : "tidak tersedia"));
                batteryDetail.setText(charging ? "Status: sedang mengisi daya" : "Status: tidak sedang mengisi daya");
                if (temperature >= 0) {
                    batteryDetail.append(String.format(Locale.getDefault(), " • Suhu baterai %.1f°C", temperature / 10.0));
                }
            }

            String thermal = getThermalDescription();
            thermalValue.setText("Status termal: " + thermal);
            if (temperature >= 0) {
                float celsius = temperature / 10.0f;
                thermalDetail.setText(String.format(Locale.getDefault(), "Suhu baterai %.1f°C. Ini bukan suhu langsung CPU/GPU.", celsius));
                if (celsius >= 45.0f) {
                    thermalDetail.setTextColor(RED);
                } else if (celsius >= 42.0f) {
                    thermalDetail.setTextColor(AMBER);
                } else {
                    thermalDetail.setTextColor(MUTED);
                }
            } else {
                thermalDetail.setText("Data suhu baterai tidak tersedia pada perangkat ini.");
                thermalDetail.setTextColor(MUTED);
            }

            String condition;
            if (temperature >= 450) {
                condition = "Suhu baterai tinggi. Hentikan aktivitas berat dan biarkan perangkat mendingin.";
            } else if (memory.lowMemory || ramPercent >= 90 || storagePercent >= 95) {
                condition = "Sumber daya mulai terbatas. Periksa aplikasi dan ruang penyimpanan melalui Pengaturan.";
            } else if (temperature >= 420 || storagePercent >= 85) {
                condition = "Perlu pemantauan. Jaga suhu dan sisakan ruang penyimpanan.";
            } else {
                condition = "Tidak ada indikator kritis yang terdeteksi dari pemeriksaan dasar.";
            }
            healthSummary.setText(condition);
            healthSummary.setTextColor((temperature >= 450 || memory.lowMemory || storagePercent >= 95) ? RED : INK);
        } catch (Exception error) {
            healthSummary.setText("Diagnostik belum dapat dibaca. Coba perbarui kembali.");
        }
    }

    private String getThermalDescription() {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            try {
                PowerManager power = (PowerManager) getSystemService(POWER_SERVICE);
                int status = power.getCurrentThermalStatus();
                switch (status) {
                    case PowerManager.THERMAL_STATUS_NONE: return "Normal";
                    case PowerManager.THERMAL_STATUS_LIGHT: return "Hangat";
                    case PowerManager.THERMAL_STATUS_MODERATE: return "Sedang";
                    case PowerManager.THERMAL_STATUS_SEVERE: return "Tinggi";
                    case PowerManager.THERMAL_STATUS_CRITICAL: return "Kritis";
                    case PowerManager.THERMAL_STATUS_EMERGENCY: return "Darurat";
                    case PowerManager.THERMAL_STATUS_SHUTDOWN: return "Shutdown";
                    default: return "Tidak diketahui";
                }
            } catch (Exception ignored) {
                return "Tidak tersedia";
            }
        }
        return "Tidak didukung Android versi ini";
    }

    private void openAppDetails() {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(android.net.Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } catch (Exception error) {
            Toast.makeText(this, "Pengaturan aplikasi tidak tersedia.", Toast.LENGTH_SHORT).show();
        }
    }

    private void open(String action) {
        try {
            startActivity(new Intent(action));
        } catch (Exception error) {
            Toast.makeText(this, "Menu ini tidak tersedia pada firmware perangkat.", Toast.LENGTH_SHORT).show();
        }
    }
}
