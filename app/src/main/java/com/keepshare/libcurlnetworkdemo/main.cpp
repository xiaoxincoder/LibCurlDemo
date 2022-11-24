// #include <iostream>
// int main(int argc, char *argv[])
// {
// 	printf("this is the first progress!!");
// 	std::cout << "Hello world!" << std::endl;
// 	printf("the world is end ");
// 	return 0;
// }

#include <iostream>
#include <stdio.h>
#include <curl/curl.h>
#include <stdlib.h>

void requestGet();
void requestPost();
void getInfo(CURL *curl, std::string heaers, std::string response);

static size_t http_curl_write_data(void *buffer, size_t size, size_t nmemb, void *userp)
{
	if (NULL == userp || NULL == buffer || 0 == size)
		return 0;
	std::cout << "请求回调: " << std::endl;
	size_t realSize = size * nmemb;
	std::string *pstr = (std::string *)userp;
	if (NULL != pstr)
	{
		pstr->append((const char *)buffer, realSize);
	}
	// std::cout<<"返回接收的数据: "<< pstr->c_str() <<std::endl;
	return realSize;
}

static size_t header_callback(char *buffer, size_t size, size_t nitems, void *userdata)
{

	if (NULL == userdata || NULL == buffer || 0 == size)
		return 0;
	std::cout << "请求头回调: " << "size: "<< size << "nitems:" << nitems << std::endl;
	size_t realSize = size * nitems;
	std::string *pstr = (std::string *)userdata;
	if (NULL != pstr)
	{
		pstr->append((const char *)buffer, realSize);
	}
	std::cout << "返回接收的数据: " << pstr->c_str() << std::endl;
	return realSize;
}

static void dump(const char *text,
          FILE *stream, unsigned char *ptr, size_t size)
{
  size_t i;
  size_t c;
  unsigned int width=0x10;
 
  fprintf(stream, "%s, %10.10ld bytes (0x%8.8lx)\n",
          text, (long)size, (long)size);
 
  for(i=0; i<size; i+= width) {
    fprintf(stream, "%4.4lx: ", (long)i);
 
    /* show hex to the left */
    for(c = 0; c < width; c++) {
      if(i+c < size)
        fprintf(stream, "%02x ", ptr[i+c]);
      else
        fputs("   ", stream);
    }
 
    /* show data on the right */
    for(c = 0; (c < width) && (i+c < size); c++) {
      char x = (ptr[i+c] >= 0x20 && ptr[i+c] < 0x80) ? ptr[i+c] : '.';
      fputc(x, stream);
    }
 
    fputc('\n', stream); /* newline */
  }
}

static int my_trace(CURL *handle, curl_infotype type,
             char *data, size_t size,
             void *userp)
{
  const char *text;
  (void)handle; /* prevent compiler warning */
  (void)userp;
 
  switch (type) {
  case CURLINFO_TEXT:
    fputs("== Info: ", stderr);
    fwrite(data, size, 1, stderr);
  default: /* in case a new one is introduced to shock us */
    return 0;
 
  case CURLINFO_HEADER_OUT:
    text = "=> Send header";
    break;
  case CURLINFO_DATA_OUT:
    text = "=> Send data";
    break;
  case CURLINFO_SSL_DATA_OUT:
    text = "=> Send SSL data";
    break;
  case CURLINFO_HEADER_IN:
    text = "<= Recv header";
    break;
  case CURLINFO_DATA_IN:
    text = "<= Recv data";
    break;
  case CURLINFO_SSL_DATA_IN:
    text = "<= Recv SSL data";
    break;
  }
 
  dump(text, stderr, (unsigned char *)data, size);
  return 0;
}

int main(int argc, char *argv[])
{

	// requestGet();

	requestPost();
	// puts("c语言中文网");

	return 0;
}

