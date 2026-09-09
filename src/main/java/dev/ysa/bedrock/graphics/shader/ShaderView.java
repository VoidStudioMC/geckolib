package dev.ysa.bedrock.graphics.shader;

import com.google.common.base.Suppliers;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

import static org.lwjgl.opengl.GL20.*;

public final class ShaderView {
    public static final ShaderView EMPTY = new ShaderView(0);

    private int handle;
    private final Supplier<ShaderType> lazyType = Suppliers.memoize(() -> {
        if (handle == 0) {
            return ShaderType.UNKNOWN;
        }
        int glConst = glGetShaderi(handle, GL_SHADER_TYPE);
        return ShaderType.from(glConst).orElse(ShaderType.UNKNOWN);
    });

    public ShaderView(int handle) {
        this.handle = handle;
    }

    public int handle() {
        return handle;
    }

    public @NonNull ShaderType type() {
        return lazyType.get();
    }
}