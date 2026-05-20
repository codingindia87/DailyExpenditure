package com.codingindia.dailyexpenditure.core.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color.GRAY
import android.graphics.Color.WHITE
import android.graphics.Color.parseColor
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import com.codingindia.dailyexpenditure.R
import com.codingindia.dailyexpenditure.data.local.entity.Expense
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun generateExpensePdf(context: Context, expenseList: List<Expense>, fileName: String): String {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()

    var pageNumber = 1
    var currentPage = pdfDocument.startPage(pageInfo)
    var canvas = currentPage.canvas

    val primaryColor = parseColor("#1E3A8A")
    val accentColor = parseColor("#0D9488")
    val textColor = parseColor("#1F2937")
    val lightGray = parseColor("#F3F4F6")
    val dividerColor = parseColor("#E5E7EB")
    val chartBarColor = parseColor("#3B82F6")

    val paint = Paint().apply { isAntiAlias = true }
    val textPaint = Paint().apply {
        color = textColor
        textSize = 12f
        isAntiAlias = true
    }

    fun drawPageHeader(pageCanvas: Canvas, pNum: Int) {

        try {
            val drawable = ContextCompat.getDrawable(context, R.drawable.budget)
            val rawBitmap = if (drawable != null) {
                val bitmap = Bitmap.createBitmap(
                    drawable.intrinsicWidth.coerceAtLeast(1),
                    drawable.intrinsicHeight.coerceAtLeast(1),
                    Bitmap.Config.ARGB_8888
                )
                val iconCanvas = Canvas(bitmap)
                drawable.setBounds(0, 0, iconCanvas.width, iconCanvas.height)
                drawable.draw(iconCanvas)
                bitmap
            } else {
                BitmapFactory.decodeResource(context.resources, R.drawable.budget)
            }

            if (rawBitmap != null) {
                val watermarkSize = 300
                val scaledWatermark =
                    Bitmap.createScaledBitmap(rawBitmap, watermarkSize, watermarkSize, true)

                val watermarkX = (595f - watermarkSize) / 2f
                val watermarkY = (842f - watermarkSize) / 2f

                val watermarkPaint = Paint().apply {
                    isAntiAlias = true
                    alpha = 25
                }

                pageCanvas.drawBitmap(scaledWatermark, watermarkX, watermarkY, watermarkPaint)


                val scaledIcon = Bitmap.createScaledBitmap(rawBitmap, 40, 40, true)
                pageCanvas.drawBitmap(scaledIcon, 40f, 40f, paint)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        paint.apply {
            color = primaryColor
            textSize = 20f
            isFakeBoldText = true
        }
        pageCanvas.drawText("Daily Expenditure", 92f, 40f + 28f, paint)

        paint.apply {
            color = GRAY
            textSize = 10f
            isFakeBoldText = false
        }
        val currentDate =
            SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
        pageCanvas.drawText("Generated: $currentDate  |  Page: $pNum", 360f, 40f + 25f, paint)

        paint.color = dividerColor
        pageCanvas.drawLine(40f, 40f + 65f, 555f, 40f + 65f, paint)

        paint.color = primaryColor
        val hRect = RectF(40f, 40f + 85f, 555f, 40f + 117f)
        pageCanvas.drawRoundRect(hRect, 8f, 8f, paint)

        paint.apply {
            color = WHITE
            textSize = 11f
            isFakeBoldText = true
        }
        pageCanvas.drawText("DESCRIPTION", 55f, 40f + 105f, paint)
        pageCanvas.drawText("CATEGORY", 280f, 40f + 105f, paint)
        pageCanvas.drawText("AMOUNT", 475f, 40f + 105f, paint)
    }

    drawPageHeader(canvas, pageNumber)
    var yPosition = 40f + 117f

    var totalAmount = 0.0
    var isEvenRow = false
    val maxDescWidth = 210f

    for (expense in expenseList) {
        val words = expense.description.split(" ")
        val descLines = mutableListOf<String>()
        var currentLine = ""

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            val textWidth = textPaint.measureText(testLine)

            if (textWidth <= maxDescWidth) {
                currentLine = testLine
            } else {
                if (currentLine.isNotEmpty()) descLines.add(currentLine)
                currentLine = word
            }
        }
        if (currentLine.isNotEmpty()) descLines.add(currentLine)

        val lineHeight = 20f
        val rowHeight = (descLines.size * lineHeight) + 10f

        if (yPosition + rowHeight > 760f) {
            pdfDocument.finishPage(currentPage)
            pageNumber++
            currentPage = pdfDocument.startPage(pageInfo)
            canvas = currentPage.canvas
            drawPageHeader(canvas, pageNumber)
            yPosition = 40f + 117f
        }

        yPosition += 6f

        if (isEvenRow) {
            paint.color = lightGray
            val rowRect = RectF(40f, yPosition, 555f, yPosition + rowHeight)
            canvas.drawRoundRect(rowRect, 6f, 6f, paint)
        }

        var currentY = yPosition + 18f
        for (line in descLines) {
            canvas.drawText(line, 55f, currentY, textPaint)
            currentY += lineHeight
        }

        canvas.drawText(expense.category, 280f, yPosition + 18f, textPaint)
        canvas.drawText(
            "₹ ${String.format(Locale.getDefault(), "%.2f", expense.amount)}",
            475f,
            yPosition + 18f,
            textPaint
        )

        totalAmount += expense.amount
        yPosition += rowHeight
        isEvenRow = !isEvenRow
    }

    if (yPosition + 60f > 800f) {
        pdfDocument.finishPage(currentPage)
        pageNumber++
        currentPage = pdfDocument.startPage(pageInfo)
        canvas = currentPage.canvas
        drawPageHeader(canvas, pageNumber)
        yPosition = 40f + 117f
    }

    yPosition += 20f

    paint.color = accentColor
    val totalCardRect = RectF(340f, yPosition, 555f, yPosition + 40f)
    canvas.drawRoundRect(totalCardRect, 10f, 10f, paint)

    paint.apply {
        color = WHITE
        textSize = 13f
        isFakeBoldText = true
    }
    canvas.drawText("Total Expenses:", 360f, yPosition + 25f, paint)
    val totalStr = "₹ ${String.format(Locale.getDefault(), "%.2f", totalAmount)}"
    canvas.drawText(totalStr, 475f, yPosition + 25f, paint)

    yPosition += 60f

    val categoryMap =
        expenseList.groupBy { it.category }.mapValues { entry -> entry.value.sumOf { it.amount } }

    if (categoryMap.isNotEmpty()) {
        val estimatedChartHeight = 40f + (categoryMap.size * 30f)
        if (yPosition + estimatedChartHeight > 800f) {
            pdfDocument.finishPage(currentPage)
            pageNumber++
            currentPage = pdfDocument.startPage(pageInfo)
            canvas = currentPage.canvas
            drawPageHeader(canvas, pageNumber)
            yPosition = 40f + 117f + 20f
        }

        paint.apply {
            color = primaryColor
            textSize = 14f
            isFakeBoldText = true
        }
        canvas.drawText("Category-wise Analytics", 40f, yPosition, paint)
        yPosition += 15f

        val maxCategoryAmount = categoryMap.values.maxOrNull() ?: 1.0
        val maxBarWidth = 250f

        categoryMap.forEach { (category, amount) ->
            yPosition += 25f

            paint.apply {
                color = textColor
                textSize = 11f
                isFakeBoldText = false
            }
            canvas.drawText(category, 45f, yPosition + 12f, paint)

            val barWidth = ((amount / maxCategoryAmount) * maxBarWidth).toFloat().coerceAtLeast(10f)

            paint.color = lightGray
            canvas.drawRoundRect(
                RectF(180f, yPosition, 180f + maxBarWidth, yPosition + 14f),
                4f,
                4f,
                paint
            )

            paint.color = chartBarColor
            canvas.drawRoundRect(
                RectF(180f, yPosition, 180f + barWidth, yPosition + 14f),
                4f,
                4f,
                paint
            )

            paint.apply {
                color = textColor
                textSize = 10f
                isFakeBoldText = true
            }
            val amountStr = "₹${String.format(Locale.getDefault(), "%.0f", amount)}"
            canvas.drawText(amountStr, 185f + barWidth, yPosition + 11f, paint)
        }
    }

    pdfDocument.finishPage(currentPage)

    val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    val timestamp = sdf.format(Date())
    val fullFileName = "${fileName}_$timestamp.pdf"
    val relativePath = "${Environment.DIRECTORY_DOWNLOADS}${File.separator}Daily Expenditure"

    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fullFileName)
        put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
        }
    }

    val contentResolver = context.contentResolver
    val collectionUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Downloads.EXTERNAL_CONTENT_URI
    } else {
        Uri.fromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
    }

    var resultPath = ""

    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val uri = contentResolver.insert(collectionUri, contentValues)
            if (uri != null) {
                contentResolver.openOutputStream(uri).use { outputStream ->
                    if (outputStream != null) {
                        pdfDocument.writeTo(outputStream)
                        resultPath = "Download/Daily Expenditure/$fullFileName"
                    }
                }
            }
        } else {
            val oldFolder = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "Daily Expenditure"
            )
            if (!oldFolder.exists()) oldFolder.mkdirs()
            val oldFile = File(oldFolder, fullFileName)
            FileOutputStream(oldFile).use { outputStream ->
                pdfDocument.writeTo(outputStream)
                resultPath = oldFile.absolutePath
            }
        }
        pdfDocument.close()
    } catch (e: Exception) {
        e.printStackTrace()
        try {
            pdfDocument.close()
        } catch (ex: Exception) {
        }
        resultPath = ""
    }

    return resultPath
}