void requestGet()
{

	CURL *curl;	  // 定义CURL类型的指针
	CURLcode res; // 定义CURLcode类型的变量，保存返回状态码

	char url[] = "http://www.baidu.com";
	curl = curl_easy_init(); // 初始化一个CURL类型的指针

	if (curl != NULL)
	{
		// 设置curl选项. 其中CURLOPT_URL是让用户指 定url. url中存放的是网址
		curl_easy_setopt(curl, CURLOPT_URL, url);
		curl_easy_setopt(curl, CURLOPT_TIMEOUT, 1000);
		curl_easy_setopt(curl, CURLOPT_CONNECTTIMEOUT, 1000);
		// curl_easy_setopt(curl, CURLOPT_HEADER, 1);
		// curl_easy_setopt(curl, CURLOPT_NOBODY, 1);
		// curl_easy_setopt(curl, CURLOPT_VERBOSE, 1);
		// curl_easy_setopt(curl, CURLOPT_HTTPGET, false);

		std::string strResp;
		curl_easy_setopt(curl, CURLOPT_WRITEDATA, (void *)&strResp);
		curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, http_curl_write_data);

		std::string strHeaders;
		curl_easy_setopt(curl, CURLOPT_HEADERDATA, (void *)&strHeaders);
		// curl_easy_setopt(curl, CURLOPT_HEADERFUNCTION, header_callback);

		// std::cout<<"response: "<<strResp<<std::endl;
		std::cout << "开始请求: " << std::endl;
		// 调用curl_easy_perform 执行我们的设置.并进行相关的操作. 在这 里只在屏幕上显示出来.
		res = curl_easy_perform(curl);

		if (res == CURLE_OK)
		{
			// getInfo(&url, strHeaders, strResp);
			// 请求总时长
			int totalTime = 0;
			curl_easy_getinfo(curl, CURLINFO_TOTAL_TIME, &totalTime);
			if (totalTime)
				std::cout << "耗时:" << totalTime << std::endl;

			double downLength = 0;
			curl_easy_getinfo(curl, CURLINFO_CONTENT_LENGTH_DOWNLOAD, &downLength);
			if (downLength)
				std::cout << "下载的文件大小:" << downLength << std::endl;

			long retcode = 0;
			curl_easy_getinfo(curl, CURLINFO_RESPONSE_CODE, &retcode);

			if (retcode)
				std::cout << "状态码:" << retcode << std::endl;

			char *contentType = {0};
			curl_easy_getinfo(curl, CURLINFO_CONTENT_TYPE, &contentType);
			if (contentType)
				std::cout << "请求的文件类型:" << contentType << std::endl;

			// 输出cookie信息
			//  print_cookies(curl);

			long filetime = 0;
			curl_easy_getinfo(curl, CURLINFO_FILETIME, &filetime);
			if (filetime)
				std::cout << "远程获取文档的时间:" << filetime << std::endl;

			long namelookuptime = 0;
			curl_easy_getinfo(curl, CURLINFO_NAMELOOKUP_TIME, &namelookuptime);
			if (namelookuptime)
				std::cout << "名称解析所消耗的时间:" << namelookuptime << "" << std::endl;

			long requestSize = 0;
			curl_easy_getinfo(curl, CURLINFO_REQUEST_SIZE, &requestSize);
			if (requestSize)
				std::cout << "请求头大小:" << requestSize << "字节" << std::endl;

			long headerSize = 0;
			curl_easy_getinfo(curl, CURLINFO_HEADER_SIZE, &headerSize);
			if (headerSize)
				std::cout << "响应头大小:" << headerSize << "字节" << std::endl;

			// 获取URL重定向地址
			curl_easy_setopt(curl, CURLOPT_FOLLOWLOCATION, true);
			char *redirectUrl = {0};
			curl_easy_getinfo(curl, CURLINFO_REDIRECT_URL, &redirectUrl);
			if (redirectUrl)
				std::cout << "URL重定向地址:" << redirectUrl << std::endl;
			else
				std::cout << "URL重定向地址:" << NULL << std::endl;

			char *ipAddress = {0};
			curl_easy_getinfo(curl, CURLINFO_PRIMARY_IP, &ipAddress);
			if (ipAddress)
				std::cout << "请求的服务器IP:" << ipAddress << std::endl;

			double downloadSpeed = 0;
			curl_easy_getinfo(curl, CURLINFO_SPEED_DOWNLOAD, &downloadSpeed);
			if (downloadSpeed)
				printf("平均下载速度: %0.3f kb/s.\n", downloadSpeed / 1024);
			//
			char *protol = {0};
			curl_easy_getinfo(curl, CURLINFO_SCHEME, &protol);

			if (protol)
				// printf("请求协议: " << protol);
				std::cout << "请求协议: " << protol << std::endl;

			char *method = {0};
			curl_easy_getinfo(curl, CURLINFO_EFFECTIVE_METHOD, &method);
			if (method)
				std::cout << "请求方法: " << method << std::endl;

			char *url = {0};
			curl_easy_getinfo(curl, CURLINFO_EFFECTIVE_URL, &url);
			if (url)
				std::cout << "请求URL: " << url << std::endl;

			long methodVersion = 0;
			curl_easy_getinfo(curl, CURLINFO_HTTP_VERSION, &methodVersion);
			if (methodVersion)
				std::cout << "http版本号: " << methodVersion << std::endl;

			std::cout << "response data: \n"
					  << std::endl;
			std::cout << strResp.c_str() << std::endl;

			std::cout << "response headers: \n"
					  << std::endl;
			std::cout << strResp.c_str() << std::endl;
		}
		else
		{
			std::cout << "网络请求失败: " << res << std::endl;
		}
		// 清除curl操作.
		curl_easy_cleanup(curl);
	}
}

