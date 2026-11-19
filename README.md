# VivoBoost P35

VivoBoost P35 adalah toolkit diagnostik dan pengaturan performa untuk Vivo Y12s / MediaTek Helio P35.

## Fitur

- Dashboard RAM, penyimpanan, baterai, suhu baterai, dan status termal Android.
- Profil Gaming, Seimbang, dan Hemat yang tersimpan lokal.
- Force Performance melalui Shizuku/ADB dengan izin eksplisit.
- Force Gaming mengatur tiga skala animasi Android ke 0,5x.
- Balanced mengembalikan skala animasi ke 1,0x.
- Tombol terpisah untuk mengaktifkan atau menonaktifkan Penghemat Baterai.
- Pintasan pengaturan Android untuk aplikasi, baterai, layar, penyimpanan, dan Jangan Ganggu.

## Mengaktifkan Force Performance

1. Pasang dan jalankan aplikasi Shizuku.
2. Pada Android 11 atau lebih baru, Shizuku dapat dimulai melalui Wireless debugging dari perangkat itu sendiri. Ikuti petunjuk di aplikasi Shizuku.
3. Buka VivoBoost P35, tekan **Hubungkan / minta izin Shizuku**, lalu setujui izin yang diminta.
4. Pilih preset yang ingin diterapkan. Hasil perintah akan ditampilkan di status Shizuku.

Shizuku non-root menggunakan identitas ADB/shell. Dukungan perintah dapat berbeda menurut versi Android dan firmware Vivo. Jika suatu perintah ditolak, aplikasi menampilkan jumlah pengaturan yang berhasil; tidak ada jaminan semua perangkat menerima semua perintah.

## Batasan dan keselamatan

- Force Gaming hanya mengubah skala animasi antarmuka ke 0,5x; pengaturan baterai tidak diubah otomatis.
- Preset Balanced mengembalikan skala animasi ke 1,0x.
- Mode Penghemat Baterai memiliki kontrol terpisah agar tidak berubah tanpa pilihan pengguna.
- Aplikasi tidak mengubah clock CPU/GPU, governor, tegangan, batas termal, atau proteksi suhu.
- Tidak ada pembersihan RAM paksa atau penghentian proses sistem.
- Suhu baterai bukan suhu langsung CPU/GPU. Status termal Android bergantung dukungan firmware.

## Build APK

GitHub Actions menjalankan `gradle assembleDebug` pada push ke `main` atau `master`, atau secara manual melalui tab Actions. APK debug diunggah sebagai artifact `VivoBoostP35-debug`.

## Build requirements

- Android 6.0+ (minSdk 23).
- Java 17 dan Android Gradle Plugin 8.7.3.
- Shizuku API 13.1.5 dan core library desugaring.
