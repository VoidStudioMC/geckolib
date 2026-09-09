package dev.ysa.bedrock.graphics.shader;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

public final class ShaderProgram {
    public static final ShaderProgram EMPTY = new ShaderProgram(0);

    private int handle;

    private final Map<String, Integer> uniforms = new Object2IntArrayMap<>();
    private final EnumMap<ShaderType, ShaderView> attachedShaders = new EnumMap<>(ShaderType.class);

    public ShaderProgram(int handle) {
        this.handle = handle;
    }

    public int handle() {
        return handle;
    }

    @Contract(pure = true)
    public @NonNull Collection<ShaderView> attachedShaders() {
        return attachedShaders.values();
    }
}