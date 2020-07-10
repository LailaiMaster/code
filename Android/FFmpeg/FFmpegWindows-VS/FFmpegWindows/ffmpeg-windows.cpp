// FFmpegWindows.cpp : 定义控制台应用程序的入口点。
//

#include "stdafx.h"
#include <stdio.h>
#include <stdlib.h>

//C/C++混编，指示编译器按照C语言进行编译
extern "C"
{
#include "libavcodec\avcodec.h"
};

void _main(){
	printf("%s \n", avcodec_configuration());
	system("pause");
}
