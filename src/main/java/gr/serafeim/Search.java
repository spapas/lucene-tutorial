package gr.serafeim;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.nio.file.Paths;

public class Search {
    public static void main(String[] args) throws IOException, ParseException {
        FSDirectory luceneIndex = FSDirectory.open(Paths.get("lucene_index"));
        DirectoryReader reader = DirectoryReader.open(luceneIndex);
        IndexSearcher indexSearcher = new IndexSearcher(reader);
        StandardAnalyzer analyzer = new StandardAnalyzer();
        BytesRef normalized = analyzer.normalize("text", "Φραγκογιαννού");
        TokenStream ts = analyzer.tokenStream("text", "Φραγκογιαννού");



        //TermQuery q = new TermQuery(new Term("id", "the-idiot.txt"));
        //TermQuery q = new TermQuery(new Term("id", "fonissa.txt"));
        //TermQuery q = new TermQuery(new Term("text", "marmeladov"));
        //TermQuery q = new TermQuery(new Term("text", "marmeladov"));
        //TermQuery q = new TermQuery(new Term("text", "φραγκογιαννου"));
        //TermQuery q = new TermQuery(new Term("text", normalized));
        //WildcardQuery q = new WildcardQuery(new Term("text", "Φραγκογιαννού"));
        //PhraseQuery q = new PhraseQuery("text", "φραγκογιαννού");
        //PhraseQuery q = new PhraseQuery("text", "Marmeladov");
        QueryParser qp = new QueryParser("text", analyzer);
        Query q = qp.parse("φραγκογιαννού");

        TopDocs docs = indexSearcher.search(q, 10);
        ScoreDoc[] hits = docs.scoreDocs;
        System.out.println("Found " + hits.length + " hits.");
        for(int i=0;i<hits.length;++i) {
            int docId = hits[i].doc;
            Document d = indexSearcher.doc(docId);
            System.out.println((i + 1) + ". " + d.get("id") + "\t" + d.get("size_s") +" " + d.get("accessed"));
        }

    }
}
