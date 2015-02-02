package com.metasys.ryft;

import mockit.Mock;
import mockit.MockUp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.metasys.ryft.Query.SortOrder;

public class QueryTest {

    @Before
    public void setUp() {
        new MockUp<System>() {
            @Mock
            long currentTimeMillis() {
                return 1234567890000l;
            }
        };
    }

    @Test
    public void testValidateCommon() {
        Query query = new Query();
        try {
            query.validate();
            Assert.fail();
        } catch (RyftException e) {
            Assert.assertEquals("The type of query must be specified", e.getMessage());
        }
        query.setType("search");
        try {
            query.validate();
            Assert.fail();
        } catch (RyftException e) {
            Assert.assertEquals("The input dataset must be specified", e.getMessage());
        }
        query.setInput("input");
        try {
            query.validate();
            Assert.fail();
        } catch (RyftException e) {
            Assert.assertEquals("search", query.getType());
            Assert.assertEquals("input", query.getInput());
            Assert.assertEquals("demo_output_1234567890000", query.getOutput());
            Assert.assertEquals(2, query.getNodes().intValue());
        }
    }

    @Test
    public void testValidateSearch() throws RyftException {
        Query query = new Query();
        query.setType("search");
        query.setInput("input");
        try {
            query.validate();
            Assert.fail();
        } catch (RyftException e) {
            Assert.assertEquals("Both the query string and surrounding width must be specified for a search query", e.getMessage());
        }
        query.setSearchQuery("query");
        try {
            query.validate();
            Assert.fail();
        } catch (RyftException e) {
            Assert.assertEquals("Both the query string and surrounding width must be specified for a search query", e.getMessage());
        }
        query.setSearchWidth(2);
        query.validate();
    }

    @Test
    public void testValidateFuzzy() throws RyftException {
        Query query = new Query();
        query.setType("fuzzy");
        query.setInput("input");
        try {
            query.validate();
            Assert.fail();
        } catch (RyftException e) {
            Assert.assertEquals("The query string, surrounding width and fuzziness must be specified for a fuzzy search query", e.getMessage());
        }
        query.setFuzzyQuery("query");
        try {
            query.validate();
            Assert.fail();
        } catch (RyftException e) {
            Assert.assertEquals("The query string, surrounding width and fuzziness must be specified for a fuzzy search query", e.getMessage());
        }
        query.setFuzzyWidth(2);
        try {
            query.validate();
            Assert.fail();
        } catch (RyftException e) {
            Assert.assertEquals("The query string, surrounding width and fuzziness must be specified for a fuzzy search query", e.getMessage());
        }
        query.setFuzziness(3);
        query.validate();
    }

    @Test
    public void testValidateTerm() throws RyftException {
        Query query = new Query();
        query.setType("term");
        query.setInput("input");
        try {
            query.validate();
            Assert.fail();
        } catch (RyftException e) {
            Assert.assertEquals("Both the field and format must be specified for a term frequency query", e.getMessage());
        }
        query.setTermField("field");
        try {
            query.validate();
            Assert.fail();
        } catch (RyftException e) {
            Assert.assertEquals("Both the field and format must be specified for a term frequency query", e.getMessage());
        }
        query.setTermFormat("format");
        query.validate();
    }

    @Test
    public void testValidateSort() throws RyftException {
        Query query = new Query();
        query.setType("sort");
        query.setInput("input");
        try {
            query.validate();
            Assert.fail();
        } catch (RyftException e) {
            Assert.assertEquals("The sort field must be specified for a sort query", e.getMessage());
            Assert.assertEquals(SortOrder.ASC, query.getSortOrder());
        }
        query.setSortField("field");
        query.validate();
    }

    @Test
    public void testValidateWrongType() {
        Query query = new Query();
        query.setType("unknown");
        query.setInput("input");
        try {
            query.validate();
            Assert.fail();
        } catch (RyftException e) {
            Assert.assertEquals("Unknown query type", e.getMessage());
        }
    }

}
