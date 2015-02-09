package com.metasys.ryft.program;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.StrictExpectations;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.metasys.ryft.Query;
import com.metasys.ryft.Query.SortOrder;
import com.metasys.ryft.Result;
import com.metasys.ryft.RyftException;
import com.metasys.ryft.program.RyftPrimitives.Statistics;

public class ProgramManagerTest {

    @Mocked
    private ProcessBuilder processBuilder;
    @Mocked
    private Process process;

    private ProgramManager pm;
    private Query query;

    @Before
    public void setUp() throws Exception {
        pm = new ProgramManager();
        pm.setTmpDir("target/c");
        pm.setStatistics(true);
        pm.init();
        query = new Query();
        query.setInput("inputFile1.txt,inputFile2.txt");
        query.setOutput("results.txt");
        query.setWriteIndex(true);
        query.setSearchQuery("(RAW_TEXT CONTAINS \"something\")");
        query.setSearchWidth(20);
        query.setFuzzyQuery("(RAW_TEXT CONTAINS \"something\")");
        query.setFuzzyWidth(20);
        query.setFuzziness(5);
        query.setSortField("sortField");
        query.setTermField("termField");
        query.setTermFormat("RAW_TEXT");
        query.setNodes(2);
        new MockUp<System>() {
            @Mock
            long currentTimeMillis() {
                return 1234567890000l;
            }
        };
    }

    @After
    public void tearDownAfter() throws Exception {
        FileUtils.deleteDirectory(new File("target/c"));
    }

    @Test(expected = RyftException.class)
    public void testGenerateInvalidQuery() throws Exception {
        pm.generate(query, "invalid");
    }

    @Test
    public void testGenerateNullOutput() throws Exception {
        query.setOutput(null);
        query.setType(Query.SEARCH);
        pm.generate(query, "nulloutput");
        Assert.assertEquals("demo/output_1234567890000", query.getOutput());
    }

    @Test
    public void testGenerateEmptyOutput() throws Exception {
        query.setOutput("");
        query.setType(Query.SEARCH);
        pm.generate(query, "emptyoutput");
        Assert.assertEquals("demo/output_1234567890000", query.getOutput());
    }

    @Test
    public void testGenerateSearch() throws Exception {
        generateProgram(Query.SEARCH);
    }

    @Test
    public void testGenerateSearchNoIdentifier() throws Exception {
        query.setSearchQuery("something");
        generateProgram(Query.SEARCH);
    }

    @Test
    public void testGenerateSearchRecord() throws Exception {
        query.setType(Query.SEARCH);
        query.setSearchQuery("RECORD something");
        pm.generate(query, "search_record");
        assertProgram("search_record");
    }

    @Test
    public void testGenerateSearchNoIndex() throws Exception {
        query.setType(Query.SEARCH);
        query.setWriteIndex(false);
        pm.generate(query, "search_noindex");
        assertProgram("search_noindex");
    }

    @Test
    public void testGenerateSearchNoStats() throws Exception {
        query.setType(Query.SEARCH);
        pm.setStatistics(false);
        pm.generate(query, "search_nostats");
        assertProgram("search_nostats");
    }

    @Test
    public void testGenerateSearchSubFolder() throws Exception {
        query.setType(Query.SEARCH);
        query.setOutput("sub/output");
        pm.generate(query, "search_subfolder");
        assertProgram("search_subfolder");
    }

    @Test
    public void testGenerateFuzzySearch() throws Exception {
        generateProgram(Query.FUZZY);
    }

    @Test
    public void testGenerateFuzzySearchNoIdentifier() throws Exception {
        query.setFuzzyQuery("something");
        generateProgram(Query.FUZZY);
    }

    @Test
    public void testGenerateSortAscending() throws Exception {
        query.setSortOrder(SortOrder.ASC);
        generateProgram(Query.SORT);
    }

    @Test
    public void testGenerateSortDescending() throws Exception {
        query.setType(Query.SORT);
        query.setSortOrder(SortOrder.DESC);
        pm.generate(query, "sort_descending");
        assertProgram("sort_descending");
    }

    @Test
    public void testGenerateTermFrequency() throws Exception {
        generateProgram(Query.TERM);
    }

