package dev.ysa.bedrock.graphics.shader;

public class ShaderUniform {
    private final int location;
    private final String name;
    private final int length;
    private final int size;
    private final ShaderUniformType type;

    public ShaderUniform(int location, String name, int length, int size, int type) {
        this(location, name, length, size, ShaderUniformType.from(type));
    }

    public ShaderUniform(int location, String name, int length, int size, ShaderUniformType type) {
        this.location = location;
        this.name = name;
        this.length = length;
        this.size = size;
        this.type = type;
    }

    public int location() {
        return location;
    }

    public String name() {
        return name;
    }

    public ShaderUniformType type() {
        return type;
    }

    @Override
    public String toString() {
        return "ShaderUniform{" +
                "location=" + location +
                ", name='" + name + '\'' +
                ", length=" + length +
                ", size=" + size +
                ", type=" + type +
                '}';
    }
}