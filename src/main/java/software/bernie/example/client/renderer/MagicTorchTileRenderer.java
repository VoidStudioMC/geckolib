package software.bernie.example.client.renderer;

import software.bernie.example.client.model.MagicTorchModel;
import software.bernie.example.block.tile.TileMagicTorch;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class MagicTorchTileRenderer extends GeoBlockRenderer<TileMagicTorch> {
    public MagicTorchTileRenderer() {
        super(new MagicTorchModel());
    }
}
