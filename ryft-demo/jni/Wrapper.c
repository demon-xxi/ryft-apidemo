#include <jni.h>
#include <stdio.h>
#include "com_metasys_ryft_Wrapper.h"
#include "libryftone.h"

int ret_val = 0;
rol_result_t input;
rol_result_t output;

void Java_com_metasys_ryft_Wrapper_init(__attribute__((unused))JNIEnv *env, __attribute__((unused))jclass class) {
	printf("Ryft Wrapper\n");
	fflush(stdout);
	return;
}

void check_ret_val() {
	if (ret_val != 0) {
		printf("%s:\n", rol_get_error_string());
	}
}

jint Java_com_metasys_ryft_Wrapper_openFile(JNIEnv *env, __attribute__((unused))jclass class, jobjectArray files) {
	int length = (*env)->GetArrayLength(env, files);
	const char* input_files[length];
	jstring jfiles[length];
	int i;
	for (i=0; i<length; i++) {
			jfiles[i] = (*env)->GetObjectArrayElement(env, files, i);
			input_files[i] = (*env)->GetStringUTFChars(env, jfiles[i], NULL);
	}
	ret_val = rol_process_files(&input, input_files, length);
	check_ret_val();
	for (i=0; i<length; i++) {
			(*env)->ReleaseStringUTFChars(env, jfiles[i], input_files[i]);
	}
	return ret_val;
}

jint Java_com_metasys_ryft_Wrapper_writeIndex (JNIEnv *env, __attribute__((unused))jclass class, jstring file) {
	const char* index = (*env)->GetStringUTFChars(env, file, NULL);
	ret_val = rol_set_index_results_file(&output, index);
	check_ret_val();
	(*env)->ReleaseStringUTFChars(env, file, index);
	return ret_val;
}

jint Java_com_metasys_ryft_Wrapper_writeData (JNIEnv *env, __attribute__((unused))jclass class, jstring file) {
	const char* data = (*env)->GetStringUTFChars(env, file, NULL);
	ret_val = rol_set_data_results_file(&output, data);
	check_ret_val();
	(*env)->ReleaseStringUTFChars(env, file, data);
	return ret_val;
}

jint Java_com_metasys_ryft_Wrapper_search (JNIEnv *env, __attribute__((unused))jclass class, jstring searchString, jint width) {
	const char* search = (*env)->GetStringUTFChars(env, searchString, NULL);
	ret_val = rol_search_exact(&output, &input, search, width);
	check_ret_val();
	(*env)->ReleaseStringUTFChars(env, searchString, search);
	return ret_val;
}

jint Java_com_metasys_ryft_Wrapper_fuzzySearch (JNIEnv *env, __attribute__((unused))jclass class, jstring searchString, jint width, jint fuzziness) {
	const char* search = (*env)->GetStringUTFChars(env, searchString, NULL);
	ret_val = rol_search_fuzzy(&output, &input, search, width, fuzziness);
	check_ret_val();
	(*env)->ReleaseStringUTFChars(env, searchString, search);
	return ret_val;
}

jint Java_com_metasys_ryft_Wrapper_sortAsc (JNIEnv *env, __attribute__((unused))jclass class, jstring field) {
	const char* sField = (*env)->GetStringUTFChars(env, field, NULL);
	ret_val = rol_sort_ascending(&output, &input, sField);
	check_ret_val();
	(*env)->ReleaseStringUTFChars(env, field, sField);
	return ret_val;
}

jint Java_com_metasys_ryft_Wrapper_sortDesc (JNIEnv *env, __attribute__((unused))jclass class, jstring field) {
	const char* sField = (*env)->GetStringUTFChars(env, field, NULL);
	ret_val = rol_sort_descending(&output, &input, sField);
	check_ret_val();
	(*env)->ReleaseStringUTFChars(env, field, sField);
	return ret_val;
}

jint Java_com_metasys_ryft_Wrapper_termFrequency (JNIEnv *env, __attribute__((unused))jclass class, jstring format) {
	const char* sFormat = (*env)->GetStringUTFChars(env, format, NULL);
	ret_val = rol_term_frequency(&output, &input, sFormat);
	check_ret_val();
	(*env)->ReleaseStringUTFChars(env, format, sFormat);
	return ret_val;
}

jint Java_com_metasys_ryft_Wrapper_execute (__attribute__((unused))JNIEnv *env, __attribute__((unused))jclass class, jint nodes) {
	ret_val = rol_execute_algorithm(nodes);
	check_ret_val();
	return ret_val;
}

