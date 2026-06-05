package com.example.aplikasikeuangan

data class Transaksi(
    val id: Int,
    val nama: String,
    val jumlah: Int,
    val tipe: String,
    val tanggal: String
)