void requestPost()
{
	CURL *curl;
	CURLcode res;
	curl_global_init(CURL_GLOBAL_ALL);
	curl = curl_easy_init();
	if (curl)
	{

		curl_easy_setopt(curl, CURLOPT_URL, "https://acp.z-onesoftware.com/racar/ra10/omp/aggregator/api/mid/productManage/getGoodsList/v2"); // 指定url
		curl_easy_setopt(curl, CURLOPT_CUSTOMREQUEST, "POST"); // 自定义请求方法
		// curl_easy_setopt(curl, CURLOPT_VERBOSE, 1);
		curl_easy_setopt(curl, CURLOPT_TIMEOUT_MS, 10000);
		curl_easy_setopt(curl, CURLOPT_ACCEPT_ENCODING, "");
		curl_easy_setopt(curl, CURLOPT_CONNECTTIMEOUT_MS, 100000);
		curl_easy_setopt(curl, CURLOPT_HEADER, 1);


  		struct curl_slist *headers = NULL;
		headers = curl_slist_append(headers, "Content-Type: application/json;charset=utf-8");
		headers = curl_slist_append(headers, "Content-Length: 139");
		headers = curl_slist_append(headers, "Accept-Encoding: gzip");
		headers = curl_slist_append(headers, "Accept: application/json;charset=utf-8");
		headers = curl_slist_append(headers, "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4MDAyMjQiLCJpZCI6ODAwMjI0LCJpbmRleCI6NDksImRldmljZSI6InBob25lIiwiYXBwX2lkIjoxLCJleHAiOjE2Njc5NzU2NjF9.fUZsek9ep9DTWemuk7Xr2NC2Cvv7-BnEJGy5v3TbPMcupRcA6q1r8TTn-w4tVK-TLEgYFiqDaDmfRPWmNJpVPw");
		headers = curl_slist_append(headers, "requestId: RI20221120122254528fb73247d");
		headers = curl_slist_append(headers, "x-device-from: phone");
		headers = curl_slist_append(headers, "deviceId: phone");
		headers = curl_slist_append(headers, "nonce: 5631b350-2ce2-4c23-aa50-db722cff83bc");
		headers = curl_slist_append(headers, "version: 1.0");
		headers = curl_slist_append(headers, "sdkVersion: 1.0.0");
		headers = curl_slist_append(headers, "deviceFrom: third");
		headers = curl_slist_append(headers, "signType: sha256Hex");
		headers = curl_slist_append(headers, "sign: 1FB03FF9EB94DDDAA49559D70DF61CD63307D145855AC72E300CBE4963B9304A");
		headers = curl_slist_append(headers, "accountNo: acc2022102200140001");
		headers = curl_slist_append(headers, "x-device-id: 0fba3265456cae11a3656ae5fc040f199f73");
		headers = curl_slist_append(headers, "x-app-id: 1");
		headers = curl_slist_append(headers, "Expect:");

		curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headers);

        std::string body = "{\"categoryFrontId\":277,\"needCache\":false,\"pageNo\":1,\"pageSize\":20,\"terminals\":2,\"userId\":\"800224\",\"vehiclesId\":43,\"vin\":\"SPTESTEP35000028\"}";
		curl_easy_setopt(curl, CURLOPT_POSTFIELDS, body.c_str());
		curl_easy_setopt(curl, CURLOPT_POSTFIELDSIZE, body.size());

		std::string strResp;
		curl_easy_setopt(curl, CURLOPT_WRITEDATA, (void *)&strResp);
		curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, http_curl_write_data);

		std::string strHeaders;
		curl_easy_setopt(curl, CURLOPT_HEADERDATA, (void *)&strHeaders);
		curl_easy_setopt(curl, CURLOPT_HEADERFUNCTION, header_callback);
		
		res = curl_easy_perform(curl);

		if (res==CURLE_OK)
		{
			// 请求总时长
			int totalTime = 0;
			curl_easy_getinfo(curl, CURLINFO_TOTAL_TIME, &totalTime);
			if (totalTime)
				std::cout << "耗时:" << totalTime << std::endl;

			double downLength = 0;
			curl_easy_getinfo(curl, CURLINFO_CONTENT_LENGTH_DOWNLOAD, &downLength);
			if (downLength)
				std::cout << "下载的文件大小:" << downLength << std::endl;

			long retcode = 0;
			curl_easy_getinfo(curl, CURLINFO_RESPONSE_CODE, &retcode);

			if (retcode)
				std::cout << "状态码:" << retcode << std::endl;

			char *contentType = {0};
			curl_easy_getinfo(curl, CURLINFO_CONTENT_TYPE, &contentType);
			if (contentType)
				std::cout << "请求的文件类型:" << contentType << std::endl;

			// 输出cookie信息
			//  print_cookies(curl);

			long filetime = 0;
			curl_easy_getinfo(curl, CURLINFO_FILETIME, &filetime);
			if (filetime)
				std::cout << "远程获取文档的时间:" << filetime << std::endl;

			long namelookuptime = 0;
			curl_easy_getinfo(curl, CURLINFO_NAMELOOKUP_TIME, &namelookuptime);
			if (namelookuptime)
				std::cout << "名称解析所消耗的时间:" << namelookuptime << "" << std::endl;

			long requestSize = 0;
			curl_easy_getinfo(curl, CURLINFO_REQUEST_SIZE, &requestSize);
			if (requestSize)
				std::cout << "请求头大小:" << requestSize << "字节" << std::endl;

			long headerSize = 0;
			curl_easy_getinfo(curl, CURLINFO_HEADER_SIZE, &headerSize);
			if (headerSize)
				std::cout << "响应头大小:" << headerSize << "字节" << std::endl;

			// 获取URL重定向地址
			curl_easy_setopt(curl, CURLOPT_FOLLOWLOCATION, true);
			char *redirectUrl = {0};
			curl_easy_getinfo(curl, CURLINFO_REDIRECT_URL, &redirectUrl);
			if (redirectUrl)
				std::cout << "URL重定向地址:" << redirectUrl << std::endl;
			else
				std::cout << "URL重定向地址:" << NULL << std::endl;

			char *ipAddress = {0};
			curl_easy_getinfo(curl, CURLINFO_PRIMARY_IP, &ipAddress);
			if (ipAddress)
				std::cout << "请求的服务器IP:" << ipAddress << std::endl;

			double downloadSpeed = 0;
			curl_easy_getinfo(curl, CURLINFO_SPEED_DOWNLOAD, &downloadSpeed);
			if (downloadSpeed)
				printf("平均下载速度: %0.3f kb/s.\n", downloadSpeed / 1024);
			//
			char *protol = {0};
			curl_easy_getinfo(curl, CURLINFO_SCHEME, &protol);

			if (protol)
				// printf("请求协议: " << protol);
				std::cout << "请求协议: " << protol << std::endl;

			char *method = {0};
			curl_easy_getinfo(curl, CURLINFO_EFFECTIVE_METHOD, &method);
			if (method)
				std::cout << "请求方法: " << method << std::endl;

			char *url = {0};
			curl_easy_getinfo(curl, CURLINFO_EFFECTIVE_URL, &url);
			if (url)
				std::cout << "请求URL: " << url << std::endl;

			long methodVersion = 0;
			curl_easy_getinfo(curl, CURLINFO_HTTP_VERSION, &methodVersion);
			if (methodVersion)
				std::cout << "http版本号: " << methodVersion << std::endl;

			std::cout << "response data: \n"
					  << std::endl;
			std::cout << strResp.c_str() << std::endl;

			std::cout << "response headers: \n"
					  << std::endl;
			std::cout << strHeaders.c_str() << std::endl;
		} else {
			std::cout << "网络请求失败: " << res << std::endl;
			const char* pError = curl_easy_strerror(res);  
			std::cout<<"错误信息: "<<pError<<std::endl;
		}
		
		curl_easy_cleanup(curl);
	}
}