    @Test
    public void testCompile() throws Exception {
        new StrictExpectations() {
            {
                new ProcessBuilder("/bin/sh", "-c", "/usr/bin/gcc -Wextra -g -Wall -L/usr/lib/x86_64-linux-gnu/ -c -o main.o main.c");
                result = processBuilder;
                processBuilder.directory(new File("target/c/id"));
                result = processBuilder;
                processBuilder.redirectError(new File("target/c/id/stderr.log"));
                result = processBuilder;
                processBuilder.redirectOutput(new File("target/c/id/stdout.log"));
                result = processBuilder;
                processBuilder.start();
                result = process;
                process.waitFor();
                result = 0;
                new ProcessBuilder("/bin/sh", "-c", "/usr/bin/gcc main.o -o ryft_demo -lryftone");
                result = processBuilder;
                processBuilder.directory(new File("target/c/id"));
                result = processBuilder;
                processBuilder.redirectError(new File("target/c/id/stderr.log"));
                result = processBuilder;
                processBuilder.redirectOutput(new File("target/c/id/stdout.log"));
                result = processBuilder;
                processBuilder.start();
                result = process;
                process.waitFor();
                result = 0;
            }
        };
        Assert.assertEquals(0, pm.compile("id"));
    }

    @Test(expected = RyftException.class)
    public void testCompileException() throws Exception {
        new StrictExpectations() {
            {
                new ProcessBuilder("/bin/sh", "-c", "/usr/bin/gcc -Wextra -g -Wall -L/usr/lib/x86_64-linux-gnu/ -c -o main.o main.c");
                result = processBuilder;
                processBuilder.directory(new File("target/c/id"));
                result = processBuilder;
                processBuilder.redirectError(new File("target/c/id/stderr.log"));
                result = processBuilder;
                processBuilder.redirectOutput(new File("target/c/id/stdout.log"));
                result = processBuilder;
                processBuilder.start();
                result = new IOException();
            }
        };
        pm.compile("id");
    }

    @Test
    public void testCompileObjectError() throws Exception {
        new StrictExpectations() {
            {
                new ProcessBuilder("/bin/sh", "-c", "/usr/bin/gcc -Wextra -g -Wall -L/usr/lib/x86_64-linux-gnu/ -c -o main.o main.c");
                result = processBuilder;
                processBuilder.directory(new File("target/c/id"));
                result = processBuilder;
                processBuilder.redirectError(new File("target/c/id/stderr.log"));
                result = processBuilder;
                processBuilder.redirectOutput(new File("target/c/id/stdout.log"));
                result = processBuilder;
                processBuilder.start();
                result = process;
                process.waitFor();
                result = 2;
            }
        };
        Assert.assertEquals(2, pm.compile("id"));
    }

    @Test
    public void testCompileLibError() throws Exception {
        new StrictExpectations() {
            {
                new ProcessBuilder("/bin/sh", "-c", "/usr/bin/gcc -Wextra -g -Wall -L/usr/lib/x86_64-linux-gnu/ -c -o main.o main.c");
                result = processBuilder;
                processBuilder.directory(new File("target/c/id"));
                result = processBuilder;
                processBuilder.redirectError(new File("target/c/id/stderr.log"));
                result = processBuilder;
                processBuilder.redirectOutput(new File("target/c/id/stdout.log"));
                result = processBuilder;
                processBuilder.start();
                result = process;
                process.waitFor();
                result = 0;
                new ProcessBuilder("/bin/sh", "-c", "/usr/bin/gcc main.o -o ryft_demo -lryftone");
                result = processBuilder;
                processBuilder.directory(new File("target/c/id"));
                result = processBuilder;
                processBuilder.redirectError(new File("target/c/id/stderr.log"));
                result = processBuilder;
                processBuilder.redirectOutput(new File("target/c/id/stdout.log"));
                result = processBuilder;
                processBuilder.start();
                result = process;
                process.waitFor();
                result = 2;
            }
        };
        Assert.assertEquals(2, pm.compile("id"));
    }

    @Test
    public void testExecute() throws Exception {
        new StrictExpectations() {
            {
                new ProcessBuilder("/bin/sh", "-c", "./ryft_demo");
                result = processBuilder;
                processBuilder.directory(new File("target/c/id"));
                result = processBuilder;
                processBuilder.redirectError(new File("target/c/id/stderr.log"));
                result = processBuilder;
                processBuilder.redirectOutput(new File("target/c/id/stdout.log"));
                result = processBuilder;
                processBuilder.start();
                result = process;
                process.waitFor();
                result = 0;
            }
        };
        Assert.assertEquals(0, pm.execute("id"));
    }

