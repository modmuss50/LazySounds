package me.modmuss50;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import javax.annotation.Nullable;
import java.util.Map;


/**
 * To run this in dev you need to add the following to the *VM* Options in the run config
 *
 * -Dfml.coreMods.load=me.modmuss50.LoadingPlugin
 */
@IFMLLoadingPlugin.SortingIndex(1001)//Ensures that the target classes have been mapped to srg names
@IFMLLoadingPlugin.Name("LazySounds")
public class LoadingPlugin implements IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        return new String[]{"me.modmuss50.ClassTransformer"};
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
