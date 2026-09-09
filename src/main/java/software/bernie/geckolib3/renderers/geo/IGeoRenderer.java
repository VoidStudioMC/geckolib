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

import java.util.List;
import java.util.Map;

public interface IGeoRenderer<T> {
	MatrixStack MATRIX_STACK = new MatrixStack();
	Matrix4f ROTATION_MAT = new Matrix4f();

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
		Map<String, AnimationController> controllerMap = animatable.getFactory()
				.getOrCreateAnimationData(animatableArg.hashCode())
				.getAnimationControllers();

		if (controllerMap.isEmpty()) {
			return;
		}

		for (AnimationController controller : controllerMap.values()) {
			List<BedrockEmitter> emitters = ((IAdvController) controller).getEmitters();
			if (emitters.isEmpty()) {
				continue;
			}
			for (BedrockEmitter emitter : emitters) {
				if (emitter.locator == null) {
					continue;
				}
				model.getBone(emitter.locator + "_locator")
						.ifPresent(bone -> renderParticle(emitter, bone, ticks));
			}
		}
	}

	static void renderParticle(BedrockEmitter emitter, GeoBone locator, float ticks) {
		Vector3d prev = emitter.prevGlobal;
		Vector3d last = emitter.lastGlobal;
		prev.x = last.x;
		prev.y = last.y;
		prev.z = last.z;

		Vector3d position = PositionUtils.getCurrentRenderPos();
		last.x = position.x;
		last.y = position.y;
		last.z = position.z;

		RenderHelper.disableStandardItemLighting();

		Matrix4f curRot = PositionUtils.getCurrentMatrix();
		PositionUtils.setInitialWorldPos();
		Matrix4f cur2 = PositionUtils.getCurrentRotation(curRot, PositionUtils.getCurrentMatrix());

		emitter.rotation.setIdentity();

		MATRIX_STACK.push();

		ROTATION_MAT.setIdentity();
		ROTATION_MAT.setElement(0, 0, cur2.m00);
		ROTATION_MAT.setElement(0, 1, cur2.m01);
		ROTATION_MAT.setElement(0, 2, cur2.m02);
		ROTATION_MAT.setElement(1, 0, cur2.m10);
		ROTATION_MAT.setElement(1, 1, cur2.m11);
		ROTATION_MAT.setElement(1, 2, cur2.m12);
		ROTATION_MAT.setElement(2, 0, cur2.m20);
		ROTATION_MAT.setElement(2, 1, cur2.m21);
		ROTATION_MAT.setElement(2, 2, cur2.m22);
		MATRIX_STACK.getModelMatrix().mul(ROTATION_MAT);

		applyBoneChainTransform(locator);

		MATRIX_STACK.moveToPivot(locator);

		Matrix4f full = MATRIX_STACK.getModelMatrix();
		emitter.rotation = new Matrix3f(full.m00, full.m01, full.m02, full.m10, full.m11, full.m12, full.m20, full.m21, full.m22);
		emitter.lastGlobal.x += full.m03;
		emitter.lastGlobal.y += full.m13;
		emitter.lastGlobal.z += full.m23;

		MATRIX_STACK.pop();
		emitter.render(Minecraft.getMinecraft().getRenderPartialTicks());
		//emitter.running = emitter.sanityTicks < 2;
		RenderHelper.enableStandardItemLighting();
	}

	static void applyBoneChainTransform(GeoBone bone) {
		if (bone == null) {
			return;
		}
		applyBoneChainTransform(bone.parent);
		MATRIX_STACK.translate(bone);
		MATRIX_STACK.moveToPivot(bone);
		MATRIX_STACK.rotate(bone);
		MATRIX_STACK.scale(bone);
		MATRIX_STACK.moveBackFromPivot(bone);
	}
}
