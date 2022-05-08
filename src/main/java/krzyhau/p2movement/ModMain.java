package krzyhau.p2movement;

import krzyhau.p2movement.config.P2MovementConfig;
import net.fabricmc.api.ModInitializer;
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
