/*
 * Copyright 2011 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package luceneexamples;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public class SortDocuments {
    @Test
    public void index() throws Exception {
        RAMDirectory directory = new RAMDirectory();
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_31);
        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_31, analyzer);
        IndexWriter writer = new IndexWriter(directory, iwc);

        Document doc = new Document();
        doc.add(new Field("str_field", "abc",
                Field.Store.YES, Field.Index.ANALYZED));
        writer.addDocument(doc);
        Document doc2 = new Document();
        doc2.add(new Field("str_field", "def",
                Field.Store.YES, Field.Index.ANALYZED));
        writer.addDocument(doc2);
        Document doc3 = new Document();
        doc3.add(new Field("str_field", "hij",
                Field.Store.YES, Field.Index.ANALYZED));
        writer.addDocument(doc3);
        writer.commit();

        IndexReader reader = IndexReader.open(writer, true);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs td = searcher.search(new MatchAllDocsQuery()
                , 1000, new Sort(new SortField("str_field", SortField.STRING)));
        assertThat(td.totalHits, is(3));
        System.out.println(searcher.doc(td.scoreDocs[0].doc));
        assertThat(searcher.doc(td.scoreDocs[0].doc).get("str_field"), equalTo("abc"));
        assertThat(searcher.doc(td.scoreDocs[1].doc).get("str_field"), equalTo("def"));
        assertThat(searcher.doc(td.scoreDocs[2].doc).get("str_field"), equalTo("hij"));

        td = searcher.search(new MatchAllDocsQuery()
                , 1000, new Sort(new SortField("str_field", SortField.STRING, true)));
        assertThat(td.totalHits, is(3));
        assertThat(searcher.doc(td.scoreDocs[0].doc).get("str_field"), equalTo("hij"));
        assertThat(searcher.doc(td.scoreDocs[1].doc).get("str_field"), equalTo("def"));
        assertThat(searcher.doc(td.scoreDocs[2].doc).get("str_field"), equalTo("abc"));

        reader.close();
        writer.close();
        searcher.close();
        directory.close();
    }
}
