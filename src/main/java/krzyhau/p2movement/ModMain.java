package krzyhau.p2movement;

import krzyhau.p2movement.config.P2MovementConfig;
import krzyhau.p2movement.item.LongFallBoots;
import krzyhau.p2movement.item.materials.LongFallBootsMaterial;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModMain implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("Portal2Movement");
    public static final ArmorMaterial LONG_FALL_BOOTS_MATERIAL = new LongFallBootsMaterial();
    public static final Item LONG_FALL_BOOTS = new LongFallBoots();
    public static final Enchantment CL_SHOWPOS_ENCHANTMENT = new ClShowposEnchantment();

    @Override
    public void onInitialize() {
        P2MovementConfig.register();

        Registry.register(Registry.ITEM, new Identifier("p2movement", "long_fall_boots"), LONG_FALL_BOOTS);
        Registry.register(Registry.ENCHANTMENT, new Identifier("p2movement", "cl_showpos"), CL_SHOWPOS_ENCHANTMENT);

        LOGGER.info("mlugg make wormhole");
    }

}
