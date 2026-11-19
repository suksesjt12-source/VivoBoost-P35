# VivoBoost P35

VivoBoost P35 adalah toolkit diagnostik dan panduan optimasi untuk Vivo Y12s dengan MediaTek Helio P35. Versi ini menambahkan **Pro Performance Suite** yang tersedia langsung tanpa pembayaran.

## Fitur

- Dashboard RAM: penggunaan dan sisa memori berdasarkan Android ActivityManager.
- Dashboard penyimpanan: kapasitas terpakai dan tersedia.
- Status baterai: persentase, status pengisian, dan suhu baterai jika disediakan perangkat.
- Thermal status: membaca status termal Android 10+ jika didukung firmware.
- Profil Gaming, Seimbang, dan Hemat yang tersimpan secara lokal.
- Rekomendasi perawatan berdasarkan indikator memori, suhu baterai, dan ruang penyimpanan.
- Pintasan ke pengaturan resmi Android: Jangan Ganggu, aplikasi, layar, baterai, dan penyimpanan.

## Batasan yang disengaja

Aplikasi tidak melakukan root, overclock CPU/GPU, perubahan governor, pembersihan RAM paksa, atau penghentian proses sistem. Profil penggunaan adalah preset rekomendasi di dalam aplikasi; tindakan yang memerlukan hak istimewa tetap diarahkan ke Pengaturan Android. Suhu baterai bukan pembacaan suhu langsung CPU/GPU, dan status termal hanya tersedia jika Android/firmware mengekspos API tersebut.

## Build APK

GitHub Actions menjalankan `gradle assembleDebug` ketika ada push ke `main` atau `master`, atau dapat dijalankan manual dari tab Actions. APK debug diunggah sebagai artifact bernama `VivoBoostP35-debug`.

## Persyaratan

- Android 6.0+ (minSdk 23).
- Java 17 dan Android Gradle Plugin 8.7.3 untuk build.

Build CI harus selesai dengan status sukses sebelum APK dianggap siap.