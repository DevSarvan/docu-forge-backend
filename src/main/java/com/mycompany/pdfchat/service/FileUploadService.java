package com.mycompany.pdfchat.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileUploadService {

    public void uploadFile(MultipartFile file) {
        // Replace with your GCP bucket name, parent folder, and PDF file path
        String bucketName = "docuforge-input";
        String parentFolder = "pdf_files"; // This can be nested folders like "folder1/folder2"

        try {
            uploadFileToBucket(bucketName, parentFolder, file);
        } catch (IOException e) {
            System.err.println("Failed to upload PDF file: " + e.getMessage());
        }
    }

    /**
     * Uploads a PDF file to a specified GCP bucket and parent folder.
     *
     * @param bucketName   The name of the GCP bucket.
     * @param parentFolder The parent folder within the bucket where the file should be uploaded.
     * @param file     The file to be uploaded.
     * @throws IOException If an error occurs while reading the file or uploading to the bucket.
     */
    public void uploadFileToBucket(String bucketName, String parentFolder, MultipartFile file) throws IOException {
        // Initialize Google Cloud Storage client
        Storage storage = StorageOptions.getDefaultInstance().getService();

        // Extract file name from the path
        String fileName = file.getOriginalFilename();

        // Define the blob's name in the bucket (includes the parent folder)
        String blobName = parentFolder + "/" + fileName;

        // Read the PDF file into a byte array
        byte[] fileBytes = file.getBytes();

        // Create a BlobId
        BlobId blobId = BlobId.of(bucketName, blobName);

        // Create a BlobInfo object with the blob's metadata
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("application/pdf").build();

        // Upload the file to the bucket
        storage.create(blobInfo, fileBytes);

        System.out.println("PDF file uploaded successfully to " + blobName);
    }
}
