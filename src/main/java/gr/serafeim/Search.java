package gr.serafeim;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.TermVectorsReader;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder;
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
        Query qq = qp.parse("text:killed");

        BooleanQuery.Builder bqb = new BooleanQuery.Builder();
        bqb.add(qq, BooleanClause.Occur.MUST);
        //WildcardQuery wq = new WildcardQuery(new Term("accessed", "2023022*"));
        //bqb.add(LongPoint.newRangeQuery("size", 262016-10, 262016+20), BooleanClause.Occur.FILTER);
        //bqb.add(wq, BooleanClause.Occur.FILTER);

        BooleanQuery q = bqb.build();

        //TopDocs hits = indexSearcher.search(q, 900);
        //ScoreDoc[] hits = docs.scoreDocs;
        TopScoreDocCollector collector = TopScoreDocCollector.create(999 , 999 );
        indexSearcher.search(q, collector);
        System.out.println("Found " + collector.getTotalHits() + " hits.");

        SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter("**", "**");
        QueryScorer queryScorer = new QueryScorer(q);
        Highlighter highlighter = new Highlighter(htmlFormatter, queryScorer);
        highlighter.setTextFragmenter(new SimpleSpanFragmenter(queryScorer, 64));
        highlighter.setMaxDocCharsToAnalyze(Integer.MAX_VALUE);
        int pageNumber = 20;
        int pageSize = 10;
        TopDocs topDocs = collector.topDocs(pageNumber * pageSize, pageSize);
        for (ScoreDoc hit : topDocs.scoreDocs) {
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
                    String f = frag[j];
                    f = f.replace("\n", " ").replace("\r", "");
                    System.out.println("---" + (j+1) + ":" + f);
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
