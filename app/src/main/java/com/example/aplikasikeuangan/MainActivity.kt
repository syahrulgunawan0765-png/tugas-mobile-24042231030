package com.example.aplikasikeuangan

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {

    private lateinit var dbHelper: DatabaseHelper

    private lateinit var tvSaldo: TextView
    private lateinit var tvTotalPemasukan: TextView
    private lateinit var tvTotalPengeluaran: TextView
    private lateinit var tvJumlahTransaksi: TextView
    private lateinit var tvKosong: TextView

    private lateinit var btnTambah: Button
    private lateinit var btnExportCsv: Button
    private lateinit var rgFilter: RadioGroup
    private lateinit var rvTransaksi: RecyclerView
    private lateinit var pieChart: PieChart

    private var filterAktif = "semua"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)

        tvSaldo = findViewById(R.id.tvSaldo)
        tvTotalPemasukan = findViewById(R.id.tvTotalPemasukan)
        tvTotalPengeluaran = findViewById(R.id.tvTotalPengeluaran)
        tvJumlahTransaksi = findViewById(R.id.tvJumlahTransaksi)
        tvKosong = findViewById(R.id.tvKosong)

        btnTambah = findViewById(R.id.btnTambah)
        btnExportCsv = findViewById(R.id.btnExportCsv)
        rgFilter = findViewById(R.id.rgFilter)
        rvTransaksi = findViewById(R.id.rvTransaksi)
        pieChart = findViewById(R.id.pieChart)

        rvTransaksi.layoutManager = LinearLayoutManager(this)
        aturTampilanGrafik()

        btnTambah.setOnClickListener {
            startActivity(Intent(this, TambahTransaksiActivity::class.java))
        }

        btnExportCsv.setOnClickListener {
            exportCsv()
        }

        rgFilter.setOnCheckedChangeListener { _, checkedId ->
            filterAktif = when (checkedId) {
                R.id.rbPemasukan -> "pemasukan"
                R.id.rbPengeluaran -> "pengeluaran"
                else -> "semua"
            }
            tampilkanData()
        }
    }

    override fun onResume() {
        super.onResume()
        tampilkanData()
    }

    private fun tampilkanData() {
        val semuaData = dbHelper.getSemuaTransaksi()

        var saldo = 0
        var totalPemasukan = 0
        var totalPengeluaran = 0

        for (transaksi in semuaData) {
            if (transaksi.tipe == "pemasukan") {
                saldo += transaksi.jumlah
                totalPemasukan += transaksi.jumlah
            } else {
                saldo -= transaksi.jumlah
                totalPengeluaran += transaksi.jumlah
            }
        }

        tvSaldo.text = formatRupiah(saldo)
        tvTotalPemasukan.text = formatRupiah(totalPemasukan)
        tvTotalPengeluaran.text = formatRupiah(totalPengeluaran)
        tvJumlahTransaksi.text = "${semuaData.size} transaksi tersimpan"
        tampilkanGrafik(totalPemasukan, totalPengeluaran)

        val dataFilter = when (filterAktif) {
            "pemasukan" -> semuaData.filter { it.tipe == "pemasukan" }
            "pengeluaran" -> semuaData.filter { it.tipe == "pengeluaran" }
            else -> semuaData
        }

        if (dataFilter.isEmpty()) {
            tvKosong.visibility = View.VISIBLE
            rvTransaksi.visibility = View.GONE
        } else {
            tvKosong.visibility = View.GONE
            rvTransaksi.visibility = View.VISIBLE
        }

        rvTransaksi.adapter = TransaksiAdapter(
            list = dataFilter,
            onEditClick = { transaksi -> bukaEditTransaksi(transaksi) },
            onDeleteClick = { transaksi -> konfirmasiHapus(transaksi) }
        )
    }

    private fun aturTampilanGrafik() {
        pieChart.description.isEnabled = false
        pieChart.setUsePercentValues(false)
        pieChart.setDrawEntryLabels(false)
        pieChart.isRotationEnabled = true
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.setNoDataText("Belum ada data grafik")
        pieChart.legend.textColor = Color.rgb(59, 42, 74)
        pieChart.legend.textSize = 12f
    }

    private fun tampilkanGrafik(totalPemasukan: Int, totalPengeluaran: Int) {
        if (totalPemasukan == 0 && totalPengeluaran == 0) {
            pieChart.clear()
            return
        }

        val entries = mutableListOf<PieEntry>()
        if (totalPemasukan > 0) entries.add(PieEntry(totalPemasukan.toFloat(), "Pemasukan"))
        if (totalPengeluaran > 0) entries.add(PieEntry(totalPengeluaran.toFloat(), "Pengeluaran"))

        val dataSet = PieDataSet(entries, "Ringkasan").apply {
            colors = listOf(
                Color.rgb(46, 204, 113),
                Color.rgb(255, 92, 138)
            )
            valueTextColor = Color.WHITE
            valueTextSize = 12f
            sliceSpace = 3f
        }

        pieChart.data = PieData(dataSet)
        pieChart.centerText = "Grafik"
        pieChart.setCenterTextColor(Color.rgb(155, 124, 255))
        pieChart.setCenterTextSize(15f)
        pieChart.invalidate()
    }

    private fun exportCsv() {
        val data = dbHelper.getSemuaTransaksi()
        if (data.isEmpty()) {
            Toast.makeText(this, "Belum ada transaksi untuk diexport", Toast.LENGTH_SHORT).show()
            return
        }

        val tanggalFile = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val namaFile = "laporan_keuangan_$tanggalFile.csv"
        val isiCsv = buatIsiCsv(data)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, namaFile)
                    put(MediaStore.Downloads.MIME_TYPE, "text/csv")
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                if (uri != null) {
                    contentResolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(isiCsv.toByteArray())
                    }
                    Toast.makeText(this, "CSV tersimpan di folder Download", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Gagal membuat file CSV", Toast.LENGTH_SHORT).show()
                }
            } else {
                val folder = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                if (folder != null && !folder.exists()) folder.mkdirs()
                val file = File(folder, namaFile)
                file.writeText(isiCsv)
                Toast.makeText(this, "CSV tersimpan: ${file.absolutePath}", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Export gagal: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun buatIsiCsv(data: List<Transaksi>): String {
        val builder = StringBuilder()
        builder.append("ID,Nama,Jumlah,Tipe,Tanggal\n")
        for (transaksi in data) {
            builder.append(transaksi.id).append(",")
            builder.append(escapeCsv(transaksi.nama)).append(",")
            builder.append(transaksi.jumlah).append(",")
            builder.append(transaksi.tipe).append(",")
            builder.append(transaksi.tanggal).append("\n")
        }
        return builder.toString()
    }

    private fun escapeCsv(text: String): String {
        val escaped = text.replace("\"", "\"\"")
        return "\"$escaped\""
    }

    private fun bukaEditTransaksi(transaksi: Transaksi) {
        val intent = Intent(this, TambahTransaksiActivity::class.java).apply {
            putExtra("mode", "edit")
            putExtra("id", transaksi.id)
            putExtra("nama", transaksi.nama)
            putExtra("jumlah", transaksi.jumlah)
            putExtra("tipe", transaksi.tipe)
        }
        startActivity(intent)
    }

    private fun formatRupiah(jumlah: Int): String {
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        return formatRupiah.format(jumlah).replace(",00", "")
    }

    private fun konfirmasiHapus(transaksi: Transaksi) {
        AlertDialog.Builder(this)
            .setTitle("Hapus transaksi?")
            .setMessage("Transaksi '${transaksi.nama}' akan dihapus permanen.")
            .setPositiveButton("Hapus") { _, _ ->
                dbHelper.hapusTransaksi(transaksi.id)
                tampilkanData()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
