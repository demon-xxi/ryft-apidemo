#define __STDC_FORMAT_MACROS
#include <stdio.h>
#include <inttypes.h>
#include <time.h>
#include <libryftone.h>

int main(__attribute__ ((unused))int argc, __attribute__ ((unused))char* argv[]) {
    int ret_val = 0;
    rol_result_t input;
    rol_result_t output;
    const char* files[] = {
        "inputFile1.txt",
        "inputFile2.txt",
    };
    ret_val = rol_process_files(&input, files, 2);
    if (ret_val != 0) {
        printf("PRIERROR: %s:\n", rol_get_error_string());
        return ret_val;
    }
    char *strDelimiter = "del";
    ret_val = rol_search_exact(&output, &input, "(RAW_TEXT CONTAINS \"something\")", 20, strDelimiter);
    if (ret_val != 0) {
        printf("PRIERROR: %s:\n", rol_get_error_string());
        return ret_val;
    }
    ret_val = rol_set_data_results_file(&output, "results.txt");
    if (ret_val != 0) {
        printf("PRIERROR: %s:\n", rol_get_error_string());
        return ret_val;
    }
    ret_val = rol_set_index_results_file(&output, "index_results.txt");
    if (ret_val != 0) {
        printf("PRIERROR: %s:\n", rol_get_error_string());
        return ret_val;
    }
    ret_val = rol_execute_algorithm(2, 0);
    if (ret_val != 0) {
        printf("PRIERROR: %s:\n", rol_get_error_string());
        return ret_val;
    }
    uint64_t start;
    rol_get_statistics(&output, "START_TIME", &start);
    printf("STATS: START_TIME: %"PRIu64"\n", start);
    uint64_t duration;
    rol_get_statistics(&output, "EXECUTION_DURATION", &duration);
    printf("STATS: EXECUTION_DURATION: %"PRIu64"\n", duration);
    uint64_t bytes;
    rol_get_statistics(&output, "TOTAL_BYTES_PROCESSED", &bytes);
    printf("STATS: TOTAL_BYTES_PROCESSED: %"PRIu64"\n", bytes);
    uint64_t matches;
    rol_get_statistics(&output, "TOTAL_NUMBER_OF_MATCHES", &matches);
    printf("STATS: TOTAL_NUMBER_OF_MATCHES: %"PRIu64"\n", matches);
    return ret_val;
}
