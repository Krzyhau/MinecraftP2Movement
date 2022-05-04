package krzyhau.p2movement;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("Portal2Movement");

    @Override
    public void onInitialize() {
        ModRegister.register();

        LOGGER.info("mlugg make wormhole");
    }

}
