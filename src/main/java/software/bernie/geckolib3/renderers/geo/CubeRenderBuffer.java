package software.bernie.geckolib3.renderers.geo;

import java.util.ArrayList;
import java.util.List;

public final class CubeRenderBuffer {
    public final List<CubeRenderCommand> commands = new ArrayList<>();
    public int count = 0;

    public CubeRenderCommand next() {
        if (count < commands.size()) {
            return commands.get(count++);
        }

        CubeRenderCommand command = new CubeRenderCommand();
        commands.add(command);
        count++;
        return command;
    }

    public void reset() {
        count = 0;
    }
}