package gr.serafeim;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

public class Search {
    public static void main(String[] args) throws IOException {
        FSDirectory luceneIndex = FSDirectory.open(Paths.get("lucene_index"));
        DirectoryReader reader = DirectoryReader.open(luceneIndex);
        IndexSearcher indexSearcher = new IndexSearcher(reader);
        //new org.apache.lucene.search.AutomatonQuery()

        //TermQuery q = new TermQuery(new Term("id", "crime-and-punishment.txt"));
        //WildcardQuery q = new WildcardQuery(new Term("text", "raskol*"));
        PhraseQuery q = new PhraseQuery("text", "marmeladov");
        TopDocs docs = indexSearcher.search(q, 10);
        ScoreDoc[] hits = docs.scoreDocs;
        System.out.println("Found " + hits.length + " hits.");
        for(int i=0;i<hits.length;++i) {
            int docId = hits[i].doc;
            Document d = indexSearcher.doc(docId);
            System.out.println((i + 1) + ". " + d.get("id") + "\t" + d.get("title"));
        }

    }
}
