package com.vivoboost.p35.privileged;

import android.os.RemoteException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PerformanceUserService extends IPerformanceService.Stub {
    public PerformanceUserService() {}

    @Override
    public String applyProfile(String profile) throws RemoteException {
        if (profile == null) return "Profil kosong ditolak.";
        String[] commands;
        String label;
        switch (profile) {
            case "GAMING":
                label = "Force Gaming";
                commands = new String[] {
                    "settings put global window_animation_scale 0.5",
                    "settings put global transition_animation_scale 0.5",
                    "settings put global animator_duration_scale 0.5",
                    "cmd power set-mode 0"
                };
                break;
            case "BALANCED":
                label = "Balanced";
                commands = new String[] {
                    "settings put global window_animation_scale 1.0",
                    "settings put global transition_animation_scale 1.0",
                    "settings put global animator_duration_scale 1.0"
                };
                break;
            case "POWER_SAVE":
                label = "Penghemat Baterai";
                commands = new String[] { "cmd power set-mode 1" };
                break;
            case "POWER_NORMAL":
                label = "Mode Daya Normal";
                commands = new String[] { "cmd power set-mode 0" };
                break;
            default:
                return "Preset tidak dikenal; tidak ada perintah dijalankan.";
        }

        StringBuilder report = new StringBuilder(label).append(": ");
        int success = 0;
        for (String command : commands) {
            try {
                Process process = new ProcessBuilder("sh", "-c", command)
                        .redirectErrorStream(true).start();
                StringBuilder output = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (output.length() < 240) output.append(line).append(' ');
                    }
                }
                int exitCode = process.waitFor();
                if (exitCode == 0) success++;
                else report.append("Perintah ditolak (").append(exitCode).append("): ")
                        .append(command.substring(0, Math.min(command.length(), 36))).append(". ");
            } catch (Exception error) {
                report.append("Gagal menjalankan satu pengaturan. ");
            }
        }
        report.append(success).append('/').append(commands.length).append(" pengaturan berhasil.");
        return report.toString();
    }

    public void destroy() {
        System.exit(0);
    }
}
