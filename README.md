# Catatan Keuangan Android

Aplikasi pencatatan keuangan sederhana berbasis Android untuk mencatat pemasukan dan pengeluaran harian.

## Fitur

- Menampilkan saldo total
- Tambah transaksi pemasukan dan pengeluaran
- Edit transaksi
- Hapus transaksi dengan konfirmasi dialog
- Data tersimpan lokal menggunakan SQLite
- Daftar riwayat transaksi menggunakan RecyclerView
- Warna berbeda untuk pemasukan dan pengeluaran
- Filter transaksi: semua, pemasukan, dan pengeluaran
- Ringkasan total pemasukan dan total pengeluaran
- Grafik sederhana pemasukan vs pengeluaran menggunakan MPAndroidChart
- Export laporan transaksi ke file CSV
- Tampilan anime/pastel dan icon aplikasi custom

## Teknologi

- Android Studio
- Kotlin
- SQLite
- RecyclerView
- MPAndroidChart

## Cara Menjalankan

1. Buka Android Studio.
2. Pilih **Open**.
3. Pilih folder project ini.
4. Tunggu proses Gradle Sync selesai.
5. Jalankan aplikasi ke emulator atau HP Android.

## Cara Export CSV

1. Tambahkan beberapa transaksi terlebih dahulu.
2. Klik tombol **Export CSV** di halaman utama.
3. File laporan akan tersimpan di folder **Download** dengan format `.csv`.

## Catatan Pengumpulan

Format pengumpulan sesuai tugas:

- File APK
- Link repository GitHub
- README berisi screenshot dan cara menjalankan aplikasi

## Validasi Saldo
Aplikasi menolak transaksi pengeluaran yang lebih besar dari saldo saat ini. Jika saldo tidak cukup, aplikasi menampilkan pesan: **Stop, saldo anda tidak cukup**. Validasi ini juga berlaku saat mengedit transaksi agar saldo tidak menjadi minus.