void getInfo(CURL *handler, std::string heaers, std::string response)
{

	CURL *curl = (CURL *)handler;

	// 请求总时长
	int totalTime = 0;
	curl_easy_getinfo(curl, CURLINFO_TOTAL_TIME, &totalTime);
	if (totalTime)
		std::cout << "耗时:" << totalTime << std::endl;

	double downLength = 0;
	curl_easy_getinfo(curl, CURLINFO_CONTENT_LENGTH_DOWNLOAD, &downLength);
	if (downLength)
		std::cout << "下载的文件大小:" << downLength << std::endl;

	long retcode = 0;
	curl_easy_getinfo(curl, CURLINFO_RESPONSE_CODE, &retcode);

	if (retcode)
		std::cout << "状态码:" << retcode << std::endl;

	char *contentType = {0};
	curl_easy_getinfo(curl, CURLINFO_CONTENT_TYPE, &contentType);
	if (contentType)
		std::cout << "请求的文件类型:" << contentType << std::endl;

	// 输出cookie信息
	//  print_cookies(curl);

	long filetime = 0;
	curl_easy_getinfo(curl, CURLINFO_FILETIME, &filetime);
	if (filetime)
		std::cout << "远程获取文档的时间:" << filetime << std::endl;

	long namelookuptime = 0;
	curl_easy_getinfo(curl, CURLINFO_NAMELOOKUP_TIME, &namelookuptime);
	if (namelookuptime)
		std::cout << "名称解析所消耗的时间:" << namelookuptime << "" << std::endl;

	long requestSize = 0;
	curl_easy_getinfo(curl, CURLINFO_REQUEST_SIZE, &requestSize);
	if (requestSize)
		std::cout << "请求头大小:" << requestSize << "字节" << std::endl;

	long headerSize = 0;
	curl_easy_getinfo(curl, CURLINFO_HEADER_SIZE, &headerSize);
	if (headerSize)
		std::cout << "响应头大小:" << headerSize << "字节" << std::endl;

	// 获取URL重定向地址
	curl_easy_setopt(curl, CURLOPT_FOLLOWLOCATION, true);
	char *redirectUrl = {0};
	curl_easy_getinfo(curl, CURLINFO_REDIRECT_URL, &redirectUrl);
	if (redirectUrl)
		std::cout << "URL重定向地址:" << redirectUrl << std::endl;
	else
		std::cout << "URL重定向地址:" << NULL << std::endl;

	char *ipAddress = {0};
	curl_easy_getinfo(curl, CURLINFO_PRIMARY_IP, &ipAddress);
	if (ipAddress)
		std::cout << "请求的服务器IP:" << ipAddress << std::endl;

	double downloadSpeed = 0;
	curl_easy_getinfo(curl, CURLINFO_SPEED_DOWNLOAD, &downloadSpeed);
	if (downloadSpeed)
		printf("平均下载速度: %0.3f kb/s.\n", downloadSpeed / 1024);
	//
	char *protol = {0};
	curl_easy_getinfo(curl, CURLINFO_SCHEME, &protol);

	if (protol)
		// printf("请求协议: " << protol);
		std::cout << "请求协议: " << protol << std::endl;

	char *method = {0};
	curl_easy_getinfo(curl, CURLINFO_EFFECTIVE_METHOD, &method);
	if (method)
		std::cout << "请求方法: " << method << std::endl;

	char *url = {0};
	curl_easy_getinfo(curl, CURLINFO_EFFECTIVE_URL, &url);
	if (url)
		std::cout << "请求URL: " << url << std::endl;

	long methodVersion = 0;
	curl_easy_getinfo(curl, CURLINFO_HTTP_VERSION, &methodVersion);
	if (methodVersion)
		std::cout << "http版本号: " << methodVersion << std::endl;

	std::cout << "response data: \n"
			  << std::endl;
	std::cout << response.c_str() << std::endl;

	std::cout << "response headers: \n"
			  << std::endl;
	std::cout << heaers.c_str() << std::endl;
}

