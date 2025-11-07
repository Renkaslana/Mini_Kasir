# Mini Kasir - Aplikasi Kasir UMKM

Aplikasi kasir sederhana untuk UMKM dengan fitur manajemen produk, transaksi, laporan penjualan, dan notifikasi. Dibangun menggunakan Kotlin dengan arsitektur MVVM.

## 📋 Daftar Isi

- [Persyaratan Sistem](#persyaratan-sistem)
- [Instalasi dan Setup](#instalasi-dan-setup)
- [Konfigurasi Project](#konfigurasi-project)
- [Menjalankan Aplikasi](#menjalankan-aplikasi)
- [Troubleshooting](#troubleshooting)
- [Fitur Aplikasi](#fitur-aplikasi)
- [Struktur Project](#struktur-project)

## 🔧 Persyaratan Sistem

### Android Studio
- **Versi Direkomendasikan:** Android Studio Hedgehog (2023.1.1) atau lebih baru
- **Versi Minimum:** Android Studio Flamingo (2022.2.1)
- **JDK:** JDK 17 atau lebih tinggi

### Android SDK
- **compileSdkVersion:** 34 (Android 14)
- **targetSdkVersion:** 34 (Android 14)
- **minSdkVersion:** 30 (Android 11)
- **Kompatibilitas:** Android 11 hingga Android 14

### Build Tools
- **Gradle:** 8.2.1
- **Android Gradle Plugin (AGP):** 8.2.1
- **Kotlin:** 1.9.22
- **Java Compatibility:** Java 17

## 📦 Instalasi dan Setup

### 1. Clone atau Download Project

```bash
git clone <repository-url>
cd Smart-MiniKasir
```

Atau download dan ekstrak file ZIP project.

### 2. Buka Project di Android Studio

1. Buka Android Studio
2. Pilih **File > Open**
3. Navigasi ke folder `Smart-MiniKasir`
4. Klik **OK**

### 3. Tunggu Gradle Sync

Android Studio akan otomatis melakukan Gradle sync. Proses ini mungkin memakan waktu beberapa menit untuk pertama kali karena akan mendownload dependencies.

**Catatan:** Pastikan koneksi internet aktif untuk download dependencies.

### 4. Verifikasi Konfigurasi

Pastikan konfigurasi berikut sudah benar:

#### File: `build.gradle` (Project Level)
```gradle
buildscript {
    ext.kotlin_version = "1.9.22"
    dependencies {
        classpath "com.android.tools.build:gradle:8.2.1"
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
    }
}
```

#### File: `gradle/wrapper/gradle-wrapper.properties`
```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.2.1-bin.zip
```

#### File: `app/build.gradle` (Module Level)
```gradle
android {
    compileSdk 34
    minSdk 30
    targetSdk 34
    
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = '17'
    }
}
```

## 🚀 Menjalankan Aplikasi

### Metode 1: Menggunakan Android Studio

1. **Sambungkan Device atau Emulator:**
   - Sambungkan Android device via USB (pastikan USB Debugging aktif)
   - Atau buat/start Android Emulator dari Android Studio

2. **Pilih Device:**
   - Di toolbar Android Studio, pilih device dari dropdown di samping tombol Run

3. **Run Aplikasi:**
   - Klik tombol **Run** (ikon hijau play) atau tekan `Shift + F10`
   - Atau pilih **Run > Run 'app'** dari menu

### Metode 2: Menggunakan Command Line

```bash
# Build debug APK
./gradlew assembleDebug

# Install ke device yang terhubung
./gradlew installDebug

# Atau langsung build dan install
./gradlew installDebug
```

### Metode 3: Generate APK

1. **Build APK:**
   - Pilih **Build > Build Bundle(s) / APK(s) > Build APK(s)**
   - Tunggu proses build selesai

2. **Lokasi APK:**
   - APK akan tersimpan di: `app/build/outputs/apk/debug/app-debug.apk`

3. **Install APK:**
   - Transfer APK ke device Android
   - Buka file APK dan install

## ⚠️ Troubleshooting

### Error: Gradle Sync Failed

**Masalah:** Gradle sync gagal dengan berbagai error.

**Solusi:**
1. **Invalidate Caches:**
   - File > Invalidate Caches... > Invalidate and Restart

2. **Clean Project:**
   ```bash
   ./gradlew clean
   ```
   Atau di Android Studio: **Build > Clean Project**

3. **Rebuild Project:**
   ```bash
   ./gradlew build
   ```
   Atau di Android Studio: **Build > Rebuild Project**

4. **Hapus Folder .gradle:**
   - Tutup Android Studio
   - Hapus folder `.gradle` di root project
   - Buka kembali project

### Error: SDK Not Found

**Masalah:** Android SDK tidak ditemukan.

**Solusi:**
1. Buka **File > Project Structure**
2. Di tab **SDK Location**, pastikan Android SDK path sudah benar
3. Jika belum terinstall, klik **Download** untuk install SDK yang diperlukan
4. Pastikan Android SDK Platform 34 sudah terinstall

### Error: Theme Not Found

**Masalah:** `Theme.MaterialComponents.Light.NoActionBar` tidak ditemukan.

**Solusi:**
1. Pastikan Material Components dependency sudah ada:
   ```gradle
   implementation 'com.google.android.material:material:1.11.0'
   ```
2. Sync project kembali

### Error: Room Database Migration

**Masalah:** Error saat migrasi database Room.

**Solusi:**
- Database menggunakan `fallbackToDestructiveMigration()` untuk development
- Untuk production, buat migration script yang sesuai

### Error: Permission Denied

**Masalah:** Aplikasi tidak bisa mengakses kamera atau storage.

**Solusi:**
1. Pastikan permission sudah dideklarasikan di `AndroidManifest.xml`
2. Untuk Android 11+, pastikan runtime permission sudah diberikan
3. Test di device fisik untuk permission kamera

### Error: Barcode Scanner Tidak Berfungsi

**Masalah:** Scanner barcode tidak bisa dibuka.

**Solusi:**
1. Pastikan permission kamera sudah diberikan
2. Test di device fisik (emulator mungkin tidak memiliki kamera)
3. Pastikan dependency ZXing sudah terinstall:
   ```gradle
   implementation 'com.journeyapps:zxing-android-embedded:4.3.0'
   ```

### Error: PDF Generation Failed

**Masalah:** Error saat generate PDF struk.

**Solusi:**
1. Pastikan permission storage sudah diberikan
2. Untuk Android 11+, gunakan Scoped Storage
3. Pastikan dependency iText sudah terinstall:
   ```gradle
   implementation 'com.itextpdf:itext7-core:7.2.5'
   ```

### Build Error: Out of Memory

**Masalah:** Build gagal karena kehabisan memory.

**Solusi:**
1. Tambahkan di `gradle.properties`:
   ```properties
   org.gradle.jvmargs=-Xmx2048m -XX:MaxMetaspaceSize=512m
   ```
2. Restart Android Studio

### Error: Kotlin Version Mismatch

**Masalah:** Versi Kotlin tidak kompatibel.

**Solusi:**
1. Pastikan versi Kotlin di `build.gradle` sesuai:
   ```gradle
   ext.kotlin_version = "1.9.22"
   ```
2. Sync project kembali

## 📱 Fitur Aplikasi

### 1. Login & Logout
- Login dengan username: `admin` dan password: `admin123`
- Auto-login menggunakan SharedPreferences
- Logout dengan menghapus session

### 2. Dashboard
- Menampilkan statistik:
  - Total Produk
  - Total Transaksi Hari Ini
  - Produk dengan Stok Menipis
- Menu navigasi cepat ke fitur utama

### 3. Manajemen Produk
- CRUD (Create, Read, Update, Delete) produk
- Scan barcode untuk input produk
- Filter dan pencarian produk
- Notifikasi stok menipis
- Notifikasi produk berhasil ditambahkan

### 4. Transaksi Kasir
- Tambah produk via barcode atau pencarian
- Kalkulasi total dan kembalian otomatis
- Pengurangan stok otomatis
- Generate PDF struk transaksi
- Riwayat transaksi
- Notifikasi transaksi berhasil

### 5. Laporan Penjualan
- Grafik penjualan harian, mingguan, bulanan
- Filter berdasarkan tanggal
- Export laporan ke PDF
- Visualisasi data menggunakan MPAndroidChart

### 6. Notifikasi
- Notifikasi transaksi berhasil
- Notifikasi stok menipis
- Notifikasi produk baru ditambahkan
- Laporan mingguan otomatis (dalam pengembangan)
- Daftar notifikasi terpusat

### 7. Pengaturan
- Reset data produk
- Reset data transaksi
- Ubah password admin
- Tema aplikasi (dalam pengembangan)

## 📁 Struktur Project

```
Smart-MiniKasir/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/minikasirpintarfree/app/
│   │   │   │   ├── data/
│   │   │   │   │   ├── dao/          # Data Access Objects (Room)
│   │   │   │   │   ├── database/     # Room Database & Converters
│   │   │   │   │   ├── model/        # Data Models
│   │   │   │   │   └── repository/   # Repository Pattern
│   │   │   │   ├── ui/               # Activities & Fragments
│   │   │   │   │   ├── dashboard/
│   │   │   │   │   ├── login/
│   │   │   │   │   ├── laporan/
│   │   │   │   │   ├── notifications/
│   │   │   │   │   ├── produk/
│   │   │   │   │   ├── settings/
│   │   │   │   │   └── transaksi/
│   │   │   │   ├── utils/            # Utility Classes
│   │   │   │   ├── viewmodel/        # ViewModels & Factories
│   │   │   │   └── MainActivity.kt
│   │   │   ├── res/                   # Resources
│   │   │   │   ├── drawable/         # Icons & Drawables
│   │   │   │   ├── layout/           # XML Layouts
│   │   │   │   ├── menu/             # Menu Resources
│   │   │   │   ├── values/           # Strings, Colors, Themes
│   │   │   └── AndroidManifest.xml
│   │   └── test/                      # Unit Tests
│   ├── build.gradle                   # App Module Build Config
│   └── proguard-rules.pro            # ProGuard Rules
├── build.gradle                       # Project Level Build Config
├── settings.gradle                    # Project Settings
├── gradle.properties                  # Gradle Properties
└── README.md                          # Dokumentasi (File ini)
```

## 🔑 Kredensial Default

- **Username:** `admin`
- **Password:** `admin123`

**Peringatan:** Ubah password default setelah instalasi pertama untuk keamanan!

## 📚 Dependencies Utama

- **Material Components:** 1.11.0
- **Room Database:** 2.6.1
- **ViewModel & LiveData:** 2.7.0
- **Navigation Component:** 2.7.6
- **ZXing Barcode Scanner:** 4.3.0
- **MPAndroidChart:** 3.1.0
- **iText PDF:** 7.2.5
- **Glide:** 4.16.0
- **Kotlin Coroutines:** 1.7.3

## 🛠️ Teknologi yang Digunakan

- **Bahasa:** Kotlin
- **Arsitektur:** MVVM (Model-View-ViewModel)
- **Database:** Room Persistence Library
- **UI:** Material Design 3
- **Binding:** View Binding & Data Binding
- **Async:** Kotlin Coroutines & Flow
- **Navigation:** Navigation Component
- **Charts:** MPAndroidChart
- **PDF:** iText 7
- **Image Loading:** Glide

## 📝 Catatan Penting

1. **Database:** Database Room akan dibuat otomatis saat pertama kali aplikasi dijalankan
2. **Permissions:** Aplikasi memerlukan permission kamera untuk scan barcode
3. **Storage:** PDF struk disimpan di internal storage aplikasi
4. **Notifications:** Notifikasi memerlukan permission POST_NOTIFICATIONS untuk Android 13+
5. **Theme:** Aplikasi menggunakan light theme (dark mode dalam pengembangan)

## 🤝 Kontribusi

Jika menemukan bug atau ingin menambahkan fitur, silakan buat issue atau pull request.

## 📄 Lisensi

Project ini dibuat untuk keperluan edukasi dan penggunaan UMKM.

---

**Dibuat dengan ❤️ menggunakan Kotlin dan Android Studio**

Untuk pertanyaan atau bantuan lebih lanjut, silakan buat issue di repository ini.
