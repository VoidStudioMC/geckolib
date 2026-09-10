package software.bernie.geckolib3.renderers.geo;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import net.minecraft.client.renderer.BufferBuilder;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoQuad;
import software.bernie.geckolib3.geo.render.built.GeoVertex;

public final class CubeRenderCommand {
    private GeoCube cube;
    private final Matrix4f modelMatrix = new Matrix4f();
    private final Matrix3f normalMatrix = new Matrix3f();
    private float red;
    private float green;
    private float blue;
    private float alpha;

    public void set(GeoCube cube, Matrix4f modelMatrix, Matrix3f normalMatrix, float red, float green, float blue,
             float alpha) {
        this.cube = cube;
        this.modelMatrix.set(modelMatrix);
        this.normalMatrix.set(normalMatrix);
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public void render(BufferBuilder builder, Vector3f tempNormal, Vector4f tempVertex) {
        for (GeoQuad quad : cube.quads) {
            tempNormal.set(quad.normal.getX(), quad.normal.getY(), quad.normal.getZ());
            normalMatrix.transform(tempNormal);

            if ((cube.size.y == 0 || cube.size.z == 0) && tempNormal.getX() < 0) {
                tempNormal.x *= -1;
            }
            if ((cube.size.x == 0 || cube.size.z == 0) && tempNormal.getY() < 0) {
                tempNormal.y *= -1;
            }
            if ((cube.size.x == 0 || cube.size.y == 0) && tempNormal.getZ() < 0) {
                tempNormal.z *= -1;
            }

            for (GeoVertex vertex : quad.vertices) {
                tempVertex.set(vertex.position.getX(), vertex.position.getY(), vertex.position.getZ(), 1.0F);
                modelMatrix.transform(tempVertex);

                builder.pos(tempVertex.getX(), tempVertex.getY(), tempVertex.getZ())
                        .tex(vertex.textureU, vertex.textureV)
                        .color(red, green, blue, alpha)
                        .normal(tempNormal.getX(), tempNormal.getY(), tempNormal.getZ())
                        .endVertex();
            }
        }
    }
}