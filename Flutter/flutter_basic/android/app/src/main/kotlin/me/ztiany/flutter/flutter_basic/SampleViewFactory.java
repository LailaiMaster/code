package me.ztiany.flutter.flutter_basic;

import android.content.Context;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

class SampleViewFactory extends PlatformViewFactory {

  private final BinaryMessenger messenger;

  public SampleViewFactory(BinaryMessenger msger) {
    super(StandardMessageCodec.INSTANCE);
    messenger = msger;
  }

  @Override
  public PlatformView create(Context context, int id, Object obj) {
    return new SimpleViewControl(context, id, messenger);
  }

}
