package software.bernie.geckolib3.particles.components;

import software.bernie.geckolib3.particles.emitter.BedrockEmitter;

public interface IComponentEmitterUpdate extends IComponentBase {
    void update(BedrockEmitter emitter);
}