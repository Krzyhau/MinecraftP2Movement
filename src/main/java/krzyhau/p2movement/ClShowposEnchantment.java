package krzyhau.p2movement;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class ClShowposEnchantment extends Enchantment{

    protected ClShowposEnchantment() {
        super(Enchantment.Rarity.COMMON, EnchantmentTarget.ARMOR_FEET, new EquipmentSlot[] { EquipmentSlot.FEET });
    }

    @Override
    public int getMinPower(int level) {
        return 1;
    }
    
    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.isOf(ModRegister.LONG_FALL_BOOTS);
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return false;
    }
}
