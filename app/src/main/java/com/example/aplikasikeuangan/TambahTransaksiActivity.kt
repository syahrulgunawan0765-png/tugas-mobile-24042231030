package com.example.aplikasikeuangan

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast

class TambahTransaksiActivity : Activity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var tvFormTitle: TextView
    private lateinit var tvFormSubtitle: TextView
    private lateinit var etNama: EditText
    private lateinit var etJumlah: EditText
    private lateinit var rbPemasukan: RadioButton
    private lateinit var rbPengeluaran: RadioButton
    private lateinit var btnSimpan: Button
    private lateinit var btnBatal: Button

    private var modeEdit = false
    private var idTransaksi = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_transaksi)

        dbHelper = DatabaseHelper(this)
        tvFormTitle = findViewById(R.id.tvFormTitle)
        tvFormSubtitle = findViewById(R.id.tvFormSubtitle)
        etNama = findViewById(R.id.etNama)
        etJumlah = findViewById(R.id.etJumlah)
        rbPemasukan = findViewById(R.id.rbPemasukan)
        rbPengeluaran = findViewById(R.id.rbPengeluaran)
        btnSimpan = findViewById(R.id.btnSimpan)
        btnBatal = findViewById(R.id.btnBatal)

        modeEdit = intent.getStringExtra("mode") == "edit"
        if (modeEdit) {
            isiDataEdit()
        }

        btnSimpan.setOnClickListener {
            simpanTransaksi()
        }

        btnBatal.setOnClickListener {
            finish()
        }
    }

    private fun isiDataEdit() {
        idTransaksi = intent.getIntExtra("id", -1)
        val nama = intent.getStringExtra("nama") ?: ""
        val jumlah = intent.getIntExtra("jumlah", 0)
        val tipe = intent.getStringExtra("tipe") ?: "pemasukan"

        tvFormTitle.text = "Edit Transaksi"
        tvFormSubtitle.text = "Perbarui data transaksi yang sudah dicatat"
        btnSimpan.text = "Update Transaksi"

        etNama.setText(nama)
        etJumlah.setText(jumlah.toString())

        if (tipe == "pemasukan") {
            rbPemasukan.isChecked = true
        } else {
            rbPengeluaran.isChecked = true
        }
    }

    private fun simpanTransaksi() {
        val nama = etNama.text.toString().trim()
        val jumlahText = etJumlah.text.toString().trim()

        if (nama.isEmpty()) {
            etNama.error = "Nama transaksi wajib diisi"
            return
        }

        if (jumlahText.isEmpty()) {
            etJumlah.error = "Jumlah wajib diisi"
            return
        }

        val jumlah = jumlahText.toIntOrNull()
        if (jumlah == null || jumlah <= 0) {
            etJumlah.error = "Jumlah harus angka lebih dari 0"
            return
        }

        val tipe = if (rbPemasukan.isChecked) "pemasukan" else "pengeluaran"

        if (modeEdit && idTransaksi != -1) {
            dbHelper.updateTransaksi(idTransaksi, nama, jumlah, tipe)
            Toast.makeText(this, "Transaksi berhasil diupdate", Toast.LENGTH_SHORT).show()
        } else {
            dbHelper.tambahTransaksi(nama, jumlah, tipe)
            Toast.makeText(this, "Transaksi berhasil disimpan", Toast.LENGTH_SHORT).show()
        }

        finish()
    }
}
