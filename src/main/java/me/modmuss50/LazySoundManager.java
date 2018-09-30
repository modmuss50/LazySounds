package me.modmuss50;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.*;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LazySoundManager {

    private static final Logger LOGGER = LogManager.getLogger();
    public static LazySoundManager INSTANCE = new LazySoundManager();

    SoundHandler soundHandler;
    HashMap<ResourceLocation, SoundList> soundQueue = new HashMap<>();

    public LazySoundManager() {
        this.soundHandler = Minecraft.getMinecraft().getSoundHandler();
    }

    public void loadSounds(IResourceManager resourceManager) {
        long start = System.currentTimeMillis();
        soundHandler.soundRegistry.clearMap();
        soundQueue.clear();
        List<Tuple<ResourceLocation, SoundList>> resources = new java.util.LinkedList<>();

        resourceManager.getResourceDomains().forEach(s -> {
            try {
                for (IResource iresource : resourceManager.getAllResources(new ResourceLocation(s, "sounds.json"))) {
                    try {
                        Map<String, SoundList> map = soundHandler.getSoundMap(iresource.getInputStream());
                        for (Map.Entry<String, SoundList> entry : map.entrySet()) {
                            resources.add(new Tuple<>(new ResourceLocation(s, entry.getKey()), entry.getValue()));
                        }
                    } catch (RuntimeException runtimeexception) {
                        LOGGER.warn("Invalid sounds.json", iresource.getResourceLocation());
                    }
                }
            } catch (IOException e) {
                //
            }
        });
        resources.forEach(entry -> soundQueue.put(entry.getFirst(), entry.getSecond()));
        soundHandler.sndManager.reloadSoundSystem();

        SoundManager.UNABLE_TO_PLAY.removeIf(location -> soundQueue.containsKey(location));

        System.out.println("Loaded sounds in " + (System.currentTimeMillis() - start) + "ms");
    }

    public void playSound(ISound sound){
        checkSound(sound.getSoundLocation());
        System.out.println("Playing sound:" + sound.getSoundLocation());
        soundHandler.sndManager.playSound(sound);
    }

    public void playDelayedSound(ISound sound, int delay){
        checkSound(sound.getSoundLocation());
        System.out.println("Playing delayed sound:" + sound.getSoundLocation());
        soundHandler.sndManager.playDelayedSound(sound, delay);
    }

    public void checkSound(ResourceLocation location){
        if(soundQueue.containsKey(location)){
            long ms  = System.currentTimeMillis();
            System.out.println("Loading sound:" + location);
            soundHandler.loadSoundResource(location, soundQueue.get(location));
            soundQueue.remove(location);
            System.out.println("Loaded sound in " + (System.currentTimeMillis() - ms) + "ms");
        }

    }

}
