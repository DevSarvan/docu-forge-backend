
package com.mycompany.pdfchat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class PdfChatApplication {
    public static void main(String[] args) {
        SpringApplication.run(PdfChatApplication.class, args);
    }
}
