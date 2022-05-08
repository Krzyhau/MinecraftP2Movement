package krzyhau.p2movement.mixin;

import krzyhau.p2movement.ModMain;
import krzyhau.p2movement.Portal2Movement;
import krzyhau.p2movement.config.P2MovementConfig;
import net.minecraft.entity.*;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class Portal2MovementMixin extends LivingEntity {

    protected Portal2MovementMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    // override travel function to allow custom movement
    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    public void travel(Vec3d movementInput, CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;

        if (Portal2Movement.shouldUseCustomMovement(self)) {
            Portal2Movement p2Movement = new Portal2Movement();

            p2Movement.config = P2MovementConfig.get();
            
            p2Movement.applyMovementInput(self, movementInput);

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
            new Portal2Movement().jump(self);
            ci.cancel();
        }
    }

    // make player invulnerable to fall damage when wearing long fall boots
    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    public void isInvulnerableTo(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity self = (PlayerEntity) (Object) this;

        if (Portal2Movement.shouldUseCustomMovement(self)) {
            if (source == DamageSource.FALL) {
                cir.setReturnValue(true);
            }
        }
    }
}
