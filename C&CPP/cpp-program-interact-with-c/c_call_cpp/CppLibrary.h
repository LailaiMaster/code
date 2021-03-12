#ifndef INTERACTOR_CPPLIBRARY_H
#define INTERACTOR_CPPLIBRARY_H

//这里必须加上 __cplusplus 判断，否则会报错，不知为何。
#ifdef  __cplusplus
extern "C" {
#endif

int add(int a, int b);

#ifdef  __cplusplus
}
#endif

#endif
