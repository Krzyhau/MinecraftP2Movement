package krzyhau.p2movement.item;

import krzyhau.p2movement.ModMain;
import krzyhau.p2movement.ModRegister;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class LongFallBoots extends ArmorItem {

    public LongFallBoots() {
        super(ModRegister.LONG_FALL_BOOTS_MATERIAL, EquipmentSlot.FEET, new Item.Settings().group(ItemGroup.COMBAT));
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        tooltip.add(new TranslatableText("item.p2movement.long_fall_boots.tooltip").formatted(Formatting.AQUA));
    }
}
