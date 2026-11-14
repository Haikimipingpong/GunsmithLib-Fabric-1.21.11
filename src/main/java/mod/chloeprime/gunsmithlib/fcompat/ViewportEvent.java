package mod.chloeprime.gunsmithlib.fcompat;

import net.minecraft.client.Camera;

public class ViewportEvent extends Event {
    private final Camera camera;
    private final float partialTick;

    public ViewportEvent(Camera camera, float partialTick) {
        this.camera = camera;
        this.partialTick = partialTick;
    }

    public Camera getCamera() {
        return camera;
    }

    public float getPartialTick() {
        return partialTick;
    }

    public static class ComputeCameraAngles extends ViewportEvent {
        private double yaw;
        private double pitch;
        private double roll;

        public ComputeCameraAngles(Camera camera, float partialTick, double yaw, double pitch, double roll) {
            super(camera, partialTick);
            this.yaw = yaw;
            this.pitch = pitch;
            this.roll = roll;
        }

        public double getYaw() {
            return yaw;
        }

        public void setYaw(double yaw) {
            this.yaw = yaw;
        }

        public double getPitch() {
            return pitch;
        }

        public void setPitch(double pitch) {
            this.pitch = pitch;
        }

        public double getRoll() {
            return roll;
        }

        public void setRoll(double roll) {
            this.roll = roll;
        }
    }

    public static class ComputeFov extends ViewportEvent {
        private double fov;

        public ComputeFov(Camera camera, float partialTick, double fov) {
            super(camera, partialTick);
            this.fov = fov;
        }

        public double getFOV() {
            return fov;
        }

        public void setFOV(double fov) {
            this.fov = fov;
        }
    }
}
