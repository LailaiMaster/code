#include <stdio.h>
#include <stdlib.h>
#include "stdafx.h"

extern "C"{
	#define __STDC_CONSTANT_MACROS
	#include <libavutil/log.h>
}

int _main(){

	//设置日至等级
	av_log_set_level(AV_LOG_DEBUG);
	//打印log
	av_log(NULL, AV_LOG_INFO, "...%s\n", "Hello World");
	//暂停
	system("pause");
	return EXIT_SUCCESS;

}