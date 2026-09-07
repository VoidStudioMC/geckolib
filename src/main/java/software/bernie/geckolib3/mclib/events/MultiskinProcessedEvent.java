package software.bernie.geckolib3.mclib.events;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import software.bernie.geckolib3.mclib.utils.resources.MultiResourceLocation;

import java.awt.image.BufferedImage;

@SideOnly(Side.CLIENT)
public class MultiskinProcessedEvent extends Event {
    public MultiResourceLocation location;
    public BufferedImage image;

    public MultiskinProcessedEvent(MultiResourceLocation location, BufferedImage image) {
        this.location = location;
        this.image = image;
    }
}
