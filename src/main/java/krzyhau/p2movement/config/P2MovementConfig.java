package krzyhau.p2movement.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

import static me.shedaniel.autoconfig.AutoConfig.getConfigHolder;

@Config(name = "p2movement")
public class P2MovementConfig implements ConfigData {

    public double STOP_SPEED = 100;
    public double MAX_SPEED = 175;
    public double MAX_AIR_SPEED = 60;
    //public double FORWARD_SPEED = 175.0;
    //public double SIDE_SPEED = 175.0;
    public double GRAVITY = 600;
    public double SPEED_CAP = 3600;
    public double FRICTION = 4;
    public double ACCELERATE = 10;
    public double AIRACCELERATE = 5;

    public static void register() {
        AutoConfig.register(P2MovementConfig.class, GsonConfigSerializer::new);
    }

    public static P2MovementConfig get() {
        return getConfigHolder(P2MovementConfig.class).getConfig();
    }

    public static void save() {
        getConfigHolder(P2MovementConfig.class).save();
    }
}
