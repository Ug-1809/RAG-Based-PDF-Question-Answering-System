package com.dailycodebuffer.spring_ai.config;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
public class PGVectorLoader {

    @Value("classpath:/IndianConstitution.pdf")
    private Resource pdfResource;

    private final VectorStore vectorStore;
    private final JdbcClient jdbcClient;

    public PGVectorLoader(VectorStore vectorStore, JdbcClient jdbcClient) {
        this.vectorStore = vectorStore;
        this.jdbcClient = jdbcClient;
    }

    @PostConstruct
    public void init(){

        Integer count = jdbcClient.sql("select COUNT(*) FROM vector_store")
                .query(Integer.class)
                .single();

        System.out.println("Num of Documents in the PG Vector Store" + count);
        if(count == 0){
            System.out.println("Initialising PG Vector Store Load");
            PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder()
                    .withPagesPerDocument(1)
                    .build();// want to read 1 page at a time
            PagePdfDocumentReader reader = new PagePdfDocumentReader(pdfResource,config);
            //split date in tokenised format
            var textSplitter = new TokenTextSplitter();// in token i want to read
            // here we will add in db rather than in vector space
            vectorStore.accept(textSplitter.apply(reader.get()));
            System.out.println("Application is started and ready to serve");

        }

    }

}
