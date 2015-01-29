package com.metasys.ryft.program;

import java.util.ArrayList;
import java.util.List;

import com.metasys.ryft.Query;
import com.metasys.ryft.Query.SortOrder;
import com.metasys.ryft.RyftException;

/**
 * Helper methods to generate the C program using the Ryft primitives.
 *
 * @author Sylvain Crozon
 *
 */
public final class RyftPrimitives {

    protected static final String ERROR_PREFIX = "PRIERROR: ";
    protected static final String STATISTICS_PREFIX = "STATS: ";

    public enum Statistics {

        // search, term, sort
        START_TIME("START_TIME", "uint64_t", "start", "PRIu64"),
        // search, term, sort
        EXECUTION_DURATION("EXECUTION_DURATION", "uint64_t", "duration", "PRIu64"),
        // search, term
        TOTAL_BYTES_PROCESSED("TOTAL_BYTES_PROCESSED", "uint64_t", "bytes", "PRIu64"),
        // search
        TOTAL_NUMBER_OF_MATCHES("TOTAL_NUMBER_OF_MATCHES", "uint64_t", "matches", "PRIu64"),
        // term
        NUMBER_OF_TERMS("NUMBER_OF_TERMS", "uint64_t", "terms", "PRIu64");

        private String name;
        private String type;
        private String variable;
        private String format;

        private Statistics(String name, String type, String variable, String format) {
            this.name = name;
            this.type = type;
            this.variable = variable;
            this.format = format;
        }

    }

    private RyftPrimitives() {
        super();
    }

    protected static void initProgram(ProgramWriter program) throws RyftException {
        program.append("#include <stdio.h>");
        program.append("#include <time.h>");
        program.append("#include <libryftone.h>").newLine();
        program.append("int main(__attribute__ ((unused))int argc, __attribute__ ((unused))char* argv[]) {");
        program.append("int ret_val = 0;", 1);
        program.append("rol_result_t input;", 1);
        program.append("rol_result_t output;", 1);
    }

    protected static void openFile(ProgramWriter program, String[] files) throws RyftException {
        program.append("const char* files[] = {", 1);
        for (String file : files) {
            program.append("\"" + file + "\",", 2);
        }
        program.append("};", 1);
        wrapPrimitive(program, "rol_process_files(&input, files, " + files.length + ")");
    }

    protected static void writeIndex(ProgramWriter program, String file) throws RyftException {
        wrapPrimitive(program, "rol_set_index_results_file(&output, \"" + file + "\")");
    }

    protected static void writeData(ProgramWriter program, String file) throws RyftException {
        wrapPrimitive(program, "rol_set_data_results_file(&output, \"" + file + "\")");
    }

    protected static void search(ProgramWriter program, String searchString, int width) throws RyftException {
        wrapPrimitive(program, "rol_search_exact(&output, &input, \"" + checkSearchExpression(searchString) + "\", " + width + ")");
    }

    protected static void fuzzySearch(ProgramWriter program, String searchString, int width, int fuzziness) throws RyftException {
        wrapPrimitive(program, "rol_search_fuzzy(&output, &input, \"" + checkSearchExpression(searchString) + "\", " + width + ", " + fuzziness + ")");
    }

    protected static void sort(ProgramWriter program, String field, SortOrder order) throws RyftException {
        wrapPrimitive(program, "rol_sort_" + (SortOrder.ASC == order ? "ascending" : "descending") + "(&output, &input, \"" + field + "\")");
    }

    protected static void termFrequency(ProgramWriter program, String format, String field) throws RyftException {
        wrapPrimitive(program, "rol_term_frequency(&output, &input, \"" + format + "\", \"" + field + "\")");
    }

    protected static void execute(ProgramWriter program, int nodes) throws RyftException {
        wrapPrimitive(program, "rol_execute_algorithm(" + nodes + ")");
    }

    protected static void statistics(ProgramWriter program, String type) throws RyftException {
        List<Statistics> stats = new ArrayList<>();
        stats.add(Statistics.START_TIME);
        stats.add(Statistics.EXECUTION_DURATION);
        if (Query.SEARCH.equals(type) || Query.FUZZY.equals(type)) {
            stats.add(Statistics.TOTAL_BYTES_PROCESSED);
            stats.add(Statistics.TOTAL_NUMBER_OF_MATCHES);
        } else if (Query.TERM.equals(type)) {
            stats.add(Statistics.TOTAL_BYTES_PROCESSED);
            stats.add(Statistics.NUMBER_OF_TERMS);
        }
        for (Statistics stat : stats) {
            program.append(stat.type + ' ' + stat.variable + ';', 1);
            program.append("rol_get_statistics(&output, \"" + stat.name + "\", &" + stat.variable + ");", 1);
            program.append("printf(\"" + STATISTICS_PREFIX + stat.name + ": %\"" + stat.format + "\"\\n\", " + stat.variable + ");", 1);
        }
    }

    protected static void closeProgram(ProgramWriter program) throws RyftException {
        program.append("return ret_val;", 1);
        program.append("}");
    }

    private static void wrapPrimitive(ProgramWriter program, String primitive) throws RyftException {
        program.append("ret_val = " + primitive + ";", 1);
        program.append("if (ret_val != 0) {", 1);
        program.append("printf(\"" + ERROR_PREFIX + "%s:\\n\", rol_get_error_string());", 2);
        program.append("return ret_val;", 2);
        program.append("}", 1);
    }

    private static String checkSearchExpression(String search) {
        if (search.contains(Query.RAW) || search.contains(Query.RECORD)) {
            return escape(search);
        }
        return escape('(' + Query.RAW + " CONTAINS \"" + search + "\")");
    }

    private static String escape(String value) {
        return value.replace("\"", "\\\"");
    }
}
