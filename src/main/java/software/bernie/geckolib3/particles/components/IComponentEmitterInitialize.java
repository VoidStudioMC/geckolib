package software.bernie.geckolib3.particles.components;

import software.bernie.geckolib3.particles.emitter.BedrockEmitter;

public interface IComponentEmitterInitialize extends IComponentBase {
    void apply(BedrockEmitter emitter);
}