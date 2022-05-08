package krzyhau.p2movement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

import java.util.Locale;


public class ClShowposHud {

    private static final MinecraftClient MC_INSTANCE = MinecraftClient.getInstance();

    public static void draw(MatrixStack matrices) {
        if (!canDraw()) return;

        String[] lines = getShowposString();
        for (int i = 0; i < lines.length; i++) {
            MC_INSTANCE.textRenderer.draw(matrices, lines[i], 5, 5 + i * 10, -1);
        }
    }

    private static boolean canDraw() {
        ClientPlayerEntity player = MC_INSTANCE.player;
        if (player == null)
            return false;

        ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);
        if (!(boots.getItem() == (ModRegister.LONG_FALL_BOOTS))) {
            return false;
        }

        return EnchantmentHelper.getLevel(ModRegister.CL_SHOWPOS_ENCHANTMENT, boots) != 0;
    }

    private static String[] getShowposString() {
        ClientPlayerEntity player = MC_INSTANCE.player;
        if (player == null)
            return new String[]{};

        String name = player.getDisplayName().asString();

        Vec3d pos = player.getPos();
        // why the fuck isnt yaw clamped?!
        Vec3d ang = new Vec3d(player.pitch, (player.yaw - 180) % 360 + 180, 0);


        Vec3d vel = player.getVelocity().multiply(1 / Portal2Movement.MOVE_SCALAR);

        Vec3d vel2d = vel.add(0, -vel.y, 0);

        return new String[]{
                "name: " + name,
                String.format(Locale.ROOT, "pos: %.2f %.2f %.2f", pos.x, pos.y, pos.z),
                String.format(Locale.ROOT, "ang: %.2f %.2f %.2f", ang.x, ang.y, ang.z),
                String.format(Locale.ROOT, "vel: %.2f %.2f", vel2d.length(), vel.y)
        };
    }
}
