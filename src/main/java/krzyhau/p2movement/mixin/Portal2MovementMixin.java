package krzyhau.p2movement.mixin;

import net.minecraft.entity.Flutterer;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import krzyhau.p2movement.Portal2Movement;

@Mixin(PlayerEntity.class)
public class Portal2MovementMixin {

    // override travel function to allow custom movement
    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    public void travel(Vec3d movementInput, CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;

        if (Portal2Movement.shouldUseCustomMovement(self)) {

            Portal2Movement.applyMovementInput(self, movementInput);

            Vec3d oldPos = self.getPos();

            self.move(MovementType.SELF, self.getVelocity());

            self.updateLimbs(self, self instanceof Flutterer);

            self.increaseTravelMotionStats(self.getX() - oldPos.x, self.getY() - oldPos.y, self.getZ() - oldPos.z);

            ci.cancel();
        }
    }

    // override jumping because something sketchy was going on with the default one
    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    public void jump(CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;

        if (Portal2Movement.shouldUseCustomMovement(self)) {
            Portal2Movement.jump(self);
            ci.cancel();
        }
    }

    // make player invulnerable to fall damage when wearing long fall boots
    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    public void isInvulnerableTo(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity self = (PlayerEntity) (Object) this;

        if (Portal2Movement.shouldUseCustomMovement(self)) {
            if(source.isFromFalling()) {
                cir.setReturnValue(true);
            }
        }
    }
}
