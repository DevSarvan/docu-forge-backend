
package com.mycompany.pdfchat.controller;

import com.mycompany.pdfchat.Dto.ChatResponseDto;
import com.mycompany.pdfchat.Dto.UploadResponseDto;
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
    public ResponseEntity<UploadResponseDto> uploadPdf(@RequestParam("file") MultipartFile file) {
        UploadResponseDto responseDto= new UploadResponseDto();
        try {
            fileUploadService.uploadFile(file);
            String summary = pdfProcessingService.summariesPdf(file);
            responseDto.setSummary(summary);
            responseDto.setMessage("File uploading has been done successfully");
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            responseDto.setMessage("Error occurred while uploading the file");
            return ResponseEntity.status(500).body(responseDto);
        }
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponseDto> askQuery(@RequestParam("query") String query) {
        ChatResponseDto responseDto=new ChatResponseDto();
        try {
            String answer = pdfProcessingService.getStringResponse(query);
            responseDto.setReply(answer);
            responseDto.setMessage("Prompting has been done successfully");
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            responseDto.setMessage("Error occurred while prompting");
            return ResponseEntity.status(500).body(responseDto);
        }
    }
}
