package body;

import support.BodyData;
import support.LaunchData;
import support.Vector;

import java.util.List;

public class SpaceShip extends Body {
    public final LaunchData launchData;
    public final Body target;
    public double closestApproach = Double.POSITIVE_INFINITY;

    public SpaceShip(BodyData data, LaunchData launchData) {
        super(data);

        this.launchData = launchData;
        this.target = launchData.target;
    }

    public boolean entered = false;
    public boolean left = false;

    private static double enteredThreshold = 1.221850e9;
    private static double leftThreshold = 2e9;

    @Override
    public void iteratePhysics(List<Body> allBodies, double timeStepInSeconds) {
        super.iteratePhysics(allBodies, timeStepInSeconds)

        double distance = position.distanceTo(target.position);

        if (distance < enteredThreshold)
            entered = true;

        if (entered && distance > leftThreshold)
            left = true;

        double distance2d = position.distance2dTo(target.position);

        if (distance2d < closestApproach) {
            // new closest approach
            closestApproach = distance2d;

            if (closestApproach < target.radius) {
                // new closest collision
                System.out.println();
                System.out.println("COLLISION (" + name + ")");
                System.out.println("  distance from center: " + position.distanceTo(target.position) / 1000 + "km");
                System.out.println("  2d distance from center: " + closestApproach / 1000 + "km");
                System.out.println("  speed: " + velocity.magnitude() / 1000 + "km/s");
                Vector velocity2d = velocity.copy();
                velocity2d.z(0);
                System.out.println("  2d speed: " + velocity2d.magnitude() / 1000 + "km/s");
            }
        }
    }
}
