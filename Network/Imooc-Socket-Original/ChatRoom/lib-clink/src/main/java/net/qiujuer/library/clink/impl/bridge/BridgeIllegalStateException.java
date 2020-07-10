package net.qiujuer.library.clink.impl.bridge;

public class BridgeIllegalStateException extends IllegalStateException {
    public static void check(boolean status) {
        if (!status) {
            throw new BridgeIllegalStateException();
        }
    }
}
