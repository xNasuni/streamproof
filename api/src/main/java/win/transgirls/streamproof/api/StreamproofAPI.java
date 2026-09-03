package win.transgirls.streamproof.api;

import win.transgirls.streamproof.api.types.ComponentCategory;
import win.transgirls.streamproof.api.types.DummyImpl;
import win.transgirls.streamproof.api.types.Impl;
import win.transgirls.streamproof.api.types.RenderTarget;

import java.lang.reflect.Field;

public final class StreamproofAPI {
    private static final Impl fallbackImpl = new DummyImpl();
    private static Field implField = null;

    static {
        try {
            Class<?> streamproofClass = Class.forName("win.transgirls.streamproof.Streamproof");
            implField = streamproofClass.getDeclaredField("_global_impl");
            implField.setAccessible(true);
            if (!(implField.get(null) instanceof Impl)) {
                implField.set(null, fallbackImpl);
            }
        } catch (Throwable ignored) {
        }
    }

    private static Impl impl() {
        if (implField == null) {
            return fallbackImpl;
        }

        try {
            return (Impl) implField.get(null);
        } catch (Throwable ignored) {
            return fallbackImpl;
        }
    }

    public static boolean isAvailable() {
        return impl().isAvailable();
    }

    public static boolean isWriting() {
        return impl().isWriting();
    }

    public static boolean isReady() {
        return impl().isReady();
    }

    public static boolean isStreamproof(String id) {
        return impl().isStreamproof(id);
    }

    public static RenderTarget getRenderTarget(String id) {
        return impl().getRenderTarget(id);
    }

    public static void add(String id, String label, ComponentCategory category, boolean defaultStreamproof) {
        impl().add(id, label, category, defaultStreamproof);
    }

    public static void begin(String id) {
        if (!impl().isReady()) {
            return;
        }

        impl().begin(id);
    }

    public static void end(String id) {
        if (!impl().isReady()) {
            return;
        }

        impl().end(id);
    }

    public static void beginImmediate(String id) {
        if (!impl().isReady()) {
            return;
        }

        if (isStreamproof(id)) {
            start(getRenderTarget(id));
        }
    }

    public static void endImmediate(String id) {
        if (!impl().isReady()) {
            return;
        }

        if (isStreamproof(id)) {
            stop(getRenderTarget(id));
        }
    }

    public static void start(RenderTarget target) {
        impl()._startFbo(target);
    }

    public static void stop(RenderTarget target) {
        impl()._stopFbo(target);
    }

    public static Impl getImpl() {
        return impl();
    }
}