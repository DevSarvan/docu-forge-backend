
package com.mycompany.pdfchat.controller;

import com.mycompany.pdfchat.service.FileUploadService;
import com.mycompany.pdfchat.service.PdfProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    @Autowired
    private PdfProcessingService pdfProcessingService;

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file) {
        try {
            fileUploadService.uploadFile(file);
            String summary = pdfProcessingService.summariesPdf(file);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing PDF: " + e.getMessage());
        }
    }

    @PostMapping("/chat")
    public ResponseEntity<String> askQuery(@RequestParam("query") String query) {
        try {
            String answer = pdfProcessingService.processQuery(query);
            return ResponseEntity.ok(answer);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing query: " + e.getMessage());
        }
    }
}
