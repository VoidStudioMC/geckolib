/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.example;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import software.bernie.example.block.tile.BotariumTileEntity;
import software.bernie.example.block.tile.FertilizerTileEntity;
import software.bernie.example.client.renderer.armor.PotatoArmorRenderer;
import software.bernie.example.client.renderer.entity.BikeGeoRenderer;
import software.bernie.example.client.renderer.entity.ExampleGeoRenderer;
import software.bernie.example.client.renderer.entity.LERenderer;
import software.bernie.example.client.renderer.entity.ReplacedCreeperRenderer;
import software.bernie.example.client.renderer.tile.BotariumTileRenderer;
import software.bernie.example.client.renderer.tile.FertilizerTileRenderer;
import software.bernie.example.entity.BikeEntity;
import software.bernie.example.entity.GeoExampleEntity;
import software.bernie.example.entity.GeoExampleEntityLayer;
import software.bernie.example.entity.ReplacedCreeperEntity;
import software.bernie.example.item.PotatoArmorItem;
import software.bernie.example.registry.ItemRegistry;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.example.client.renderer.MagicTorchTileRenderer;
import software.bernie.example.client.renderer.ParticleExampleEntityRenderer;
import software.bernie.example.entity.ParticleExampleEntity;
import software.bernie.example.block.tile.TileMagicTorch;
import software.bernie.geckolib3.particles.BedrockLibrary;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import software.bernie.geckolib3.renderers.geo.GeoReplacedEntityRenderer;

import java.io.File;

@Mod(modid = GeckoLib.ModID, version = GeckoLib.VERSION, dependencies = "required-after:cleanroom@[0.3.31-alpha,);")
public class GeckoLibMod {
	private static CreativeTabs geckolibItemGroup;
	public static BedrockLibrary particleLibraryInstance;

	public static CreativeTabs getGeckolibItemGroup() {
		if (geckolibItemGroup == null) {
			geckolibItemGroup = new CreativeTabs(CreativeTabs.getNextID(), "geckolib_examples") {
				@Override
				public ItemStack createIcon() {
					return new ItemStack(ItemRegistry.JACK_IN_THE_BOX);
				}
			};
		}

		return geckolibItemGroup;
	}

	public GeckoLibMod() {
		GeckoLibConfig.syncConfig();
		if (GeckoLibConfig.enableExampleMod) {
			MinecraftForge.EVENT_BUS.register(new CommonListener());
		}
		particleLibraryInstance = new BedrockLibrary(new File("./particle"));
		particleLibraryInstance.reload();
	}

	@SideOnly(Side.CLIENT)
	@Mod.EventHandler
	public void registerRenderers(FMLPreInitializationEvent event) {
		if (GeckoLibConfig.enableExampleMod) {
			RenderingRegistry.registerEntityRenderingHandler(GeoExampleEntityLayer.class,
					LERenderer::new);
			RenderingRegistry.registerEntityRenderingHandler(GeoExampleEntity.class, ExampleGeoRenderer::new);
			RenderingRegistry.registerEntityRenderingHandler(BikeEntity.class, BikeGeoRenderer::new);

			GeoArmorRenderer.registerArmorRenderer(PotatoArmorItem.class, new PotatoArmorRenderer());

			ClientRegistry.bindTileEntitySpecialRenderer(BotariumTileEntity.class, new BotariumTileRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(FertilizerTileEntity.class, new FertilizerTileRenderer());

			ClientRegistry.bindTileEntitySpecialRenderer(TileMagicTorch.class, new MagicTorchTileRenderer());
			RenderingRegistry.registerEntityRenderingHandler(ParticleExampleEntity.class, ParticleExampleEntityRenderer::new);
		}
	}

	@SideOnly(Side.CLIENT)
	@Mod.EventHandler
	public void registerReplacedRenderers(FMLInitializationEvent event) {
		if (GeckoLibConfig.enableExampleMod) {
			GeckoLib.initialize();
			RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
			ReplacedCreeperRenderer creeperRenderer = new ReplacedCreeperRenderer(renderManager);
			renderManager.entityRenderMap.put(EntityCreeper.class, creeperRenderer);
			GeoReplacedEntityRenderer.registerReplacedEntity(ReplacedCreeperEntity.class, creeperRenderer);
		}
	}
}
