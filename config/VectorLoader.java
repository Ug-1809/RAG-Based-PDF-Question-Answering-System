package com.dailycodebuffer.spring_ai.config;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.observation.conventions.VectorStoreProvider;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.File;
import java.util.List;

//@Configuration
public class VectorLoader {


    @Value("classpath:/IndianConstitution.pdf")
    private Resource pdfResource;

    // now create a vector store
    @Bean
    SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel){
        //SimpleVectorStore vectorStore = new SimpleVectorStore(embeddingModel);
        //embedding model help us to get the embedding data through openAPI
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        File vectoreStoreFile = new File("C:/Users/uday.garg/Downloads/spring-ai/spring-ai/src/main/resources/vector_store.json");
        if(vectoreStoreFile.exists())
        {
            System.out.println("Loaded vector store file! ");
            vectorStore.load(vectoreStoreFile);
        }
        else{
            System.out.println("Creating Vector Store!");
            //how i want to read that file for that PdfDocumentReaderConfig
            //In newer Spring AI versions, PDF support comes from a separate dependency.
            PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder()
                                                .withPagesPerDocument(1)
                                                 .build();// want to read 1 page at a time
            PagePdfDocumentReader reader = new PagePdfDocumentReader(pdfResource,config);
            //split date in tokenised format
            var textSplitter = new TokenTextSplitter();// in token i want to read
            List<Document> docs =textSplitter.apply(reader.get());
            vectorStore.add(docs);
            vectorStore.save(vectoreStoreFile);
            System.out.println("Vector store created successfully");
        }
        return vectorStore;
        // after running vectorstore.json will come in resources
    }

 

}
