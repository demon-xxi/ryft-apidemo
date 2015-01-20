#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <stdint.h>

typedef struct ryft_one_results
{
    int index; // integer used for referencing each result
} rol_result_t;

// Open files
int rol_process_files(rol_result_t* result, const char* list_of_files[], const uint16_t number_of_files) {
	printf("rol_process_files: ");
	int i;
	for(i = 0; i < number_of_files; i++) {
		printf("%s,", list_of_files[i]);
	}
	printf("\n");
	fflush(stdout);
	return 0;
}

// Tell where to write results to
int rol_set_index_results_file(const rol_result_t* input_data, const char* index_file_location) {
	printf("rol_set_index_results_file: %s\n", index_file_location);
	fflush(stdout);
	return 0;
}
int rol_set_data_results_file(const rol_result_t* input_data, const char* data_file_location) {
	printf("rol_set_data_results_file: %s\n", data_file_location);
	fflush(stdout);
	return 0;
}

// Search Functions
int rol_search_exact(rol_result_t* result, const rol_result_t* input_data, const char* match_criteria_query_string, const uint16_t surrounding_width) {
	printf("rol_search_exact: %s, %d\n", match_criteria_query_string, surrounding_width);
	fflush(stdout);
	return 0;
}
int rol_search_fuzzy(rol_result_t* result, const rol_result_t* input_data, const char* match_criteria_query_string, const uint16_t surrounding_width, const uint8_t fuzziness) {
	printf("rol_search_fuzzy: %s, %d, %d\n", match_criteria_query_string, surrounding_width, fuzziness);
	fflush(stdout);
	return 0;
}

// Sort Functions
int rol_sort_ascending(rol_result_t* result, const rol_result_t* input_data, const char* key_field_name) {
	printf("rol_sort_ascending: %s\n", key_field_name);
	fflush(stdout);
	return 0;
}
int rol_sort_descending(rol_result_t* result, const rol_result_t* input_data, const char* key_field_name) {
	printf("rol_sort_descending: %s\n", key_field_name);
	fflush(stdout);
	return 0;
}

// Term Frequency Functions
int rol_term_frequency(rol_result_t* result, rol_result_t* input_data, const char* input_data_format) {
	printf("rol_term_frequency: %s\n", input_data_format);
	fflush(stdout);
	return 0;
}

// Execute the algorithm
int rol_execute_algorithm(const uint16_t nodes_to_process) {
	printf("rol_execute_algorithm: %d\n", nodes_to_process);
	fflush(stdout);
	return 0;
}

// Get Error String
char* rol_get_error_string(void) {
	printf("rol_get_error_string\n");
	fflush(stdout);
	return 0;
}

