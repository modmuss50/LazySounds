package me.modmuss50;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class ClassTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if(transformedName.equals("net.minecraft.client.audio.SoundHandler")){
            System.out.println("Found SoundHandler");
            ClassNode classNode = readClassFromBytes(basicClass);
            for(MethodNode methodNode : classNode.methods){
                if(methodNode.name.equals("onResourceManagerReload") || methodNode.name.equals("func_110549_a") || (methodNode.name.equals("a") && methodNode.desc.equals("(Lcep;)V"))){
                    System.out.println("Found onResourceManagerReload");
                    patch_onResourceManagerReload(methodNode);
                }
                if(methodNode.name.equals("playSound") || methodNode.name.equals("func_147682_a") || (methodNode.name.equals("a") && methodNode.desc.equals("(Lcgt;)V"))){
                    System.out.println("Found playSound");
                    patch_playSound(methodNode);
                }
                if(methodNode.name.equals("playDelayedSound") || methodNode.name.equals("func_147681_a") || (methodNode.name.equals("a") && methodNode.desc.equals("(Lcgt;I)V"))){
                    System.out.println("Found playDelayedSound");
                    patch_playDelayedSound(methodNode);
                }
            }
            return writeClassToBytes(classNode);
        }
        return basicClass;
    }

    public void patch_onResourceManagerReload(MethodNode methodNode){
        methodNode.instructions.clear();
        InsnList insnList = new InsnList();
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
        insnList.add(new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "me/modmuss50/SoundHooks",
                "onResourceManagerReload", "(Lnet/minecraft/client/resources/IResourceManager;)V",
                false
        ));
       insnList.add(new InsnNode(Opcodes.RETURN));
       methodNode.instructions.insert(insnList);

       //Remove the old try catches as we no longer have any - great 2 hours spent on this 1 line of code
       methodNode.tryCatchBlocks.clear();
    }

    public void patch_playSound(MethodNode methodNode){
        methodNode.instructions.clear();
        InsnList insnList = new InsnList();
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
        insnList.add(new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "me/modmuss50/SoundHooks",
                "playSound", "(Lnet/minecraft/client/audio/ISound;)V",
                false
        ));

        insnList.add(new InsnNode(Opcodes.RETURN));
        methodNode.instructions.insert(insnList);
    }

    public void patch_playDelayedSound(MethodNode methodNode){
        methodNode.instructions.clear();
        InsnList insnList = new InsnList();
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
        insnList.add(new VarInsnNode(Opcodes.ILOAD, 2));
        insnList.add(new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "me/modmuss50/SoundHooks",
                "playDelayedSound", "(Lnet/minecraft/client/audio/ISound;I)V",
                false
        ));

        insnList.add(new InsnNode(Opcodes.RETURN));
        methodNode.instructions.insert(insnList);
    }

    public static ClassNode readClassFromBytes(byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);
        return classNode;
    }

    public static byte[] writeClassToBytes(ClassNode classNode) {
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }
}
