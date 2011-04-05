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
import org.apache.lucene.document.NumericField;
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
import org.apache.lucene.util.NumericUtils;
import org.apache.lucene.util.Version;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public class NumericFieldDocument {
    @Test
    public void index() throws Exception {
        RAMDirectory directory = new RAMDirectory();
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_31);
        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_31, analyzer);
        IndexWriter writer = new IndexWriter(directory, iwc);

        for (int i = 8; i < 12; i++) {
            Document doc = new Document();
            doc.add(new NumericField("int_field", Field.Store.YES, true).setIntValue(i));
            System.out.println(doc);
            writer.addDocument(doc);
        }
        writer.commit();

        IndexReader reader = IndexReader.open(writer, true);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs td = searcher.search(new MatchAllDocsQuery()
                , 1000, new Sort(new SortField("int_field", SortField.INT)));
        assertThat(td.totalHits, is(4));
        assertThat(searcher.doc(td.scoreDocs[0].doc).get("int_field"), equalTo("8"));
        assertThat(searcher.doc(td.scoreDocs[1].doc).get("int_field"), equalTo("9"));
        assertThat(searcher.doc(td.scoreDocs[2].doc).get("int_field"), equalTo("10"));
        assertThat(searcher.doc(td.scoreDocs[3].doc).get("int_field"), equalTo("11"));

        reader.close();
        writer.close();
        searcher.close();
        directory.close();
    }
}
