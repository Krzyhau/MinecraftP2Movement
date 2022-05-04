package krzyhau.p2movement;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class Portal2Movement {

    public static final double TICKRATE = 20;
    public static final double TICKTIME = 1.0 / TICKRATE;
    // scale hammer units into minecraft units.
    // technically the scale should be 40 (chell's height / steve's height, 72/1.8)
    // but that makes jumps too small for minecraft world, so I'm scaling everything up a little bit more.
    public static final double UNIT_SCALE = 38.0;
    public static final double MOVE_SCALAR = 1.0 / (UNIT_SCALE * TICKRATE);

    public static final double STOP_SPEED = 100.0 * MOVE_SCALAR;
    public static final double MAX_SPEED = 175.0 * MOVE_SCALAR;
    public static final double MAX_AIR_SPEED = 60.0 * MOVE_SCALAR;
    public static final double FORWARD_SPEED = 175.0 * MOVE_SCALAR;
    public static final double SIDE_SPEED = 175.0 * MOVE_SCALAR;
    public static final double FRICTION = 4.0;
    public static final double ACCELERATE = 10.0;
    public static final double AIRACCELERATE = 5.0;
    public static final double AIR_CONTROL_LIMIT = 300 * MOVE_SCALAR;
    public static final double GRAVITY = 600 * MOVE_SCALAR;
    public static final double JUMP_FORCE = Math.sqrt(2.0 * GRAVITY * 45.0 * MOVE_SCALAR);
    public static final double SPEED_CAP = 3600 * MOVE_SCALAR;

    public static boolean shouldUseCustomMovement(PlayerEntity pe) {
        // check if we can move at all
        if (!pe.canMoveVoluntarily() && !pe.isLogicalSideForUpdatingMovement())
            return false;

        // we can do custom movement only if our current movement is regular
        if (pe.isSwimming() || pe.getAbilities().flying || pe.hasVehicle()
        || pe.isFallFlying() || pe.isTouchingWater() || pe.isInLava() || pe.isClimbing())
            return false;

        // long fall boots are required to be worn for custom movement
        if (!pe.getEquippedStack(EquipmentSlot.FEET).isOf(ModRegister.LONG_FALL_BOOTS))
            return false;

        return true;
    }

    // used for slowfly effect
    public static double getPlayerFriction(PlayerEntity pe) {
        double friction = 1;
        if (!pe.isOnGround() && pe.getVelocity().y < 0.1875 && pe.getVelocity().y > 0) {
            friction *= 0.25;
        }
        return friction;
    }

    public static void applyFriction(PlayerEntity pe) {
        double friction = FRICTION * getPlayerFriction(pe);

        Vec3d vel = pe.getVelocity();
        Vec3d vel2d = pe.getVelocity().add(0, -vel.y, 0);

        if (pe.isOnGround()) {
            if (vel2d.length() >= STOP_SPEED) {
                vel = vel.multiply(1.0 - TICKTIME * friction);
            } else if (vel2d.length() >= Math.max(0.1f * MOVE_SCALAR, TICKTIME * STOP_SPEED * friction)) {
                vel = vel.subtract(vel2d.normalize().multiply(TICKTIME * STOP_SPEED * friction));
            } else {
                vel = new Vec3d(0, vel.y, 0);
            }

            if (vel.length() < MOVE_SCALAR) {
                vel = new Vec3d(0, vel.y, 0);
            }
        }

        pe.setVelocity(vel);
    }

    public static Vec3d createWishDir(PlayerEntity pe, Vec3d movementInput) {
        Vec3d wishDir = movementInput.normalize();

        if (!pe.isOnGround()) {
            if (Math.abs(pe.getPitch()) >= 30.0f) {
                double z = wishDir.z * Math.cos(Math.toRadians((pe.getPitch())));
                wishDir = new Vec3d(wishDir.x, wishDir.y, z);
            }
        }

        double cosYaw = Math.cos(Math.toRadians(pe.getYaw()));
        double sinYaw = Math.sin(Math.toRadians(pe.getYaw()));
        wishDir = new Vec3d(
                cosYaw * wishDir.x - sinYaw * wishDir.z,
                wishDir.y,
                sinYaw * wishDir.x + cosYaw * wishDir.z);

        Vec3d vel2d = pe.getVelocity().add(0, -pe.getVelocity().y, 0);
        if (!pe.isOnGround() && vel2d.length() > AIR_CONTROL_LIMIT) {
            if (Math.abs(vel2d.x) > AIR_CONTROL_LIMIT * 0.5 && vel2d.x * wishDir.x < 0) {
                wishDir = new Vec3d(0, wishDir.y, wishDir.z);
            }
            if (Math.abs(vel2d.z) > AIR_CONTROL_LIMIT * 0.5 && vel2d.z * wishDir.z < 0) {
                wishDir = new Vec3d(wishDir.x, wishDir.y, 0);
            }
        }

        return wishDir;
    }

    public static double getMaxSpeed(PlayerEntity pe, Vec3d wishDir, boolean notAired) {
        double duckMultiplier = (pe.isOnGround() && pe.isSneaking()) ? (1.0 / 3.0) : 1.0;
        wishDir = wishDir.multiply(MAX_SPEED);
        double maxSpeed = Math.min(MAX_SPEED, wishDir.length()) * duckMultiplier;
        double maxAiredSpeed = (pe.isOnGround() || notAired) ? maxSpeed : Math.min(MAX_AIR_SPEED, maxSpeed);
        return maxAiredSpeed;
    }

    public static double getMaxAccel(PlayerEntity pe, Vec3d wishDir) {
        double accel = (pe.isOnGround()) ? ACCELERATE : AIRACCELERATE;
        double realAccel = getPlayerFriction(pe) * TICKTIME * getMaxSpeed(pe, wishDir, true) * accel;
        return realAccel;
    }

    public static void applyGravity(PlayerEntity pe) {
        pe.setVelocity(pe.getVelocity().add(0, -GRAVITY * TICKTIME, 0));
    }

    public static void clampVelocity(PlayerEntity pe) {
        double velX = pe.getVelocity().x;
        double velY = pe.getVelocity().y;
        double velZ = pe.getVelocity().z;

        if (Math.abs(velX) > SPEED_CAP) {
            velX = Math.signum(velX) * SPEED_CAP;
        }
        if (Math.abs(velY) > SPEED_CAP) {
            velY = Math.signum(velY) * SPEED_CAP;
        }
        if (Math.abs(velZ) > SPEED_CAP) {
            velZ = Math.signum(velZ) * SPEED_CAP;
        }

        pe.setVelocity(velX, velY, velZ);
    }

    public static void applyMovementInput(PlayerEntity pe, Vec3d movementInput) {
        pe.setSprinting(false);

        applyGravity(pe);
        applyFriction(pe);

        Vec3d wishDir = createWishDir(pe, movementInput);

        if (wishDir.length() == 0)
            return;

        double maxSpeed = getMaxSpeed(pe, wishDir, false);
        double maxAccel = getMaxAccel(pe, wishDir);

        double accelDiff = maxSpeed - pe.getVelocity().dotProduct(wishDir.normalize());

        if (accelDiff <= 0)
            return;

        double accel = Math.min(accelDiff, maxAccel);

        pe.setVelocity(pe.getVelocity().add(wishDir.normalize().multiply(accel)));

        clampVelocity(pe);
    }

    public static void jump(PlayerEntity pe) {
        pe.setOnGround(false);

        Vec3d vel = pe.getVelocity();
        if(vel.y < JUMP_FORCE) vel = new Vec3d(vel.x,JUMP_FORCE,vel.z);
        pe.setVelocity(vel);
    }
}
