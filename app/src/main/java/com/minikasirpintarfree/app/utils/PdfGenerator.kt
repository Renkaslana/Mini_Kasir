package com.minikasirpintarfree.app.utils

import android.content.Context
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.properties.TextAlignment
import com.minikasirpintarfree.app.data.model.Transaksi
import com.minikasirpintarfree.app.data.model.TransaksiItem
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

object PdfGenerator {
    fun generateReceipt(context: Context, transaksi: Transaksi, items: List<TransaksiItem>): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val fileName = "Struk_${dateFormat.format(transaksi.tanggal)}.pdf"
        val file = File(context.getExternalFilesDir(null), fileName)
        
        val writer = PdfWriter(file)
        val pdfDocument = PdfDocument(writer)
        val document = Document(pdfDocument)
        
        // Header
        val headerParagraph = Paragraph("MINI KASIR")
            .setTextAlignment(TextAlignment.CENTER)
            .setBold()
            .setFontSize(18f)
        document.add(headerParagraph)
        
        val titleParagraph = Paragraph("Struk Transaksi")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(12f)
        document.add(titleParagraph)
        
        val dateParagraph = Paragraph("Tanggal: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(transaksi.tanggal)}")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(10f)
        document.add(dateParagraph)
        
        val separator1 = Paragraph("--------------------------------")
            .setTextAlignment(TextAlignment.CENTER)
        document.add(separator1)
        
        // Items
        items.forEach { item ->
            document.add(Paragraph(item.namaProduk).setBold())
            document.add(Paragraph("${item.quantity}x ${formatCurrency(item.harga)} = ${formatCurrency(item.subtotal)}"))
        }
        
        document.add(Paragraph("--------------------------------")
            .setTextAlignment(TextAlignment.CENTER))
        
        // Summary
        document.add(Paragraph("Total: ${formatCurrency(transaksi.totalHarga)}")
            .setTextAlignment(TextAlignment.RIGHT))
        document.add(Paragraph("Bayar: ${formatCurrency(transaksi.uangDiterima)}")
            .setTextAlignment(TextAlignment.RIGHT))
        val kembalianParagraph = Paragraph("Kembali: ${formatCurrency(transaksi.kembalian)}")
            .setTextAlignment(TextAlignment.RIGHT)
            .setBold()
        document.add(kembalianParagraph)
        
        val thankYouParagraph = Paragraph("Terima Kasih")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(12f)
            .setBold()
        document.add(thankYouParagraph)
        
        document.close()
        
        return file.absolutePath
    }
    
    private fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return format.format(amount)
    }
}

