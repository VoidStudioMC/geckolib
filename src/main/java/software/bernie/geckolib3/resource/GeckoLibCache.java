package software.bernie.geckolib3.resource;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.eliotlash.molang.MolangParser;

import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.FileResourcePack;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.LegacyV2Adapter;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.FMLFolderResourcePack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.file.AnimationFile;
import software.bernie.geckolib3.file.AnimationFileLoader;
import software.bernie.geckolib3.file.GeoModelLoader;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.molang.MolangRegistrar;

@SuppressWarnings("deprecation")
public class GeckoLibCache implements IResourceManagerReloadListener {
	private static GeckoLibCache INSTANCE;

	private final AnimationFileLoader animationLoader;
	private final GeoModelLoader modelLoader;

	public final MolangParser parser = new MolangParser();

	public HashMap<ResourceLocation, AnimationFile> getAnimations() {
		if (!GeckoLib.hasInitialized) {
			throw new RuntimeException("GeckoLib was never initialized! Please read the documentation!");
		}
		return animations;
	}

	public HashMap<ResourceLocation, GeoModel> getGeoModels() {
		if (!GeckoLib.hasInitialized) {
			throw new RuntimeException("GeckoLib was never initialized! Please read the documentation!");
		}
		return geoModels;
	}

	private HashMap<ResourceLocation, AnimationFile> animations = new HashMap<>();
	private HashMap<ResourceLocation, GeoModel> geoModels = new HashMap<>();

	protected GeckoLibCache() {
		this.animationLoader = new AnimationFileLoader();
		this.modelLoader = new GeoModelLoader();
		MolangRegistrar.registerVars(parser);
	}

	public static GeckoLibCache getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new GeckoLibCache();
			return INSTANCE;
		}
		return INSTANCE;
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		if (!Loader.instance().hasReachedState(LoaderState.PREINITIALIZATION)) {
			GeckoLib.LOGGER.info("Too early to reload GeckoLib cache, skipping...");
			return;
		}
		GeckoLib.LOGGER.info("Reloading GeckoLib caches with {}", resourceManager);
		HashMap<ResourceLocation, AnimationFile> tempAnimations = new HashMap<>();
		HashMap<ResourceLocation, GeoModel> tempModels = new HashMap<>();
		List<IResourcePack> packs = FMLClientHandler.instance().getResourcePackList();

		if (packs == null) {
			return;
		}

		for (IResourcePack pack : packs) {
			for (ResourceLocation location : this.getLocations(pack, "animations",
					fileName -> fileName.endsWith(".json"))) {
				try {
					tempAnimations.put(location, animationLoader.loadAllAnimations(parser, location, resourceManager));
				} catch (Exception e) {
					GeckoLib.LOGGER.debug("Error loading animation file \"" + location + "\"!", e);
				}
			}

			for (ResourceLocation location : this.getLocations(pack, "geo", fileName -> fileName.endsWith(".json"))) {
				try {
					tempModels.put(location, modelLoader.loadModel(resourceManager, location));
				} catch (Exception e) {
					GeckoLib.LOGGER.debug("Error loading model file \"" + location + "\"!", e);
				}
			}
		}

		animations = tempAnimations;
		geoModels = tempModels;
	}

    private List<ResourceLocation> getLocations(IResourcePack pack, String folder, Predicate<String> predicate) {
		if (pack instanceof LegacyV2Adapter adapter) {
            return this.getLocations(adapter.getUnadaptedPack(), folder, predicate);
		}

		List<ResourceLocation> locations = new ArrayList<>();

		if (pack instanceof FolderResourcePack folderResourcePack) {
			this.handleFolderResourcePack(folderResourcePack, folder, predicate, locations);
		} else if (pack instanceof FileResourcePack fileResourcePack) {
			this.handleZipResourcePack(fileResourcePack, folder, predicate, locations);
		}

		return locations;
	}

	/* Folder handling */

	private void handleFolderResourcePack(FolderResourcePack folderPack, String folder, Predicate<String> predicate,
			List<ResourceLocation> locations) {
        File file = folderPack.getResourcePackFile();
        Set<String> domains = folderPack.getResourceDomains();

        if (folderPack instanceof FMLFolderResourcePack fmlFolderResourcePack) {
            domains.add(fmlFolderResourcePack.getFMLContainer().getModId());
        }

        for (String domain : domains) {
            String prefix = "assets/" + domain + "/" + folder;
            File pathFile = new File(file, prefix);

            this.enumerateFiles(folderPack, pathFile, predicate, locations, domain, folder);
        }

    }

	private void enumerateFiles(FolderResourcePack folderPack, File parent, Predicate<String> predicate,
			List<ResourceLocation> locations, String domain, String prefix) {
		File[] files = parent.listFiles();

		if (files == null) {
			return;
		}

		for (File file : files) {
			if (file.isFile() && predicate.test(file.getName())) {
				locations.add(new ResourceLocation(domain, prefix + "/" + file.getName()));
			} else if (file.isDirectory()) {
				this.enumerateFiles(folderPack, file, predicate, locations, domain, prefix + "/" + file.getName());
			}
		}
	}

	/* Zip handling */

	private void handleZipResourcePack(FileResourcePack filePack, String folder, Predicate<String> predicate,
			List<ResourceLocation> locations) {
        try {
            this.enumerateZipFile(filePack, folder, filePack.getResourcePackZipFile(), predicate, locations);
        } catch (IOException e) {
            GeckoLib.LOGGER.error(e);
        }

	}

	private void enumerateZipFile(FileResourcePack filePack, String folder, ZipFile file, Predicate<String> predicate,
			List<ResourceLocation> locations) {
		Set<String> domains = filePack.getResourceDomains();
		Enumeration<? extends ZipEntry> it = file.entries();

		while (it.hasMoreElements()) {
			String name = it.nextElement().getName();

			for (String domain : domains) {
				String assets = "assets/" + domain + "/";
				String path = assets + folder + "/";

				if (name.startsWith(path) && predicate.test(name)) {
					locations.add(new ResourceLocation(domain, name.substring(assets.length())));
				}
			}
		}
	}
}
