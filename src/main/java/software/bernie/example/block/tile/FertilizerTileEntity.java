package software.bernie.example.block.tile;

import net.minecraft.tileentity.TileEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class FertilizerTileEntity extends TileEntity implements IAnimatable {
	private final AnimationFactory manager = new AnimationFactory(this);

	private <E extends TileEntity & IAnimatable> PlayState predicate(AnimationEvent<E> event) {
		event.getController().transitionLengthTicks = 0;
		if (event.getAnimatable().getWorld().isRaining()) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("fertilizer.animation.deploy", ILoopType.EDefaultLoopTypes.LOOP)
					.addAnimation("fertilizer.animation.idle", ILoopType.EDefaultLoopTypes.LOOP));
		} else {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("Botarium.anim.deploy", ILoopType.EDefaultLoopTypes.LOOP)
					.addAnimation("Botarium.anim.idle", ILoopType.EDefaultLoopTypes.LOOP));
		}
		return PlayState.CONTINUE;
	}
 
	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
	}

	@Override
	public AnimationFactory getFactory() {
		return this.manager;
	}
}