    @Test
    public void testExecuteError() throws Exception {
        new StrictExpectations() {
            {
                new ProcessBuilder("/bin/sh", "-c", "./ryft_demo");
                result = processBuilder;
                processBuilder.directory(new File("target/c/id"));
                result = processBuilder;
                processBuilder.redirectError(new File("target/c/id/stderr.log"));
                result = processBuilder;
                processBuilder.redirectOutput(new File("target/c/id/stdout.log"));
                result = processBuilder;
                processBuilder.start();
                result = process;
                process.waitFor();
                result = 255;
            }
        };
        Assert.assertEquals(255, pm.execute("id"));
    }

    @Test
    public void testExecuteErrorWithLog() throws Exception {
        new StrictExpectations() {
            {
                new ProcessBuilder("/bin/sh", "-c", "./ryft_demo");
                result = processBuilder;
                processBuilder.directory(new File("target/c/log"));
                result = processBuilder;
                processBuilder.redirectError(new File("target/c/log/stderr.log"));
                result = processBuilder;
                processBuilder.redirectOutput(new File("target/c/log/stdout.log"));
                result = processBuilder;
                processBuilder.start();
                result = process;
                process.waitFor();
                result = 255;
            }
        };
        new File("target/c/log").mkdirs();
        FileWriter fw = new FileWriter("target/c/log/stdout.log");
        fw.write("something\n");
        fw.write("PRIERROR: error from ryft #1\n");
        fw.write("something else\n");
        fw.write("PRIERROR: error from ryft #2\n");
        fw.write("something\n");
        fw.flush();
        fw.close();
        try {
            pm.execute("log");
            Assert.fail();
        } catch (RyftException e) {
            Assert.assertEquals("[error from ryft #1, error from ryft #2]", e.getMessage());
        }
    }

    @Test(expected = RyftException.class)
    public void testExecuteException() throws Exception {
        new StrictExpectations() {
            {
                new ProcessBuilder("/bin/sh", "-c", "./ryft_demo");
                result = processBuilder;
                processBuilder.directory(new File("target/c/id"));
                result = processBuilder;
                processBuilder.redirectError(new File("target/c/id/stderr.log"));
                result = processBuilder;
                processBuilder.redirectOutput(new File("target/c/id/stdout.log"));
                result = processBuilder;
                processBuilder.start();
                result = new IOException();
            }
        };
        pm.execute("id");
    }

    @Test
    public void testGetSearchResult() throws Exception {
        new File("target/c/stats").mkdirs();
        FileWriter fw = new FileWriter("target/c/stats/stdout.log");
        fw.write("something\n");
        fw.write("STATS: START_TIME: 123\n");
        fw.write("STATS: EXECUTION_DURATION: 456\n");
        fw.write("STATS: TOTAL_BYTES_PROCESSED: 789\n");
        fw.write("STATS: TOTAL_NUMBER_OF_MATCHES: 147\n");
        fw.write("something\n");
        fw.flush();
        fw.close();
        Result result = pm.getResult("stats");
        Assert.assertEquals(4, result.getStatistics().size());
        Assert.assertEquals(Statistics.START_TIME, result.getStatistics().get(0).getName());
        Assert.assertEquals(123l, result.getStatistics().get(0).getValue());
        Assert.assertEquals(Statistics.EXECUTION_DURATION, result.getStatistics().get(1).getName());
        Assert.assertEquals(456l, result.getStatistics().get(1).getValue());
        Assert.assertEquals(Statistics.TOTAL_BYTES_PROCESSED, result.getStatistics().get(2).getName());
        Assert.assertEquals(789l, result.getStatistics().get(2).getValue());
        Assert.assertEquals(Statistics.TOTAL_NUMBER_OF_MATCHES, result.getStatistics().get(3).getName());
        Assert.assertEquals(147l, result.getStatistics().get(3).getValue());
    }

