
package com.mycompany.pdfchat.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.vertexai.VertexAiEmbeddingModel;
import dev.langchain4j.model.vertexai.VertexAiGeminiChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
public class PdfProcessingService {

    interface LlmExpert {
        String ask(String question);
    }

    InMemoryEmbeddingStore<TextSegment> embeddingStore =null;

    public String summariesPdf(MultipartFile file) throws IOException {

        ApachePdfBoxDocumentParser pdfParser = new ApachePdfBoxDocumentParser();
        Document document = pdfParser.parse(file.getInputStream());

        VertexAiEmbeddingModel embeddingModel = VertexAiEmbeddingModel.builder()
                .endpoint(System.getenv("LOCATION") + "-aiplatform.googleapis.com:443")
                .project(System.getenv("PROJECT_ID"))
                .location(System.getenv("LOCATION"))
                .publisher("google")
                .modelName("textembedding-gecko@003")
                .maxRetries(3)
                .build();

        embeddingStore =
                new InMemoryEmbeddingStore<>();

        EmbeddingStoreIngestor storeIngestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(500, 100))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();

        System.out.println("Chunking and embedding PDF...");
        storeIngestor.ingest(document);
        System.out.println("Ready!\n");
        String ques= "Summarize the content of the PDF file uploaded?";
        return getStringResponse(embeddingStore, embeddingModel,ques);
    }

    private static String getStringResponse(InMemoryEmbeddingStore<TextSegment> embeddingStore, VertexAiEmbeddingModel embeddingModel, String ques) {
        ChatLanguageModel model = VertexAiGeminiChatModel.builder()
                .project(System.getenv("PROJECT_ID"))
                .location(System.getenv("LOCATION"))
                .modelName("gemini-1.5-flash-001")
                .maxOutputTokens(1000)
                .build();

        EmbeddingStoreContentRetriever retriever =
                new EmbeddingStoreContentRetriever(embeddingStore, embeddingModel);
        LlmExpert expert = AiServices.builder(LlmExpert.class)
                .chatLanguageModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(retriever)
                .build();

        String response = expert.ask(ques);
        System.out.printf("%n=== %s === %n%n %s %n%n", ques, response);
        return response;
    }

    public String processQuery(String query) throws IOException {

        VertexAiEmbeddingModel embeddingModel = VertexAiEmbeddingModel.builder()
                .endpoint(System.getenv("LOCATION") + "-aiplatform.googleapis.com:443")
                .project(System.getenv("PROJECT_ID"))
                .location(System.getenv("LOCATION"))
                .publisher("google")
                .modelName("textembedding-gecko@003")
                .maxRetries(3)
                .build();

        return getStringResponse(embeddingStore, embeddingModel,query);

    }


}
