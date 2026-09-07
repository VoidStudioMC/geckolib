package software.bernie.example.client.model;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.example.entity.ParticleExampleEntity;
import software.bernie.geckolib3.model.AnimatedTickingGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class ParticleExampleEntityModel extends AnimatedTickingGeoModel<ParticleExampleEntity> {
    @Override
    public ResourceLocation getAnimationFileLocation(ParticleExampleEntity entity) {
        return new ResourceLocation(GeckoLib.ModID, "animations/bat.animation.json");
    }

    @Override
    public ResourceLocation getModelLocation(ParticleExampleEntity entity) {
        return new ResourceLocation(GeckoLib.ModID, "geo/bat.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(ParticleExampleEntity entity) {
        return new ResourceLocation(GeckoLib.ModID, "textures/entity/bat.png");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void setLivingAnimations(ParticleExampleEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
        head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
    }
}
