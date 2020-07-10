import 'dart:async';

void asyncFutureRule1() {
  Future(() => print('f3')).then((_) => print('then 3'));
}

/*
    f6
    f7
    f8
 */
void asyncFutureRule2() {
  Future(() => print('f6'))
      .then((_) => Future(() => print('f7')))
      .then((_) => print('f8'));
}

/*
    f6
    f8
    f7
 */
void asyncFutureRule3() {
  Future(() => print('f6'))
      .then((_) => scheduleMicrotask(()=>print('f7')))
      .then((_) => print('f8'));
}

/*
  f6
  f9
  f7
  f8
 */
void asyncFutureRule4() {
  Future(() => print('f6'))
      .then((_) => Future(() => print('f7')))
      .then((_) => print('f8'));

  Future(() => print('f9'));
}