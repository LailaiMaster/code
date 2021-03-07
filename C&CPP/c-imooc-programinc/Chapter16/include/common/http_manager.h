#ifndef BASICC_DOWNLOADER_HTTP_MANAGER_H_
#define BASICC_DOWNLOADER_HTTP_MANAGER_H_

#include <curl/curl.h>
#include <stdint.h>
#include <tinycthread.h>
#include "common/http_common.h"

typedef struct Request {
  void *receiver;
  void *request_parameter;
  void **request_handler;
  OnRequestError error_callback;
  OnRequestSuccess success_callback;
  OnRequestCancellation cancel_callback;
  OnRequestProgress progress_callback;
} Request;


typedef struct RequestContext {
  Request *request;

  char *url;
  char *download_directory;

  int header_only;

  int64_t range_start;
  int64_t range_end;

  /**
   * use this to pause downloading.
   */
  int is_running;

  /**
   * Used internally.
   */
  CURL *curl;

  CURLcode curl_code;
  long response_code;
  /**
   * if NULL, automatically generated from header or url.
   * Should be released by user.
   */
  char *output_filename;
  int64_t file_size;
  /**
   * File opened according to the filename and directory. Will be closed after data transferring.
   */
  FILE *output_stream;
} RequestContext;

RequestContext *CreateRequestContext(Request *request, char const *url, char const *directory);

void DestroyRequestContext(RequestContext *context);

void SendRequest(RequestContext *request_context);

#endif //BASICC_DOWNLOADER_HTTP_MANAGER_H_
