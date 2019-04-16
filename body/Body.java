package body;

import support.Constants;
import support.BodyData;
import support.Vector;

import java.awt.*;
import java.util.List;


public class Body {
    public final String name;
    protected double mass;
    protected double radius;
    protected Vector position;
    protected Vector velocity;
    protected Color color;

    public double getMass() { return mass; }
    public double getRadius() { return radius; }
    public Vector getPosition() { return position; }
    public Vector getVelocity() { return velocity; }
    public Color getColor() { return color; }

    public Body(BodyData data) {
        this.name = data.getName();
        this.mass = data.getMass();
        this.radius = data.getRadius();
        this.position = data.getInitialPosition();
        this.velocity = data.getInitialVelocity();
        this.color = data.getColor();
    }

    public void iteratePhysics(List<Body> allBodies, double timeStepInSeconds) {
        // apply acceleration due to gravity
        for (Body attractor : allBodies) {
            if (this == attractor) continue; // don't apply own gravity
            if (attractor.mass == 0) continue; // don't apply gravity of massless objects (space ships)

            Vector vectorToAttractor = position.vectorTo(attractor.position);

            Vector acceleration = vectorToAttractor.direction();
            double distance = vectorToAttractor.magnitude();

            // skip if they're at the same position
            if (distance == 0) continue;

            double accelerationMagnitude = Constants.G * attractor.mass / Math.pow(distance, 2);

            acceleration.scaleBy(accelerationMagnitude * timeStepInSeconds);

            velocity.add(acceleration);
        }

        // move
        Vector scaledVelocity = velocity.copy();
        scaledVelocity.scaleBy(timeStepInSeconds);
        position.add(scaledVelocity);
    }
}
