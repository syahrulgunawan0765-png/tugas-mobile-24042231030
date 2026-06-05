package com.example.aplikasikeuangan

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "keuangan.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "transaksi"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_NAME (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nama TEXT NOT NULL,
                jumlah INTEGER NOT NULL,
                tipe TEXT NOT NULL,
                tanggal TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun tambahTransaksi(nama: String, jumlah: Int, tipe: String): Long {
        val db = writableDatabase
        val tanggalSekarang = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val values = ContentValues().apply {
            put("nama", nama)
            put("jumlah", jumlah)
            put("tipe", tipe)
            put("tanggal", tanggalSekarang)
        }
        val result = db.insert(TABLE_NAME, null, values)
        db.close()
        return result
    }

    fun updateTransaksi(id: Int, nama: String, jumlah: Int, tipe: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nama", nama)
            put("jumlah", jumlah)
            put("tipe", tipe)
        }
        val result = db.update(TABLE_NAME, values, "id=?", arrayOf(id.toString()))
        db.close()
        return result
    }

    fun getSemuaTransaksi(): MutableList<Transaksi> {
        val list = mutableListOf<Transaksi>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY id DESC", null)

        while (cursor.moveToNext()) {
            list.add(
                Transaksi(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    nama = cursor.getString(cursor.getColumnIndexOrThrow("nama")),
                    jumlah = cursor.getInt(cursor.getColumnIndexOrThrow("jumlah")),
                    tipe = cursor.getString(cursor.getColumnIndexOrThrow("tipe")),
                    tanggal = cursor.getString(cursor.getColumnIndexOrThrow("tanggal"))
                )
            )
        }

        cursor.close()
        db.close()
        return list
    }

    fun hapusTransaksi(id: Int): Int {
        val db = writableDatabase
        val result = db.delete(TABLE_NAME, "id=?", arrayOf(id.toString()))
        db.close()
        return result
    }
}
