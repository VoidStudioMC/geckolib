package software.bernie.geckolib3.mclib.utils.resources;

import com.google.gson.JsonElement;
import net.minecraft.nbt.NBTBase;
import software.bernie.geckolib3.mclib.utils.ICopy;

public interface IWritableLocation<T> extends ICopy<T> {
    void fromNbt(NBTBase nbt) throws Exception;

    void fromJson(JsonElement element) throws Exception;

    NBTBase writeNbt();

    JsonElement writeJson();
}
