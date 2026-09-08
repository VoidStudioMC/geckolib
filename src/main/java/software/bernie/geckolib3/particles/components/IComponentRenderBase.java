package software.bernie.geckolib3.particles.components;

import net.minecraft.client.renderer.BufferBuilder;
import software.bernie.geckolib3.particles.emitter.BedrockEmitter;
import software.bernie.geckolib3.particles.emitter.BedrockParticle;

public interface IComponentRenderBase extends IComponentBase {
    void render(BedrockEmitter emitter, BedrockParticle particle, BufferBuilder builder, float partialTicks);

    void renderOnScreen(BedrockParticle particle, int x, int y, float scale, float partialTicks);

    void preRender(BedrockEmitter emitter, float partialTicks);

    void postRender(BedrockEmitter emitter, float partialTicks);
}
