package software.bernie.example.client.model;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.example.block.tile.TileMagicTorch;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class MagicTorchModel extends AnimatedGeoModel<TileMagicTorch> {
    @Override
    public ResourceLocation getAnimationFileLocation(TileMagicTorch entity) {
        return new ResourceLocation(GeckoLib.ModID, "animations/magic_torch.animation.json");
    }

    @Override
    public ResourceLocation getModelLocation(TileMagicTorch animatable) {
        return new ResourceLocation(GeckoLib.ModID, "geo/magic_torch.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(TileMagicTorch entity) {
        return new ResourceLocation(GeckoLib.ModID, "textures/block/magic_torch.png");
    }
}