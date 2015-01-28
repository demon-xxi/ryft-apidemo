package com.metasys.ryft.api;

import mockit.FullVerifications;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.StrictExpectations;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.metasys.ryft.Query;
import com.metasys.ryft.Result;
import com.metasys.ryft.RyftException;
import com.metasys.ryft.program.ProgramManager;

public class RyftApiTest {

    @Mocked
    private ProgramManager pm;
    private RyftApi api;
    private Query query;
    private Result queryResult;
    private String id;

    @Before
    public void setUp() {
        query = new Query();
        query.setInput("inputFile1.txt,inputFile2.txt");
        query.setOutput("results.txt");
        query.setType(Query.SEARCH);
        query.setSearchQuery("(RAW_TEXT CONTAINS \"something\")");
        query.setSearchWidth(20);
        query.setNodes(2);
        new MockUp<System>() {
            @Mock
            long currentTimeMillis() {
                return 1234567890000l;
            }
        };
        id = "demo_1234567890000_" + query.hashCode();
        api = new RyftApi();
        api.setProgramManager(pm);
        queryResult = new Result();
    }

    @Test
    public void testMockFail() throws Exception {
        api.setMock(true);
        api.setSuccessRate(0);
        try {
            api.execute(query);
            Assert.fail();
        } catch (RyftException e) {
            Assert.assertEquals(id, query.getId());
            Assert.assertEquals("Randomly failing", e.getMessage());
        }
        new FullVerifications() {
            {
                // nothing should happen
            }
        };
    }

    @Test
    public void testMockSuccess() throws Exception {
        api.setMock(true);
        api.setSuccessRate(1);
        Result result = api.execute(query);
        Assert.assertEquals(id, query.getId());
        Assert.assertNotNull(result);
        new FullVerifications() {
            {
                // nothing should happen
            }
        };
    }

    @Test
    public void testOK() throws Exception {
        new StrictExpectations() {
            {
                pm.generate(query, id);
                pm.compile(id);
                result = 0;
                pm.execute(id);
                result = 0;
                pm.getResult(id);
                result = queryResult;
            }
        };
        Result result = api.execute(query);
        Assert.assertEquals(id, query.getId());
        Assert.assertEquals(queryResult, result);
    }

    @Test(expected = RyftException.class)
    public void testGenerationError() throws Exception {
        new StrictExpectations() {
            {
                pm.generate(query, id);
                result = new RyftException("");
            }
        };
        api.execute(query);
    }

    @Test(expected = RyftException.class)
    public void testCompilationError() throws Exception {
        new StrictExpectations() {
            {
                pm.generate(query, id);
                pm.compile(id);
                result = new RyftException("");
            }
        };
        api.execute(query);
    }

    @Test(expected = RyftException.class)
    public void testCompilationNonZeroExitCode() throws Exception {
        new StrictExpectations() {
            {
                pm.generate(query, id);
                pm.compile(id);
                result = -1;
            }
        };
        api.execute(query);
    }

    @Test(expected = RyftException.class)
    public void testExecutionError() throws Exception {
        new StrictExpectations() {
            {
                pm.generate(query, id);
                pm.compile(id);
                result = 0;
                pm.execute(id);
                result = new RyftException("");
            }
        };
        api.execute(query);
    }

    @Test(expected = RyftException.class)
    public void testExecutionNonZeroExitCode() throws Exception {
        new StrictExpectations() {
            {
                pm.generate(query, id);
                pm.compile(id);
                result = 0;
                pm.execute(id);
                result = -1;
            }
        };
        api.execute(query);
    }
}
