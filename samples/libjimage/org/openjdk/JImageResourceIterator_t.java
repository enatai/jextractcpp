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
 * typedef void (*JImageResourceIterator_t)(JImageFile *, JImageResourceVisitor_t, void *)
 * }
 */
public interface JImageResourceIterator_t {

    void apply(MemorySegment jimage, MemorySegment visitor, MemorySegment arg);

    FunctionDescriptor $DESC = FunctionDescriptor.ofVoid(
        jimage_h.C_POINTER,
        jimage_h.C_POINTER,
        jimage_h.C_POINTER
    );

    MethodHandle UP$MH = jimage_h.upcallHandle(JImageResourceIterator_t.class, "apply", $DESC);

    static MemorySegment allocate(JImageResourceIterator_t fi, Arena scope) {
        return Linker.nativeLinker().upcallStub(UP$MH.bindTo(fi), $DESC, scope);
    }

    MethodHandle DOWN$MH = Linker.nativeLinker().downcallHandle($DESC);

    static JImageResourceIterator_t ofAddress(MemorySegment addr, Arena arena) {
        MemorySegment symbol = addr.reinterpret(arena, null);
        return (MemorySegment _jimage, MemorySegment _visitor, MemorySegment _arg) -> {
            try {
                 DOWN$MH.invokeExact(symbol, _jimage, _visitor, _arg);
            } catch (Throwable ex$) {
                throw new AssertionError("should not reach here", ex$);
            }
        };
    }
}

