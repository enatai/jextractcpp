// Generated by jextract

package org.openjdk;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.lang.foreign.*;
import static java.lang.foreign.ValueLayout.*;

/**
 * {@snippet lang=c :
 * typedef jlong (*JImageGetResource_t)(JImageFile *, JImageLocationRef, char *, jlong)
 * }
 */
public interface JImageGetResource_t {

    long apply(MemorySegment jimage, long location, MemorySegment buffer, long size);

    FunctionDescriptor $DESC = FunctionDescriptor.of(
        jimage_h.C_LONG_LONG,
        jimage_h.C_POINTER,
        jimage_h.C_LONG_LONG,
        jimage_h.C_POINTER,
        jimage_h.C_LONG_LONG
    );

    MethodHandle UP$MH = jimage_h.upcallHandle(JImageGetResource_t.class, "apply", $DESC);

    static MemorySegment allocate(JImageGetResource_t fi, Arena scope) {
        return Linker.nativeLinker().upcallStub(UP$MH.bindTo(fi), $DESC, scope);
    }

    MethodHandle DOWN$MH = Linker.nativeLinker().downcallHandle($DESC);

    static JImageGetResource_t ofAddress(MemorySegment addr, Arena arena) {
        MemorySegment symbol = addr.reinterpret(arena, null);
        return (MemorySegment _jimage, long _location, MemorySegment _buffer, long _size) -> {
            try {
                return (long) DOWN$MH.invokeExact(symbol, _jimage, _location, _buffer, _size);
            } catch (Throwable ex$) {
                throw new AssertionError("should not reach here", ex$);
            }
        };
    }
}

