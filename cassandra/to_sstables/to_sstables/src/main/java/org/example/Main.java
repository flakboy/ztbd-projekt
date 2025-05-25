package org.example;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.cassandra.io.sstable.CQLSSTableWriter;

import java.io.*;
//import java.io.File;
//import org.apache.cassandra.io.util.*;
import org.apache.cassandra.io.sstable.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        String schema = "CREATE TABLE yelp.reviews (" +
                "review_id text," +
                "user_id text," +
                "business_id text," +
                "review_text text," +
                "date_reviewed timestamp," +
                "PRIMARY KEY ((business_id), review_id, date_reviewed)" +
        ")";

        String insertStmt = "INSERT INTO yelp.reviews (review_id, user_id, business_id, review_text, date_reviewed) VALUES (?, ?, ?, ?, ?)";

        String dataPath = System.getenv().get("DATA_DIR");
//        String outputDirPath = dataPath + File.separator + "sstable";
        String outputDirPath = "/home/kali/sstable";
        org.apache.cassandra.io.util.File outputDir = new org.apache.cassandra.io.util.File(outputDirPath);

        String inputPath = dataPath + File.separator + "csv" + File.separator + "reviews.csv";
        System.out.println(inputPath);

        int i = 0;
        int milestoneInterval = 10_000;
        try (
            CQLSSTableWriter writer = CQLSSTableWriter.builder()
                .inDirectory(outputDir)
                .forTable(schema)
                .withBufferSizeInMB(256)
                .using(insertStmt)
                .build();

            CSVReader reader = new CSVReaderBuilder(new FileReader(inputPath))
                    .withCSVParser(new CSVParserBuilder()
                            .withSeparator('|')
                            .build()
                    ).build()
        ) {
            //skip first line since it contains header
            String[] row;
            reader.readNext();
            while ((row = reader.readNext()) != null) {
                LocalDateTime review_date = LocalDateTime.parse(row[4]);
                writer.addRow(row[0], row[1], row[2], row[3], new Date(review_date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
                i++;
                if (i % milestoneInterval == 0) {
                    System.out.println(String.format("Parsed %d rows.", i));
                }
            }
        } catch (IOException e) {
            System.out.println(e);
//            throw new RuntimeException(e);
        } catch (CsvValidationException e) {
            System.out.println(e);
//            throw new RuntimeException(e);
        }
    }
}