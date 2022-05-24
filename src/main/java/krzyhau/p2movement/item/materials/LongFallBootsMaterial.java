package krzyhau.p2movement.item.materials;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class LongFallBootsMaterial implements ArmorMaterial {

    @Override
    public int getDurability(EquipmentSlot slot) {
        return 600;
    }

    @Override
    public int getEnchantability() {
        return 1;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.5f;
    }

    @Override
    public String getName() {
        return "long_fall_boots";
    }

    @Override
    public int getProtectionAmount(EquipmentSlot slot) {
        return 2;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(Items.IRON_BARS);
    }

    @Override
    public float getToughness() {
        return 1.0f;
    }

}