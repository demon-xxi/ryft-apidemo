package com.metasys.ryft.program;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.metasys.ryft.Query;
import com.metasys.ryft.Result;
import com.metasys.ryft.Result.Stat;
import com.metasys.ryft.RyftException;
import com.metasys.ryft.program.RyftPrimitives.Statistics;

/**
 * Handles the generation, compilation and execution of a Ryft program based on a {@link Query} object.
 *
 * @author Sylvain Crozon
 *
 */
@Component
public class ProgramManager {

    private static final Logger LOG = LogManager.getLogger(ProgramManager.class);

    // compilation instructions
    private static final String O_COMPILATION = "/usr/bin/g++ -Wextra -g -Wall -L/usr/lib/x86_64-linux-gnu/ -c -o main.o main.cpp";
    private static final String LIB_COMPILATION = "/usr/bin/g++ main.o -o ryft_demo -lryftone";

    private static final String STD_OUT = "stdout.log";
    private static final String STD_ERR = "stderr.log";

    public static final String INDEX_PREFIX = "index_";

    @Value("${ryft.workingDir:/tmp}")
    private String tmpDir;
    @Value("${ryft.statistics:true}")
    private boolean statistics;
    @Value("${ryft.fs.root}")
    private String rootFolder;

    private File workingDirectory;

    @PostConstruct
    public void init() throws RyftException {
        workingDirectory = new File(tmpDir);
        checkDir(workingDirectory);
    }

    public void generate(Query query, String id) throws RyftException {
        File programWorkDir = workingDirectory(id);
        LOG.debug("Generating C++ program for {} under {}", id, programWorkDir);

        query.validate();
        File output = new File(rootFolder, query.getOutput());
        if (output.getParentFile() != null && !output.getParentFile().exists()) {
            if (!output.getParentFile().mkdirs()) {
                LOG.warn("Error creating output directory for {}", output.getAbsolutePath());
            }
        }

        ProgramWriter program;
        try {
            program = new ProgramWriter(new File(programWorkDir, "main.cpp"));
            RyftPrimitives.initProgram(program, query.getNodes());

            RyftPrimitives.addFile(program, query.getInputFiles());

            // in our demo, we now always generate (as opposed to having the user decide whether to have one or not)
            StringBuilder index = new StringBuilder();
            String outputFolder = new File(query.getOutput()).getParent();
            if (outputFolder != null) {
                index.append(outputFolder).append('/');
            }
            index.append(INDEX_PREFIX).append(output.getName());

            switch (query.getType()) {
                case Query.SEARCH:
                    RyftPrimitives.search(program, query.getSearchQuery(), query.getSearchWidth(), query.getOutput(), index.toString(), query.getSearchDelimiter());
                    break;
                case Query.FUZZY:
                    RyftPrimitives.fuzzySearch(program, query.getFuzzyQuery(), query.getFuzzyWidth(), query.getFuzziness(), query.getOutput(), index.toString(), query.getFuzzyDelimiter());
                    break;
                case Query.TERM:
                    RyftPrimitives.termFrequency(program, query.getTermFormat(), query.getOutput(), query.getTermField(), query.getTermKey());
                    break;
                case Query.SORT:
                    RyftPrimitives.sort(program, query.getSortField(), query.getSortOrder());
                    break;
                default:
                    throw new IllegalArgumentException("Unknown query type");
            }

            RyftPrimitives.checkError(program);

            if (statistics) {
                RyftPrimitives.statistics(program, query.getType());
            }
            RyftPrimitives.closeProgram(program);
            program.flush();
            program.close();
        } catch (Exception e) {
            LOG.error("Error generating C program for query ID " + id, e);
            throw RyftException.GENERATE_PROGRAM;
        }
    }

    public int compile(String id) throws RyftException {
        int res = exec(id, O_COMPILATION);
        if (res == 0) {
            res = exec(id, LIB_COMPILATION);
            if (res != 0) {
                LOG.error("Error compiling C library for query ID {}, exit code: {}", id, res);
            }
        } else {
            LOG.error("Error compiling C objects for query ID {}, exit code: {}", id, res);
        }
        return res;
    }

    public int execute(String id) throws RyftException {
        int res = exec(id, "cd " + workingDirectory(id).getAbsolutePath() + " ; ./ryft_demo");
        if (res != 0) {
            String log = readLog(id);
            if (log != null) {
                Collection<String> errors = extract(log, RyftPrimitives.ERROR_PREFIX);
                if (errors.size() > 0) {
                    throw new RyftException(errors.toString());
                }
            }
        }
        return res;
    }

    public Result getResult(String id) {
        Result result = new Result();
        String log = readLog(id);
        if (log != null) {
            Collection<String> stats = extract(log, RyftPrimitives.STATISTICS_PREFIX);
            for (String stat : stats) {
                String[] kv = stat.split(": ");
                try {
                    Statistics name = Statistics.valueOf(kv[0]);
                    result.getStatistics().add(new Stat(name, Long.parseLong(kv[1])));
                } catch (NumberFormatException e) {
                    LOG.warn("Error reading statistic value {}", stat);
                } catch (IllegalArgumentException e) {
                    LOG.warn("Unable to parse statistic log entry {}", stat);
                }
            }
        }
        return result;
    }

    private File workingDirectory(String id) throws RyftException {
        File dir = new File(workingDirectory, id);
        checkDir(dir);
        return dir;
    }

    private void checkDir(File dir) throws RyftException {
        if (!dir.exists() && !dir.mkdir()) {
            LOG.error("Unable to create directory: {}", dir);
            throw RyftException.GENERATE_PROGRAM;
        } else if (!dir.isDirectory()) {
            LOG.error("Not a directory: {}", dir);
            throw RyftException.GENERATE_PROGRAM;
        }
    }

    private int exec(String id, String cmd) throws RyftException {
        File workDir = workingDirectory(id);
        LOG.debug("Executing {} under {}", cmd, workDir);
        ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", cmd);
        pb.directory(workDir);
        pb.redirectError(new File(workDir, STD_ERR));
        pb.redirectOutput(new File(workDir, STD_OUT));
        try {
            int res = pb.start().waitFor();
            LOG.debug("res: {}", res);
            return res;
        } catch (InterruptedException | IOException e) {
            LOG.error("Error executing " + cmd + " for query ID " + id, e);
            throw RyftException.EXECUTE_PROGRAM;
        }
    }

    private String readLog(String id) {
        try (FileInputStream fis = new FileInputStream(new File(workingDirectory(id), STD_OUT))) {
            StringWriter log = new StringWriter();
            IOUtils.copy(fis, log);
            return log.toString();
        } catch (Exception e) {
            LOG.error("Error reading logs", e);
            return null;
        }
    }

    private Collection<String> extract(String log, String prefix) {
        Collection<String> extract = new ArrayList<>();
        int errorIndex = log.indexOf(prefix);
        while (errorIndex != -1) {
            int end = log.indexOf('\n', errorIndex);
            extract.add(log.substring(errorIndex + prefix.length(), end));
            errorIndex = log.indexOf(prefix, end);
        }
        return extract;
    }

    public void setTmpDir(String tmpDir) {
        this.tmpDir = tmpDir;
    }

    public void setStatistics(boolean statistics) {
        this.statistics = statistics;
    }

}
