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

    @Override
    public void onInitialize() {
        P2MovementConfig.register();
        ModRegister.register();

        LOGGER.info("mlugg make wormhole");
    }

}
