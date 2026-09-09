package dev.ysa.bedrock.graphics.shader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;

public enum ShaderUniformType {
    UNKNOWN(0, "unknown"),
    FLOAT(GL_FLOAT, "float"),
    FLOAT_VEC2(GL_FLOAT_VEC2, "vec2"),
    FLOAT_VEC3(GL_FLOAT_VEC3, "vec3"),
    FLOAT_VEC4(GL_FLOAT_VEC4, "vec4"),
    INT(GL_INT, "int"),
    INT_VEC2(GL_INT_VEC2, "ivec2"),
    INT_VEC3(GL_INT_VEC3, "ivec3"),
    INT_VEC4(GL_INT_VEC4, "ivec4"),
    UNSIGNED_INT(GL_UNSIGNED_INT, "unsigned int"),
    UNSIGNED_INT_VEC2(GL_UNSIGNED_INT_VEC2, "uvec2"),
    UNSIGNED_INT_VEC3(GL_UNSIGNED_INT_VEC3, "uvec3"),
    UNSIGNED_INT_VEC4(GL_UNSIGNED_INT_VEC4, "uvec4"),
    BOOL(GL_BOOL, "bool"),
    BOOL_VEC2(GL_BOOL_VEC2, "bvec2"),
    BOOL_VEC3(GL_BOOL_VEC3, "bvec3"),
    BOOL_VEC4(GL_BOOL_VEC4, "bvec4"),
    FLOAT_MAT2(GL_FLOAT_MAT2, "mat2"),
    FLOAT_MAT3(GL_FLOAT_MAT3, "mat3"),
    FLOAT_MAT4(GL_FLOAT_MAT4, "mat4"),
    FLOAT_MAT2x3(GL_FLOAT_MAT2x3, "mat2x3"),
    FLOAT_MAT2x4(GL_FLOAT_MAT2x4, "mat2x4"),
    FLOAT_MAT3x2(GL_FLOAT_MAT3x2, "mat3x2"),
    FLOAT_MAT3x4(GL_FLOAT_MAT3x4, "mat3x4"),
    FLOAT_MAT4x2(GL_FLOAT_MAT4x2, "mat4x2"),
    FLOAT_MAT4x3(GL_FLOAT_MAT4x3, "mat4x3"),
    SAMPLER_1D(GL_SAMPLER_1D, "sampler1D"),
    SAMPLER_2D(GL_SAMPLER_2D, "sampler2D"),
    SAMPLER_3D(GL_SAMPLER_3D, "sampler3D"),
    SAMPLER_CUBE(GL_SAMPLER_CUBE, "samplerCube"),
    SAMPLER_1D_SHADOW(GL_SAMPLER_1D_SHADOW, "sampler1DShadow"),
    SAMPLER_2D_SHADOW(GL_SAMPLER_2D_SHADOW, "sampler2DShadow"),
    SAMPLER_1D_ARRAY(GL_SAMPLER_1D_ARRAY, "sampler1DArray"),
    SAMPLER_2D_ARRAY(GL_SAMPLER_2D_ARRAY, "sampler2DArray"),
    SAMPLER_1D_ARRAY_SHADOW(GL_SAMPLER_1D_ARRAY_SHADOW, "sampler1DArrayShadow"),
    SAMPLER_2D_ARRAY_SHADOW(GL_SAMPLER_2D_ARRAY_SHADOW, "sampler2DArrayShadow"),
    SAMPLER_2D_MULTISAMPLE(GL_SAMPLER_2D_MULTISAMPLE, "sampler2DMS"),
    SAMPLER_2D_MULTISAMPLE_ARRAY(GL_SAMPLER_2D_MULTISAMPLE_ARRAY, "sampler2DMSArray"),
    SAMPLER_CUBE_SHADOW(GL_SAMPLER_CUBE_SHADOW, "samplerCubeShadow"),
    SAMPLER_BUFFER(GL_SAMPLER_BUFFER, "samplerBuffer"),
    SAMPLER_2D_RECT(GL_SAMPLER_2D_RECT, "sampler2DRect"),
    SAMPLER_2D_RECT_SHADOW(GL_SAMPLER_2D_RECT_SHADOW, "sampler2DRectShadow"),
    INT_SAMPLER_1D(GL_INT_SAMPLER_1D, "isampler1D"),
    INT_SAMPLER_2D(GL_INT_SAMPLER_2D, "isampler2D"),
    INT_SAMPLER_3D(GL_INT_SAMPLER_3D, "isampler3D"),
    INT_SAMPLER_CUBE(GL_INT_SAMPLER_CUBE, "isamplerCube"),
    INT_SAMPLER_1D_ARRAY(GL_INT_SAMPLER_1D_ARRAY, "isampler1DArray"),
    INT_SAMPLER_2D_ARRAY(GL_INT_SAMPLER_2D_ARRAY, "isampler2DArray"),
    INT_SAMPLER_2D_MULTISAMPLE(GL_INT_SAMPLER_2D_MULTISAMPLE, "isampler2DMS"),
    INT_SAMPLER_2D_MULTISAMPLE_ARRAY(GL_INT_SAMPLER_2D_MULTISAMPLE_ARRAY, "isampler2DMSArray"),
    INT_SAMPLER_BUFFER(GL_INT_SAMPLER_BUFFER, "isamplerBuffer"),
    INT_SAMPLER_2D_RECT(GL_INT_SAMPLER_2D_RECT, "isampler2DRect"),
    UNSIGNED_INT_SAMPLER_1D(GL_UNSIGNED_INT_SAMPLER_1D, "usampler1D"),
    UNSIGNED_INT_SAMPLER_2D(GL_UNSIGNED_INT_SAMPLER_2D, "usampler2D"),
    UNSIGNED_INT_SAMPLER_3D(GL_UNSIGNED_INT_SAMPLER_3D, "usampler3D"),
    UNSIGNED_INT_SAMPLER_CUBE(GL_UNSIGNED_INT_SAMPLER_CUBE, "usamplerCube"),
    UNSIGNED_INT_SAMPLER_1D_ARRAY(GL_UNSIGNED_INT_SAMPLER_1D_ARRAY, "usampler2DArray"),
    UNSIGNED_INT_SAMPLER_2D_ARRAY(GL_UNSIGNED_INT_SAMPLER_2D_ARRAY, "usampler2DArray"),
    UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE(GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE, "usampler2DMS"),
    UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE_ARRAY(GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE_ARRAY, "usampler2DMSArray"),
    UNSIGNED_INT_SAMPLER_BUFFER(GL_UNSIGNED_INT_SAMPLER_BUFFER, "usamplerBuffer"),
    UNSIGNED_INT_SAMPLER_2D_RECT(GL_UNSIGNED_INT_SAMPLER_2D_RECT, "usampler2DRect");

    private static final ShaderUniformType[] ENTRIES = values();

    private int glConst;
    private String glslName;

    ShaderUniformType(int glConst, String glslName) {
        this.glConst = glConst;
        this.glslName = glslName;
    }

    public int glConst() {
        return glConst;
    }

    public String glslName() {
        return glslName;
    }

    public static ShaderUniformType from(int glConst) {
        for (ShaderUniformType type : ENTRIES) {
            if (type.glConst == glConst) {
                return type;
            }
        }
        return UNKNOWN;
    }
}