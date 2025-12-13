package software.bernie.example;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class GeckoLibConfig {
    public static boolean enableExampleMod;
    
    public static final File configFile = new File(new File(Launch.minecraftHome, "config"), "saurialib.cfg");
    private static final Configuration forgeConfig = new Configuration(configFile);
    private static final String categoryGeneral = "General";

    public static void syncConfig() {
        enableExampleMod = forgeConfig.getBoolean("Enable Example Mod", categoryGeneral, false, "Enable the test example mod in GeckoLib");
        
        if (forgeConfig.hasChanged()) {
            forgeConfig.save();
        }
    }
}
