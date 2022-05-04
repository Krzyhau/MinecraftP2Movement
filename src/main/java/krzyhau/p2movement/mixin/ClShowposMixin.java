package krzyhau.p2movement.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import krzyhau.p2movement.ClShowposHud;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public class ClShowposMixin {
    @Inject(method = "render", at = @At("RETURN"), cancellable = true)
    public void onRender(MatrixStack matrices, float tickDelta, CallbackInfo info) {
        ClShowposHud.draw(matrices);
    }
}
