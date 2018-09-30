package me.modmuss50;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.resources.IResourceManager;

public class SoundHooks {

    public static void onResourceManagerReload(IResourceManager resourceManager){
        LazySoundManager.INSTANCE.loadSounds(resourceManager);
    }

    public static void playSound(ISound sound){
        LazySoundManager.INSTANCE.playSound(sound);
    }

    public static void playDelayedSound(ISound sound, int delay){
        LazySoundManager.INSTANCE.playDelayedSound(sound, delay);
    }


}
