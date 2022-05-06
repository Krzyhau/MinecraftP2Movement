package krzyhau.p2movement;

import krzyhau.p2movement.config.P2MovementConfig;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class Portal2Movement {
    public static final double TICKRATE = 20;
    public static final double TICKTIME = 1 / TICKRATE;
    // scale hammer units into minecraft units.
    // technically the scale should be 40 (chell's height / steve's height, 72/1.8)
    // but that makes jumps too small for minecraft world, so I'm scaling everything up a little bit more.
    public static final double UNIT_SCALE = 38;
    public static final double MOVE_SCALAR = 1 / (UNIT_SCALE * TICKRATE);
    public static final double FORWARD_SPEED = 175 * MOVE_SCALAR;
    public static final double SIDE_SPEED = 175 * MOVE_SCALAR;
    public static final double AIR_CONTROL_LIMIT = 300 * MOVE_SCALAR;


    public P2MovementConfig config;

    // fuck static
    public double STOP_SPEED;
    public double MAX_SPEED;
    public double MAX_AIR_SPEED;
    public double GRAVITY;
    public double JUMP_FORCE;
    public double SPEED_CAP;
    public double FRICTION;
    public double ACCELERATE;
    public double AIRACCELERATE;

    public Portal2Movement() {
        P2MovementConfig config = P2MovementConfig.get();

        STOP_SPEED = config.STOP_SPEED * MOVE_SCALAR;
        MAX_SPEED = config.MAX_SPEED * MOVE_SCALAR;
        MAX_AIR_SPEED = config.MAX_AIR_SPEED * MOVE_SCALAR;
        GRAVITY = config.GRAVITY * MOVE_SCALAR;
        JUMP_FORCE = Math.sqrt(2 * GRAVITY * 45 * MOVE_SCALAR);
        SPEED_CAP = config.SPEED_CAP * MOVE_SCALAR;
        FRICTION = config.FRICTION;
        ACCELERATE = config.ACCELERATE;
        AIRACCELERATE = config.AIRACCELERATE;
    }

    public static boolean shouldUseCustomMovement(PlayerEntity pe) {
        // check if we can move at all
        if (!pe.canMoveVoluntarily() && !pe.isLogicalSideForUpdatingMovement())
            return false;

        // we can do custom movement only if our current movement is regular
        if (pe.isSwimming() || pe.abilities.flying || pe.hasVehicle()
                || pe.isFallFlying() || pe.isTouchingWater() || pe.isInLava() || pe.isClimbing())
            return false;

        // long fall boots are required to be worn for custom movement
        return pe.getEquippedStack(EquipmentSlot.FEET).getItem() == (ModMain.LONG_FALL_BOOTS);
    }

    // used for slowfly effect
    public double getPlayerFriction(PlayerEntity pe) {
        double friction = 1;
        if (!pe.isOnGround() && pe.getVelocity().y < 0.1875 && pe.getVelocity().y > 0) {
            friction *= 0.25;
        }
        return friction;
    }

    public void applyFriction(PlayerEntity pe) {
        double friction = FRICTION * getPlayerFriction(pe);

        Vec3d vel = pe.getVelocity();
        Vec3d vel2d = pe.getVelocity().add(0, -vel.y, 0);

        if (pe.isOnGround()) {
            if (vel2d.length() >= STOP_SPEED) {
                vel = vel.multiply(1 - TICKTIME * friction);
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

    public Vec3d createWishDir(PlayerEntity pe, Vec3d movementInput) {
        Vec3d wishDir = movementInput.normalize();

        if (!pe.isOnGround()) {
            if (Math.abs(pe.pitch) >= 30f) {
                double z = wishDir.z * Math.cos(Math.toRadians((pe.pitch)));
                wishDir = new Vec3d(wishDir.x, wishDir.y, z);
            }
        }

        double cosYaw = Math.cos(Math.toRadians(pe.yaw));
        double sinYaw = Math.sin(Math.toRadians(pe.yaw));
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

    public double getMaxSpeed(PlayerEntity pe, Vec3d wishDir, boolean notAired) {
        double duckMultiplier = (pe.isOnGround() && pe.isSneaking()) ? (1.0 / 3.0) : 1;
        wishDir = wishDir.multiply(MAX_SPEED);
        double maxSpeed = Math.min(MAX_SPEED, wishDir.length()) * duckMultiplier;
        return (pe.isOnGround() || notAired) ? maxSpeed : Math.min(MAX_AIR_SPEED, maxSpeed);
    }

    public double getMaxAccel(PlayerEntity pe, Vec3d wishDir) {
        double accel = (pe.isOnGround()) ? ACCELERATE : AIRACCELERATE;
        return getPlayerFriction(pe) * TICKTIME * getMaxSpeed(pe, wishDir, true) * accel;
    }

    public void applyGravity(PlayerEntity pe) {
        pe.setVelocity(pe.getVelocity().add(0, -GRAVITY * TICKTIME, 0));
    }

    public void clampVelocity(PlayerEntity pe) {
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

    public void applyMovementInput(PlayerEntity pe, Vec3d movementInput) {
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

    public void jump(PlayerEntity pe) {
        pe.setOnGround(false);

        Vec3d vel = pe.getVelocity();
        if (vel.y < JUMP_FORCE) vel = new Vec3d(vel.x, JUMP_FORCE, vel.z);
        pe.setVelocity(vel);
    }
}
