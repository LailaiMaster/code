#include <io_utils.h>
#include <curl/curl.h>

//=============== C 的第三方依赖管理工具：conan ===============
int main(void) {
  FILE *file = fopen("bennyhuo.json", "w");
  CURL *curl = curl_easy_init();

  struct curl_slist *headers = NULL;
  headers = curl_slist_append(headers, "User-Agent: Awesome-Octocat-App");

  curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headers);
  curl_easy_setopt(curl, CURLOPT_URL, "https://api.github.com/users/bennyhuo");
  //curl_easy_setopt(curl, CURLOPT_URL, "https://www.baidu.com");
  curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, fwrite);
  curl_easy_setopt(curl, CURLOPT_WRITEDATA, file);
  curl_easy_setopt(curl, CURLOPT_SSL_OPTIONS, CURLSSLOPT_NATIVE_CA);

  CURLcode res = curl_easy_perform(curl);

  PRINT_INT(res);
  curl_slist_free_all(headers);
  curl_easy_cleanup(curl);

  fclose(file);
  return 0;
}