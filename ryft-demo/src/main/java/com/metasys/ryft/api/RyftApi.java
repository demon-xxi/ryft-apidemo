package com.metasys.ryft.api;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.metasys.ryft.Query;
import com.metasys.ryft.Result;
import com.metasys.ryft.Result.Stat;
import com.metasys.ryft.RyftException;
import com.metasys.ryft.program.ProgramManager;
import com.metasys.ryft.program.RyftPrimitives.Statistics;

/**
 * Ryft One API to execute priitives against the Ryft Box.
 *
 * <p>
 * The API can be access via REST
 *
 * @author Sylvain Crozon
 *
 */
@Controller
@RequestMapping("/ryftone")
public class RyftApi {

    private static final Logger LOG = LogManager.getLogger(RyftApi.class);

    @Value("${ryft.mock:false}")
    private boolean mock;
    @Value("${ryft.mock.successRate:0.8}")
    private double successRate;
    @Value("${ryft.fs.root}")
    private String ryftFsRoot;
    @Autowired
    private ProgramManager programManager;

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Result execute(@RequestBody Query query) throws Exception {
        LOG.debug("Executing (mocked: {}) {}", mock, query);
        query.validate();
        String queryId = "demo_" + System.currentTimeMillis() + "_" + query.hashCode();
        query.setId(queryId);
        Result result;
        if (mock) {
            result = mock(query);
        } else {
            ThreadContext.put("query-id", queryId);
            programManager.generate(query, queryId);
            long start = System.currentTimeMillis();
            if (programManager.compile(queryId) == 0 && programManager.execute(queryId) == 0) {
                result = programManager.getResult(queryId);
                if (result.getStatistics().isEmpty()) {
                    // if stats aren't available, publish at least start time and duration from the API's point of view
                    result.getStatistics().add(new Stat(Statistics.START_TIME, start));
                    result.getStatistics().add(new Stat(Statistics.EXECUTION_DURATION, System.currentTimeMillis() - start));
                }
            } else {
                throw RyftException.EXECUTE_PROGRAM;
            }
        }
        result.setOutputFile(FileBrowserApi.EXPECTED_ROOT + query.getOutput());
        if (query.isWriteIndex() && (Query.SEARCH.equals(query.getType()) || Query.FUZZY.equals(query.getType()))) {
            result.setIndexFile(FileBrowserApi.EXPECTED_ROOT + ProgramManager.INDEX_PREFIX + query.getOutput());
        }
        LOG.debug("Result: {}", result);
        return result;
    }

    private Result mock(Query query) throws RyftException {
        query.validate();
        Result result = new Result();
        long start = System.currentTimeMillis();
        try {
            Thread.sleep((long) (1000 * Math.random()));
        } catch (InterruptedException e) {
            throw RyftException.GENERIC;
        } finally {
            if (Math.random() > successRate) {
                throw new RyftException("Randomly failing");
            }
            try {
                FileWriter fw = new FileWriter(new File(ryftFsRoot, query.getOutput()));
                fw.write("mocked output file content");
                fw.flush();
                fw.close();
                fw = new FileWriter(new File(ryftFsRoot, ProgramManager.INDEX_PREFIX + query.getOutput()));
                fw.write("mocked output index file content");
                fw.flush();
                fw.close();
            } catch (IOException e) {
                LOG.error("Error writing mocked output", e);
            }
            result.getStatistics().add(new Stat(Statistics.START_TIME, start));
            result.getStatistics().add(new Stat(Statistics.EXECUTION_DURATION, System.currentTimeMillis() - start));
            if (Query.SEARCH.equals(query.getType()) || Query.FUZZY.equals(query.getType())) {
                result.getStatistics().add(new Stat(Statistics.TOTAL_BYTES_PROCESSED, (long) (1234567 * Math.random())));
                result.getStatistics().add(new Stat(Statistics.TOTAL_NUMBER_OF_MATCHES, (long) (963258 * Math.random())));
            } else if (Query.TERM.equals(query.getType())) {
                result.getStatistics().add(new Stat(Statistics.TOTAL_BYTES_PROCESSED, (long) (14785236 * Math.random())));
                result.getStatistics().add(new Stat(Statistics.NUMBER_OF_TERMS, (long) (321478 * Math.random())));
            }
        }
        return result;
    }

    public void setProgramManager(ProgramManager programManager) {
        this.programManager = programManager;
    }

    public void setMock(boolean mock) {
        this.mock = mock;
    }

    public void setSuccessRate(double successRate) {
        this.successRate = successRate;
    }

    public void setRyftFsRoot(String ryftFsRoot) {
        this.ryftFsRoot = ryftFsRoot;
    }
}
