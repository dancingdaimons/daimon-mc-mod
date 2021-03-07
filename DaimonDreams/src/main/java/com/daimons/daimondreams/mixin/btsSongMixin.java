package com.daimons.daimondreams.mixin;

import com.daimons.daimondreams.MovingBtsSoundInstance;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.network.packet.s2c.play.MobSpawnS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPlayNetworkHandler.class)
public class btsSongMixin {

    @Shadow
    private MinecraftClient client;

    @Inject(at = @At(shift = At.Shift.AFTER, value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;addEntity(ILnet/minecraft/entity/Entity;)V"), method = "onMobSpawn",locals = LocalCapture.CAPTURE_FAILHARD)
    public void onMobSpawn(MobSpawnS2CPacket packet, CallbackInfo ci, double d, double e, double f, float g, float h, LivingEntity livingEntity) {
        if (livingEntity instanceof ZombieEntity) {
            //this.client.getSoundManager().play(new MovingMinecartSoundInstance((AbstractMinecartEntity)entity15));
            MovingBtsSoundInstance yes = new MovingBtsSoundInstance((ZombieEntity)livingEntity);
            this.client.getSoundManager().playNextTick((TickableSoundInstance) yes);
        }
    }
}