    @Test
    public void testGetTermResult() throws Exception {
        new File("target/c/stats").mkdirs();
        FileWriter fw = new FileWriter("target/c/stats/stdout.log");
        fw.write("something\n");
        fw.write("STATS: START_TIME: 123\n");
        fw.write("STATS: EXECUTION_DURATION: 456\n");
        fw.write("STATS: TOTAL_BYTES_PROCESSED: 789\n");
        fw.write("STATS: NUMBER_OF_TERMS: 147\n");
        fw.write("something\n");
        fw.flush();
        fw.close();
        Result result = pm.getResult("stats");
        Assert.assertEquals(4, result.getStatistics().size());
        Assert.assertEquals(Statistics.START_TIME, result.getStatistics().get(0).getName());
        Assert.assertEquals(123l, result.getStatistics().get(0).getValue());
        Assert.assertEquals(Statistics.EXECUTION_DURATION, result.getStatistics().get(1).getName());
        Assert.assertEquals(456l, result.getStatistics().get(1).getValue());
        Assert.assertEquals(Statistics.TOTAL_BYTES_PROCESSED, result.getStatistics().get(2).getName());
        Assert.assertEquals(789l, result.getStatistics().get(2).getValue());
        Assert.assertEquals(Statistics.NUMBER_OF_TERMS, result.getStatistics().get(3).getName());
        Assert.assertEquals(147l, result.getStatistics().get(3).getValue());
    }

    @Test
    public void testGetResultBadStat() throws Exception {
        new File("target/c/stats").mkdirs();
        FileWriter fw = new FileWriter("target/c/stats/stdout.log");
        fw.write("something\n");
        fw.write("STATS: START_TIME: 123\n");
        fw.write("STATS: UNKNOWN: 456\n");
        fw.write("STATS: TOTAL_BYTES_PROCESSED: 789\n");
        fw.write("STATS: NUMBER_OF_TERMS: 147\n");
        fw.write("something\n");
        fw.flush();
        fw.close();
        Result result = pm.getResult("stats");
        Assert.assertEquals(3, result.getStatistics().size());
        Assert.assertEquals(Statistics.START_TIME, result.getStatistics().get(0).getName());
        Assert.assertEquals(123l, result.getStatistics().get(0).getValue());
        Assert.assertEquals(Statistics.TOTAL_BYTES_PROCESSED, result.getStatistics().get(1).getName());
        Assert.assertEquals(789l, result.getStatistics().get(1).getValue());
        Assert.assertEquals(Statistics.NUMBER_OF_TERMS, result.getStatistics().get(2).getName());
        Assert.assertEquals(147l, result.getStatistics().get(2).getValue());
    }

    @Test
    public void testGetResultBadStatValue() throws Exception {
        new File("target/c/stats").mkdirs();
        FileWriter fw = new FileWriter("target/c/stats/stdout.log");
        fw.write("something\n");
        fw.write("STATS: START_TIME: 123\n");
        fw.write("STATS: EXECUTION_DURATION: nan\n");
        fw.write("STATS: TOTAL_BYTES_PROCESSED: 789\n");
        fw.write("STATS: NUMBER_OF_TERMS: 147\n");
        fw.write("something\n");
        fw.flush();
        fw.close();
        Result result = pm.getResult("stats");
        Assert.assertEquals(3, result.getStatistics().size());
        Assert.assertEquals(Statistics.START_TIME, result.getStatistics().get(0).getName());
        Assert.assertEquals(123l, result.getStatistics().get(0).getValue());
        Assert.assertEquals(Statistics.TOTAL_BYTES_PROCESSED, result.getStatistics().get(1).getName());
        Assert.assertEquals(789l, result.getStatistics().get(1).getValue());
        Assert.assertEquals(Statistics.NUMBER_OF_TERMS, result.getStatistics().get(2).getName());
        Assert.assertEquals(147l, result.getStatistics().get(2).getValue());
    }

    @Test
    public void testGetResultNoLog() throws Exception {
        Result result = pm.getResult("stats");
        Assert.assertEquals(0, result.getStatistics().size());
    }

    private void generateProgram(String type) throws Exception {
        query.setType(type);
        pm.generate(query, type);
        assertProgram(type);
    }

    private void assertProgram(String name) throws Exception {
        StringWriter program = new StringWriter();
        StringWriter expected = new StringWriter();
        InputStream fis = new FileInputStream("target/c/" + name + "/main.c");
        IOUtils.copy(fis, program);
        fis.close();
        InputStream res = Thread.currentThread().getContextClassLoader().getResourceAsStream("expected_" + name + ".c");
        IOUtils.copy(res, expected);
        res.close();
        Assert.assertEquals(expected.toString(), program.toString());
    }

}
