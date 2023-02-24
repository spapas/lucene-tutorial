package gr.serafeim;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.TermVectorsReader;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.nio.file.Paths;
// https://lucene.apache.org/core/9_5_0/core/allclasses.html
public class Search {
    public static void main(String[] args) throws IOException, ParseException, InvalidTokenOffsetsException {
        FSDirectory luceneIndex = FSDirectory.open(Paths.get("lucene_index"));
        DirectoryReader reader = DirectoryReader.open(luceneIndex);
        IndexSearcher indexSearcher = new IndexSearcher(reader);
        //StandardAnalyzer analyzer = new StandardAnalyzer();
        EnglishAnalyzer analyzer = new EnglishAnalyzer();

        BytesRef normalized = analyzer.normalize("text", "Φραγκογιαννού");
        TokenStream ts = analyzer.tokenStream("text", "Φραγκογιαννού");

        //TermQuery q = new TermQuery(new Term("id", "the-idiot.txt"));
        //TermQuery q = new TermQuery(new Term("id", "fonissa.txt"));
        //TermQuery q = new TermQuery(new Term("text", "killers"));
        //TermQuery q = new TermQuery(new Term("text", "marmeladov"));
        //TermQuery q = new TermQuery(new Term("text", "φραγκογιαννου"));
        //TermQuery q = new TermQuery(new Term("text", normalized));
        //WildcardQuery q = new WildcardQuery(new Term("text", "Φραγκογιαννού"));
        //PhraseQuery q = new PhraseQuery("text", "φραγκογιαννού");
        //PhraseQuery q = new PhraseQuery("text", "Marmeladov");
        QueryParser qp = new QueryParser("text", analyzer);
        //Query q = qp.parse("φραγκογιαννού");
        Query q = qp.parse("killed");

        TopDocs hits = indexSearcher.search(q, 10);
        //ScoreDoc[] hits = docs.scoreDocs;
        System.out.println("Found " + hits.scoreDocs.length + " hits.");

        SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter();
        QueryScorer queryScorer = new QueryScorer(q);
        Highlighter highlighter = new Highlighter(htmlFormatter, queryScorer);
        highlighter.setTextFragmenter(new SimpleSpanFragmenter(queryScorer, 32));
        highlighter.setMaxDocCharsToAnalyze(Integer.MAX_VALUE);
        for (ScoreDoc hit : hits.scoreDocs) {
            int id = hit.doc;
            //System.out.println("XXX: " + hit +" "+ id);
            StoredFields storedFields = indexSearcher.storedFields();
            //Document d = storedFields.document(id);
            Document d = indexSearcher.doc(id);
            //System.out.println("XXX: " + d.get("id"));
            System.out.println("~~~" + d.get("id") + "\t" + d.get("size_s") +" " + d.get("accessed") + " " + hit.score);
            //TokenStream tokenStream = TokenSources.getAnyTokenStream(indexSearcher.getIndexReader(), id, "text", analyzer);

            String text = d.get("text");
            System.out.println(text.length());

            //TokenStream tokenStream = TokenSources.getTokenStream("text", null, text, analyzer,-1);

            String[] frag = highlighter.getBestFragments(analyzer.tokenStream("text", text), text,  5);
            for (int j = 0; j < frag.length; j++) {
                //if ((frag[j] != null) && (frag[j].getScore() > 0)) {

                    System.out.println("---" + (j+1) + ":" + frag[j]);
                //}
            }

        }
/*
        for (int i = 0; i < hits.scoreDocs.length; i++) {
            int id = hits.scoreDocs[i].doc;
            Document doc = indexSearcher.doc(id);
            String text = doc.get("text");
            TokenStream tokenStream = TokenSources.getAnyTokenStream(indexSearcher.getIndexReader(), id, "text", analyzer);
            TextFragment[] frag = highlighter.getBestTextFragments(tokenStream, text, false, 10);//highlighter.getBestFragments(tokenStream, text, 3, "...");
            for (int j = 0; j < frag.length; j++) {
                if ((frag[j] != null) && (frag[j].getScore() > 0)) {
                    System.out.println((frag[j].toString()));
                    System.out.println("---");
                }
            }

            //Term vector
            text = doc.get("tv");
            tokenStream = TokenSources.getAnyTokenStream(indexSearcher.getIndexReader(), hits.scoreDocs[i].doc, "tv", analyzer);
            frag = highlighter.getBestTextFragments(tokenStream, text, false, 10);
            for (int j = 0; j < frag.length; j++) {
                if ((frag[j] != null) && (frag[j].getScore() > 0)) {
                    System.out.println((frag[j].toString()));
                }
            }


            System.out.println("-------------");
        }
        */

        /*
        for(int i=0;i<hits.length;++i) {

            //int docId = hits[i].doc;

            //Document d = indexSearcher.doc(docId);
            //System.out.println((i + 1) + ". " + d.get("id") + "\t" + d.get("size_s") +" " + d.get("accessed"));
        }

         */

    }
}
