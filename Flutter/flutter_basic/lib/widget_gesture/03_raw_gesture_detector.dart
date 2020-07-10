import 'package:flutter/material.dart';
import 'package:flutter/gestures.dart';

Widget buildDoubleGestureWidget() => DoubleGestureWidget();

class DoubleGestureWidget extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: RawGestureDetector(

      gestures: {
        MultipleTapGestureRecognizer:
            GestureRecognizerFactoryWithHandlers<MultipleTapGestureRecognizer>(() => MultipleTapGestureRecognizer(),
                  (MultipleTapGestureRecognizer instance) {
            instance.onTap = () => print('parent tapped ');
          },
        )
      },

      child: Container(
        color: Colors.pinkAccent,
        child: Center(
          child: GestureDetector(
              onTap: () => print('Child tapped'),
              child: Container(
                color: Colors.blueAccent,
                width: 200.0,
                height: 200.0,
              )),
        ),
      ),
    ));
  }
}

class MultipleTapGestureRecognizer extends TapGestureRecognizer {
  @override
  void rejectGesture(int pointer) {
    acceptGesture(pointer);
  }
}
