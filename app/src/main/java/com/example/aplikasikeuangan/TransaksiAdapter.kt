package com.example.aplikasikeuangan

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class TransaksiAdapter(
    private val list: List<Transaksi>,
    private val onEditClick: (Transaksi) -> Unit,
    private val onDeleteClick: (Transaksi) -> Unit
) : RecyclerView.Adapter<TransaksiAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.tvNama)
        val tvJumlah: TextView = itemView.findViewById(R.id.tvJumlah)
        val tvTanggal: TextView = itemView.findViewById(R.id.tvTanggal)
        val tvTipe: TextView = itemView.findViewById(R.id.tvTipe)
        val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        val btnHapus: Button = itemView.findViewById(R.id.btnHapus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaksi, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val jumlahText = formatRupiah(item.jumlah)

        holder.tvNama.text = item.nama
        holder.tvTanggal.text = formatTanggal(item.tanggal)

        if (item.tipe == "pemasukan") {
            holder.tvTipe.text = "Pemasukan"
            holder.tvJumlah.text = "+ $jumlahText"
            holder.tvJumlah.setTextColor(Color.rgb(46, 204, 113))
        } else {
            holder.tvTipe.text = "Pengeluaran"
            holder.tvJumlah.text = "- $jumlahText"
            holder.tvJumlah.setTextColor(Color.rgb(255, 92, 138))
        }

        holder.btnEdit.setOnClickListener {
            onEditClick(item)
        }

        holder.btnHapus.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount(): Int = list.size

    private fun formatRupiah(jumlah: Int): String {
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        return formatRupiah.format(jumlah).replace(",00", "")
    }

    private fun formatTanggal(tanggal: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("in", "ID"))
            val date = inputFormat.parse(tanggal)
            if (date != null) outputFormat.format(date) else tanggal
        } catch (e: Exception) {
            tanggal
        }
    }
}
