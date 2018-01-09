package org.bookmarks.report.bean;

import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfPage;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.Document;


public class Rotate extends PdfPageEventHelper {
    protected PdfNumber rotation = PdfPage.PORTRAIT;
    
    public void setRotation(PdfNumber rotation) {
        this.rotation = rotation;
    }
    
    public void onEndPage(PdfWriter writer, Document document) {
        writer.addPageDictEntry(PdfName.ROTATE, rotation);
    }
}
