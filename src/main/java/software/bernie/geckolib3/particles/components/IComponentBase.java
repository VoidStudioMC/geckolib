package software.bernie.geckolib3.particles.components;

public interface IComponentBase {
    default int getSortingIndex() {
        return 0;
    }
}