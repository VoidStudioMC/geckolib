package dev.ysa.bedrock.graphics.shader;

import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.Optional;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

public enum ShaderType {
    UNKNOWN(0),
    VERTEX(GL_VERTEX_SHADER),
    FRAGMENT(GL_FRAGMENT_SHADER);

    public static final ShaderType[] entries = values();

    private final int glConst;

    ShaderType(int glConst) {
        this.glConst = glConst;
    }

    public int getGlConst() {
        return glConst;
    }

    public static @NonNull Optional<ShaderType> from(int glConst) {
        return Arrays.stream(entries).filter(shaderType -> shaderType.getGlConst() == glConst).findFirst();
    }
}