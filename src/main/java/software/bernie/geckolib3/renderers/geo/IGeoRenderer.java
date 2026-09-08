package software.bernie.geckolib3.renderers.geo;

import javax.vecmath.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.controller.IAdvController;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.geo.render.built.GeoQuad;
import software.bernie.geckolib3.geo.render.built.GeoVertex;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.particles.emitter.BedrockEmitter;
import software.bernie.geckolib3.util.MatrixStack;
import software.bernie.geckolib3.util.PositionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface IGeoRenderer<T> {
	MatrixStack MATRIX_STACK = new MatrixStack();

	default void render(GeoModel model, T animatable, float partialTicks, float red, float green, float blue,
			float alpha) {
		//GlStateManager.enableCull();
		GlStateManager.enableRescaleNormal();
		renderEarly(animatable, partialTicks, red, green, blue, alpha);

		renderLate(animatable, partialTicks, red, green, blue, alpha);

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		BufferBuilder builder = Tessellator.getInstance().getBuffer();

		// Pass 1: Render opaque bones with depth writing
		GlStateManager.depthMask(true);
		builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
		for (GeoBone group : model.topLevelBones) {
			renderRecursively(builder, group, red, green, blue, alpha, true);
		}
		Tessellator.getInstance().draw();

		// Pass 2: Render transparent bones without depth writing so they don't occlude
		GlStateManager.depthMask(false);
		builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
		for (GeoBone group : model.topLevelBones) {
			renderRecursively(builder, group, red, green, blue, alpha, false);
		}
		Tessellator.getInstance().draw();

		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		renderAfter(animatable, partialTicks, red, green, blue, alpha);
		drawParticles(model, animatable, partialTicks);
		GlStateManager.disableRescaleNormal();
	}

	default void renderRecursively(BufferBuilder builder, GeoBone bone, float red, float green, float blue,
			float alpha) {
		renderRecursively(builder, bone, red, green, blue, alpha, true);
	}

	default void renderRecursively(BufferBuilder builder, GeoBone bone, float red, float green, float blue,
			float alpha, boolean opaquePass) {
		float boneAlpha = alpha * bone.getAlpha();
		if (boneAlpha <= 0) {
			return;
		}

		MATRIX_STACK.push();

		MATRIX_STACK.translate(bone);
		MATRIX_STACK.moveToPivot(bone);
		MATRIX_STACK.rotate(bone);
		MATRIX_STACK.scale(bone);
		MATRIX_STACK.moveBackFromPivot(bone);

		boolean isTransparent = boneAlpha < 1;
		boolean renderCubes = opaquePass != isTransparent;

		if (renderCubes && !bone.isHidden()) {
			for (GeoCube cube : bone.childCubes) {
				MATRIX_STACK.push();
				GlStateManager.pushMatrix();
				renderCube(builder, cube, red, green, blue, boneAlpha);
				GlStateManager.popMatrix();
				MATRIX_STACK.pop();
			}
		}
		if (!bone.childBonesAreHiddenToo()) {
			for (GeoBone childBone : bone.childBones) {
				renderRecursively(builder, childBone, red, green, blue, boneAlpha, opaquePass);
			}
		}

		MATRIX_STACK.pop();
	}

	default void renderCube(BufferBuilder builder, GeoCube cube, float red, float green, float blue, float alpha) {
		MATRIX_STACK.moveToPivot(cube);
		MATRIX_STACK.rotate(cube);
		MATRIX_STACK.moveBackFromPivot(cube);

		for (GeoQuad quad : cube.quads) {
			Vector3f normal = new Vector3f(quad.normal.getX(), quad.normal.getY(), quad.normal.getZ());

			MATRIX_STACK.getNormalMatrix().transform(normal);

			/*
			 * Fix shading dark shading for flat cubes + compatibility wish Optifine shaders
			 */
			if ((cube.size.y == 0 || cube.size.z == 0) && normal.getX() < 0) {
				normal.x *= -1;
			}
			if ((cube.size.x == 0 || cube.size.z == 0) && normal.getY() < 0) {
				normal.y *= -1;
			}
			if ((cube.size.x == 0 || cube.size.y == 0) && normal.getZ() < 0) {
				normal.z *= -1;
			}

			for (GeoVertex vertex : quad.vertices) {
				Vector4f vector4f = new Vector4f(vertex.position.getX(), vertex.position.getY(), vertex.position.getZ(),
						1.0F);

				MATRIX_STACK.getModelMatrix().transform(vector4f);

				builder.pos(vector4f.getX(), vector4f.getY(), vector4f.getZ()).tex(vertex.textureU, vertex.textureV)
						.color(red, green, blue, alpha).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();
			}
		}
	}

	@SuppressWarnings("rawtypes")
	GeoModelProvider getGeoModelProvider();

	ResourceLocation getTextureLocation(T instance);

	default void renderEarly(T animatable, float ticks, float red, float green, float blue, float partialTicks) {
	}

	default void renderLate(T animatable, float ticks, float red, float green, float blue, float partialTicks) {
	}

	default void renderAfter(T animatable, float ticks, float red, float green, float blue, float partialTicks) {
	}

	default Color getRenderColor(T animatable, float partialTicks) {
		return Color.ofRGBA(255, 255, 255, 255);
	}

	default Integer getUniqueID(T animatable) {
		return animatable.hashCode();
	}

	static void drawParticles(GeoModel model, Object animatableArg, float ticks) {
		if (!(animatableArg instanceof IAnimatable)) {
            return;
        }
		IAnimatable animatable = (IAnimatable) animatableArg;
		Map<String, AnimationController> controllerMap = animatable.getFactory().getOrCreateAnimationData(animatableArg.hashCode()).getAnimationControllers();
		for (AnimationController controller : controllerMap.values()) {
			List<BedrockEmitter> emitters = ((IAdvController)controller).getEmitters();
			for (BedrockEmitter emitter : emitters) {
				String locator = emitter.locator + "_locator";
				if (emitter.locator != null && model.getBone(locator).isPresent()) {
					GeoBone bone = model.getBone(locator).get();
					renderParticle(emitter, bone, ticks);
				}
			}
		}
	}

	static void renderParticle(BedrockEmitter emitter, GeoBone locator, float ticks) {
		emitter.prevGlobal.x = emitter.lastGlobal.x;
		emitter.prevGlobal.y = emitter.lastGlobal.y;
		emitter.prevGlobal.z = emitter.lastGlobal.z;

		Vector3d position = PositionUtils.getCurrentRenderPos();
		//Vector3d position = new Vector3d(0,0,0);
		double posX = position.x;//-376.5;
		double posY = position.y;//8;
		double posZ = position.z;//569.5;

		emitter.lastGlobal.x = posX; //TODO
		emitter.lastGlobal.y = posY; //TODO
		emitter.lastGlobal.z = posZ; //TODO
		RenderHelper.disableStandardItemLighting();

		Matrix4f curRot = PositionUtils.getCurrentMatrix();

		PositionUtils.setInitialWorldPos();

		Matrix4f cur2 = PositionUtils.getCurrentRotation(curRot, PositionUtils.getCurrentMatrix());

		emitter.rotation.setIdentity();

		MATRIX_STACK.push();
		MATRIX_STACK.getModelMatrix().mul(new Matrix4f(cur2.m00,cur2.m01,cur2.m02,0, cur2.m10,cur2.m11,cur2.m12,0,cur2.m20,cur2.m21,cur2.m22,0,0,0,0,1));
		GeoBone[] bonePath = getPathFromRoot(locator);
        for (GeoBone bone : bonePath) {
            MATRIX_STACK.translate(bone);
            MATRIX_STACK.moveToPivot(bone);
            MATRIX_STACK.rotate(bone);
            MATRIX_STACK.scale(bone);
            MATRIX_STACK.moveBackFromPivot(bone);
        }
		MATRIX_STACK.moveToPivot(locator);
		//MATRIX_STACK.translate(6f/16f,16f/16f,0);
		//MATRIX_STACK.rotateX((float) (Math.PI/2));
		//MATRIX_STACK.scale(0.5f,0.5f,0.5f);

		Matrix4f full = MATRIX_STACK.getModelMatrix();
		emitter.rotation = new Matrix3f(full.m00, full.m01, full.m02, full.m10, full.m11, full.m12, full.m20, full.m21, full.m22);
		emitter.lastGlobal.x += full.m03;
		emitter.lastGlobal.y += full.m13;
		emitter.lastGlobal.z += full.m23;

		MATRIX_STACK.pop();
		emitter.render(Minecraft.getMinecraft().getRenderPartialTicks());
		RenderHelper.enableStandardItemLighting();
	}

	static GeoBone[] getPathFromRoot(GeoBone bone) {
		List<GeoBone> bones = new ArrayList<>();
		while (bone != null) {
			bones.add(0, bone);
			bone = bone.parent;
		}
		return bones.toArray(new GeoBone[0]);
	}
}
