package com.aaryan7.dastakmobile7.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;

import com.aaryan7.dastakmobile7.models.Bill;
import com.aaryan7.dastakmobile7.models.BillItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class for generating PDF bills
 */
public class PDFGenerator {
    private Context context;
    private static final int PAGE_WIDTH = 595; // A4 width in points
    private static final int PAGE_HEIGHT = 842; // A4 height in points
    private static final int MARGIN = 50;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());

    public PDFGenerator(Context context) {
        this.context = context;
    }

    /**
     * Generate PDF bill
     * @param bill Bill to generate PDF for
     * @return Generated PDF file
     */
    public File generateBillPdf(Bill bill) {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // Create paints
        Paint titlePaint = new Paint();
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextSize(18);
        titlePaint.setFakeBoldText(true);

        Paint headerPaint = new Paint();
        headerPaint.setColor(Color.BLACK);
        headerPaint.setTextSize(14);
        headerPaint.setFakeBoldText(true);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(12);

        Paint linePaint = new Paint();
        linePaint.setColor(Color.GRAY);
        linePaint.setStrokeWidth(1);

        // Draw header
        int y = MARGIN;
        canvas.drawText("Dastak Mobile 7", MARGIN, y, titlePaint);
        y += 30;
        canvas.drawText("BILL", MARGIN, y, titlePaint);
        y += 30;

        // Draw date and time
        String dateTime = "Date: " + DATE_FORMAT.format(bill.getDate());
        canvas.drawText(dateTime, MARGIN, y, textPaint);
        y += 30;

        // Draw table header
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, linePaint);
        y += 15;
        canvas.drawText("Item", MARGIN, y, headerPaint);
        canvas.drawText("Qty", MARGIN + 200, y, headerPaint);
        canvas.drawText("Price (₹)", MARGIN + 250, y, headerPaint);
        canvas.drawText("Subtotal (₹)", MARGIN + 350, y, headerPaint);
        y += 15;
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, linePaint);
        y += 20;

        // Draw items
        for (BillItem item : bill.getItems()) {
            canvas.drawText(item.getProductName(), MARGIN, y, textPaint);
            canvas.drawText(String.valueOf(item.getQuantity()), MARGIN + 200, y, textPaint);
            canvas.drawText(String.format(Locale.getDefault(), "%.2f", item.getPrice()), MARGIN + 250, y, textPaint);
            canvas.drawText(String.format(Locale.getDefault(), "%.2f", item.getSubtotal()), MARGIN + 350, y, textPaint);
            y += 20;
        }

        // Draw summary
        y += 10;
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, linePaint);
        y += 20;
        canvas.drawText("Subtotal:", MARGIN + 250, y, headerPaint);
        canvas.drawText(String.format(Locale.getDefault(), "₹%.2f", bill.getTotal()), MARGIN + 350, y, headerPaint);
        y += 20;
        canvas.drawText("Discount:", MARGIN + 250, y, headerPaint);
        canvas.drawText(String.format(Locale.getDefault(), "₹%.2f", bill.getDiscount()), MARGIN + 350, y, headerPaint);
        y += 20;
        canvas.drawText("Final Amount:", MARGIN + 250, y, headerPaint);
        canvas.drawText(String.format(Locale.getDefault(), "₹%.2f", bill.getFinalAmount()), MARGIN + 350, y, headerPaint);
        y += 30;

        // Draw footer
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, linePaint);
        y += 20;
        canvas.drawText("Thank you for shopping with us!", MARGIN, y, textPaint);
        y += 20;
        canvas.drawText("Contact: Aaryan Parmar (owner)", MARGIN, y, textPaint);
        y += 20;
        canvas.drawText("Email: myuse077@gmail.com", MARGIN, y, textPaint);

        document.finishPage(page);

        // Save the document
        File pdfFile = createPdfFile();
        try {
            document.writeTo(new FileOutputStream(pdfFile));
            return pdfFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            document.close();
        }
    }

    /**
     * Create PDF file
     * @return PDF file
     */
    private File createPdfFile() {
        File dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "DastakMobile7");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = "Bill_" + FILE_DATE_FORMAT.format(new Date()) + ".pdf";
        return new File(dir, fileName);
    }
}
