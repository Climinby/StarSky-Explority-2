package com.climinby.starsky_explority.mixin;

import com.climinby.starsky_explority.nbt.player.SSEDataHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//@Mixin(Entity.class)
//public abstract class PlayerEntityMixin implements SSEDataHandler {
//    private NbtCompound customNbt;
//    private static final String SSE_NBT_KEY = "SSENbt";
//
//    @Inject(method = "<init>", at = @At("RETURN"))
//    private void onConstruct(CallbackInfo ci) {
//        customNbt = new NbtCompound();
//    }
//
//    @Inject(method = "writeNbt", at = @At("RETURN"))
//    private void writeSSEDataToNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
//        //write in file with data
//        nbt.put(SSE_NBT_KEY, customNbt);
//    }
//
//    @Inject(method = "readNbt", at = @At("RETURN"))
//    private void readSSEDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
//        //load data from file
//        if(nbt.contains(SSE_NBT_KEY)) {
//            customNbt.copyFrom(nbt.getCompound(SSE_NBT_KEY));
//        }
//    }
//
//    @Override
//    public NbtCompound getSSEData() {
//        return customNbt;
//    }
//
//    @Override
//    public void setSSEData(NbtCompound nbt) {
//        customNbt.copyFrom(nbt);
//    }
//}
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements SSEDataHandler {
    private NbtCompound customNbt;
    private static final String SSE_NBT_KEY = "SSENbt";

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstruct(CallbackInfo ci) {
        customNbt = new NbtCompound();
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    private void writeSSEDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        //write in file with data
        nbt.put(SSE_NBT_KEY, customNbt);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    private void readSSEDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        //load data from file
        if(nbt.contains(SSE_NBT_KEY)) {
            customNbt.copyFrom(nbt.getCompound(SSE_NBT_KEY));
        }
    }

    @Override
    public NbtCompound getSSEData() {
        return customNbt.copy();
    }

    @Override
    public void setSSEData(NbtCompound nbt) {
        customNbt.copyFrom(nbt);
    }
}
