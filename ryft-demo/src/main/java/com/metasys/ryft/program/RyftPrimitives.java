package com.metasys.ryft.program;

import java.util.ArrayList;
import java.util.List;

import com.metasys.ryft.Query;
import com.metasys.ryft.Query.SortOrder;
import com.metasys.ryft.Query.TermFormat;
import com.metasys.ryft.RyftException;

/**
 * Helper methods to generate the C program using the Ryft primitives.
 *
 * @author Sylvain Crozon
 *
 */
public final class RyftPrimitives {

    protected static final String ERROR_PREFIX = "ERROR: ";
    protected static final String STATISTICS_PREFIX = "STATS: ";

    public enum Statistics {

        // search, term, sort
        START_TIME("START_TIME", "rol_ds_get_start_time(rdsOutput)", "start"),
        // search, term, sort
        EXECUTION_DURATION("EXECUTION_DURATION", "rol_ds_get_execution_duration(rdsOutput)", "duration"),
        // search, term
        TOTAL_BYTES_PROCESSED("TOTAL_BYTES_PROCESSED", "rol_ds_get_total_bytes_processed(rdsOutput)", "bytes"),
        // search
        TOTAL_NUMBER_OF_MATCHES("TOTAL_NUMBER_OF_MATCHES", "rol_ds_get_total_matches(rdsOutput)", "matches"),
        // term
        NUMBER_OF_TERMS("NUMBER_OF_TERMS", "rol_ds_get_total_unique_terms(rdsOutput)", "terms");

        private String name;
        private String call;
        private String variable;

        private Statistics(String name, String call, String variable) {
            this.name = name;
            this.call = call;
            this.variable = variable;
        }

    }

    private RyftPrimitives() {
        super();
    }

    protected static void initProgram(ProgramWriter program, int nodes) throws RyftException {
        program.append("#include \"stdio.h\"").newLine();
        program.append("#include \"libryftone.h\"").newLine();
        program.append("int main(__attribute__ ((unused))int argc, __attribute__ ((unused))char* argv[]) {");
        program.append("rol_data_set_t rdsInput = rol_ds_create_with_nodes(" + nodes + ");", 1);
    }

    protected static void addFile(ProgramWriter program, String[] files) throws RyftException {
        for (String file : files) {
            program.append("rol_ds_add_file(rdsInput, \"" + file + "\");", 1);
        }
    }

    protected static void search(ProgramWriter program, String searchString, int width, String dataResults, String indexResults, String delimiter) throws RyftException {
        program.append("rol_data_set_t rdsOutput = rol_ds_search_exact(rdsInput, \"" + dataResults + "\", \"" + checkSearchExpression(searchString) + "\", " + width + ", \"" + delimiter + "\", \"" + indexResults + "\", true, NULL);", 1);
    }

    protected static void fuzzySearch(ProgramWriter program, String searchString, int width, int fuzziness, String dataResults, String indexResults, String delimiter) throws RyftException {
        program.append("rol_data_set_t rdsOutput = rol_ds_search_fuzzy_hamming(rdsInput, \"" + dataResults + "\", \"" + checkSearchExpression(searchString) + "\", " + width + ", " + fuzziness + ", \"" + delimiter + "\", \"" + indexResults + "\", true, NULL);", 1);
    }

    protected static void sort(ProgramWriter program, String field, SortOrder order) throws RyftException {
    }

    protected static void termFrequency(ProgramWriter program, TermFormat format, String dataResults, String field, String key) throws RyftException {
        switch (format) {
            case RAW:
                program.append("rol_data_set_t rdsOutput = rol_ds_term_frequency_rawtext(rdsInput, \"" + dataResults + "\", true, NULL)", 1);
                break;
            case RECORD:
                program.append("rol_data_set_t rdsOutput = rol_ds_term_frequency_record(rdsInput, \"" + dataResults + "\", true, \"" + key + "\", NULL)", 1);
                break;
            case FIELD:
                program.append("rol_data_set_t rdsOutput = rol_ds_term_frequency_record(rdsInput, \"" + dataResults + "\", true, \"" + key + "\", \"" + field + "\", NULL)", 1);
                break;
            default:
                throw new IllegalStateException("Unknown format " + format);
        }
    }

    protected static void checkError(ProgramWriter program) throws RyftException {
        program.append("if (rol_ds_has_error_occurred(rdsOutput)) {", 1);
        program.append("printf(\"" + ERROR_PREFIX + "%s:\\n\", rol_ds_get_error_string(rdsOutput));", 2);
        program.append("}", 1);
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
            program.append("long " + stat.variable + " = " + stat.call + ";", 1);
            program.append("printf(\"" + STATISTICS_PREFIX + stat.name + ": %ld\\n\"" + ", " + stat.variable + ");", 1);
        }
    }

    protected static void closeProgram(ProgramWriter program) throws RyftException {
        program.append("rol_ds_delete(&rdsOutput);", 1);
        program.append("rol_ds_delete(&rdsInput);", 1);
        program.append("return 0;", 1);
        program.append("}");
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
