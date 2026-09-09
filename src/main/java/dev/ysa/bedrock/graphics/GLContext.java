package dev.ysa.bedrock.graphics;

import dev.ysa.bedrock.graphics.shader.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import org.jspecify.annotations.NonNull;
import org.lwjgl.BufferUtils;
import org.lwjgl.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;

public class GLContext implements AutoCloseable {
    private final Logger logger = LoggerFactory.getLogger("GLContext");

    private final Map<String, ShaderView> shaderViews = new Object2ObjectArrayMap<>();
    private final Map<String, ShaderProgram> programViews = new Object2ObjectArrayMap<>();
    private final Map<String, List<ShaderUniform>> programUniforms = new Object2ObjectArrayMap<>();

    public void useProgram(String name) {
        if (!programViews.containsKey(name)) {
            return;
        }
        ShaderProgram shaderProgram = programViews.get(name);
        glUseProgram(shaderProgram.handle());
    }

    public void unuseProgram() {
        glUseProgram(0);
    }

    public ShaderView compileShader(String name, @NonNull ShaderType type, CharSequence source) {
        int handle = glCreateShader(type.getGlConst());

        if (!glIsShader(handle)) {
            logger.error("Invalid shader object, name: {}", handle);
            return ShaderView.EMPTY;
        }

        glShaderSource(handle, source);
        glCompileShader(handle);

        int compileStatus = glGetShaderi(handle, GL_COMPILE_STATUS);

        if (compileStatus != GL_TRUE) {
            int infoLogLength = glGetShaderi(handle, GL_INFO_LOG_LENGTH);
            String infoLog = glGetShaderInfoLog(handle, infoLogLength);

            logger.error("Shader is not compiled, info: {}", infoLog);

            glDeleteShader(handle);
            return ShaderView.EMPTY;
        }

        ShaderView shaderView = new ShaderView(handle);
        shaderViews.put(String.format("%s_%s", name, type.name().toLowerCase()), shaderView);
        return shaderView;
    }

    public ShaderProgram createProgram(String name, List<String> shaderNames) {
        int handle = glCreateProgram();

        if (!glIsProgram(handle)) {
            logger.error("Invalid program object, name: {}", handle);
            return ShaderProgram.EMPTY;
        }

        for (String shaderName : shaderNames) {
            ShaderView view = shaderViews.get(shaderName);

            if (view == null) {
                continue;
            }

            glAttachShader(handle, view.handle());
        }

        glLinkProgram(handle);

        int linkStatus = glGetProgrami(handle, GL_LINK_STATUS);
        if (linkStatus != GL_TRUE) {
            int infoLogLength = glGetProgrami(handle, GL_INFO_LOG_LENGTH);
            String infoLog = glGetProgramInfoLog(handle, infoLogLength);
            logger.error("Program link error, info: {}", infoLog);
            glDeleteProgram(handle);
            return ShaderProgram.EMPTY;
        }

        int activeUniforms = glGetProgrami(handle, GL_ACTIVE_UNIFORMS);
        if (activeUniforms != 0) {
            int maxLength = glGetProgrami(handle, GL_ACTIVE_UNIFORM_MAX_LENGTH);

            IntBuffer length = BufferUtils.createIntBuffer(1);
            IntBuffer size = BufferUtils.createIntBuffer(1);
            IntBuffer type = BufferUtils.createIntBuffer(1);

            for (int index = 0; index < activeUniforms; ++index) {
                ByteBuffer uniformName = BufferUtils.createByteBuffer(maxLength);
                glGetActiveUniform(handle, index, length, size, type, uniformName);

                int location = glGetUniformLocation(handle, uniformName);

                List<ShaderUniform> uniforms = programUniforms.putIfAbsent(name, new ArrayList<>());
                if (uniforms != null) {
                    ShaderUniform uniform = new ShaderUniform(location, MemoryUtil.decodeUTF8(uniformName), length.get(0), size.get(0), type.get(0));
                    uniforms.add(uniform);
                    logger.info("Uniform view data: {}", uniform);
                }
            }
        }

        ShaderProgram shaderProgram = new ShaderProgram(handle);
        programViews.put(name, shaderProgram);
        return shaderProgram;
    }

    @Override
    public void close() throws Exception {
        for (ShaderProgram shaderProgram : programViews.values()) {
            Collection<ShaderView> attachedShaders = shaderProgram.attachedShaders();

            for (ShaderView shaderView : attachedShaders) {
                glDetachShader(shaderProgram.handle(), shaderView.handle());
                glDeleteShader(shaderView.handle());
            }

            glDeleteProgram(shaderProgram.handle());
        }
    }

    private void updateUniform(String program, String name) {
        List<ShaderUniform> uniforms = programUniforms.get(program);
        for (ShaderUniform uniform : uniforms) {
            if (uniform.name().equalsIgnoreCase(name)) {
                uniform.type();
                break;
            }
        }
    }
}