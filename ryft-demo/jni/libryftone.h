/********************************************************************
* This software is "commercial computer software" as defined in the *
* Federal Acquisition Regulations and is subject to Ryft Systems    *
* Corporation's standard End User License Agreement.                *
* Inc's standard End User License Agreement.                        *
*                                                                   *
* CONFIDENTIAL - All source code is the propriety and confidential  *
* information of Ryft Systems Inc.                                  *
*                                                                   *
* Copyright 2014-2015 Ryft Systems Inc.                             *
* Unpublished -- all rights reserved under the copyright laws       *
* of the United States.                                             *
*********************************************************************/

#ifndef LIBRYFTONE_H_
#define LIBRYFTONE_H_

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <stdint.h>

// Defining Error Codes to return for functions
#define ERROR_CODE_SEND_FAILURE                -7
#define ERROR_CODE_TIMEOUT                     -8
#define ERROR_CODE_INVALID_MESSAGE             -5
#define ERROR_CODE_ERROR_FROM_CCC              -9
#define ERROR_CODE_ERROR_FROM_PREVIOUS        -11
#define ERROR_CODE_UNEXPECTED                 -15
#define ERROR_CODE_UNKNOWN_ERROR              -25
#define ERROR_CODE_NORMAL_TERMINATION          25     // not an error
#define ERROR_CODE_ABNORMAL_TERMINATION       -22
#define ERROR_CODE_INVALID_NUMBER_OF_FILES    -27

// contains either a set of data being operated on, or returned by a primitive library operation
typedef struct ryft_one_results
{
    int index; // integer used for referencing each result
} rol_result_t;

// Open files
int rol_process_files(rol_result_t* result, const char* list_of_files[], const uint16_t number_of_files);

// Tell where to write results to
int rol_set_index_results_file(const rol_result_t* input_data, const char* index_file_location);
int rol_set_data_results_file(const rol_result_t* input_data, const char* data_file_location);

// Search Functions
int rol_search_exact(rol_result_t* result, const rol_result_t* input_data, const char* match_criteria_query_string, const uint16_t surrounding_width);
int rol_search_fuzzy(rol_result_t* result, const rol_result_t* input_data, const char* match_criteria_query_string, const uint16_t surrounding_width, const uint8_t fuzziness);

// Sort Functions
int rol_sort_ascending(rol_result_t* result, const rol_result_t* input_data, const char* key_field_name);
int rol_sort_descending(rol_result_t* result, const rol_result_t* input_data, const char* key_field_name);

// Term Frequency Functions
int rol_term_frequency(rol_result_t* result, rol_result_t* input_data, const char* input_data_format);

// Execute the algorithm
int rol_execute_algorithm(const uint16_t nodes_to_process);

// Get Error String
char* rol_get_error_string(void);

#endif //LIBRYFTONE_H_
