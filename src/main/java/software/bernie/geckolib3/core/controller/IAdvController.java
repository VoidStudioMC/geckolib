package software.bernie.geckolib3.core.controller;

import software.bernie.geckolib3.particles.emitter.BedrockEmitter;

import java.util.List;

public interface IAdvController {
    List<BedrockEmitter> getEmitters();

    long getLastTick();
    void setLastTick(long tick);
}
