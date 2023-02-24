package gr.serafeim;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

public class Index {
    public static void main(String[] args) throws IOException {
        String directory = "books";
        FSDirectory luceneIndex = FSDirectory.open(Paths.get("lucene_index"));
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(luceneIndex, indexWriterConfig);

        Files.walk(Paths.get(directory)).forEach(filePath -> {
            if (Files.isRegularFile(filePath)) {
                String filename = filePath.getFileName().toString();

                try {
                    BasicFileAttributes attrs = Files.readAttributes(filePath, BasicFileAttributes.class);
                    long size = attrs.size();
                    FileTime fileTime = attrs.lastAccessTime();
                    System.out.println(filename +" " + size + " " + fileTime);

                    String content = Files.readString(filePath);
                    Document doc = new Document();
                    doc.add(new StringField("id", filename, Field.Store.YES ));
                    doc.add(new TextField("text", content, Field.Store.YES ));
                    String accessTime = DateTools.timeToString(fileTime.toMillis(), DateTools.Resolution.MINUTE);
                    doc.add(new StringField("accessed", accessTime, Field.Store.YES ));
                    doc.add(new LongPoint("accessedPoint", fileTime.toMillis()));
                    doc.add(new LongField("size", size));
                    doc.add(new StoredField("size_s", size));

                    Term idTerm = new Term("id", filename);

                    indexWriter.updateDocument(idTerm, doc);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        indexWriter.close();
    }
}