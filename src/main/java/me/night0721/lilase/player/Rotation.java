package me.night0721.lilase.player;

import me.night0721.lilase.Lilase;
import me.night0721.lilase.utils.AngleUtils;
import org.apache.commons.lang3.tuple.MutablePair;

public class Rotation {
    public boolean rotating;
    public boolean completed;

    private long startTime;
    private long endTime;

    final MutablePair<Float, Float> start = new MutablePair<>(0f, 0f);
    final MutablePair<Float, Float> target = new MutablePair<>(0f, 0f);
    final MutablePair<Float, Float> difference = new MutablePair<>(0f, 0f);

    public void easeTo(float yaw, float pitch, long time) {
        completed = false;
        rotating = true;
        startTime = System.currentTimeMillis();
        endTime = System.currentTimeMillis() + time;
        start.setLeft(Lilase.mc.thePlayer.rotationYaw);
        start.setRight(Lilase.mc.thePlayer.rotationPitch);
        target.setLeft(AngleUtils.get360RotationYaw(yaw));
        target.setRight(pitch);
        getDifference();
    }

    public void lockAngle(float yaw, float pitch) {
        if (Lilase.mc.thePlayer.rotationYaw != yaw || Lilase.mc.thePlayer.rotationPitch != pitch && !rotating)
            easeTo(yaw, pitch, 1000);
    }

    public void update() {
        if (System.currentTimeMillis() <= endTime) {
            if (shouldRotateClockwise()) {
                Lilase.mc.thePlayer.rotationYaw = start.left + interpolate(difference.left);
            } else {
                Lilase.mc.thePlayer.rotationYaw = start.left - interpolate(difference.left);
            }
            Lilase.mc.thePlayer.rotationPitch = start.right + interpolate(difference.right);
        } else if (!completed) {
            if (shouldRotateClockwise()) {
                System.out.println("Rotation final st - " + start.left + ", " + Lilase.mc.thePlayer.rotationYaw);
                Lilase.mc.thePlayer.rotationYaw = target.left;
                System.out.println("Rotation final - " + start.left + difference.left);
            } else {
                Lilase.mc.thePlayer.rotationYaw = target.left;
                System.out.println("Rotation final - " + (start.left - difference.left));
            }
            Lilase.mc.thePlayer.rotationPitch = start.right + difference.right;
            completed = true;
            rotating = false;
        }
    }

    private boolean shouldRotateClockwise() {
        return AngleUtils.clockwiseDifference(AngleUtils.get360RotationYaw(start.left), target.left) < 180;
    }

    public void reset() {
        completed = false;
        rotating = false;
    }

    private void getDifference() {
        difference.setLeft(AngleUtils.smallestAngleDifference(AngleUtils.get360RotationYaw(), target.left));
        difference.setRight(target.right - start.right);
    }

    private float interpolate(float difference) {
        final float spentMillis = System.currentTimeMillis() - startTime;
        final float relativeProgress = spentMillis / (endTime - startTime);
        return (difference) * easeOutSine(relativeProgress);
    }

    private float easeOutCubic(double number) {
        return (float) (1.0 - Math.pow(1.0 - number, 3.0));
    }

    private float easeOutSine(double number) {
        return (float) Math.sin((number * Math.PI) / 2);
    }
}