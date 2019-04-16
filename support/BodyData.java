package support;

import java.awt.*;

public class BodyData {
    private final String name;
    private final double mass;
    private final double radius;
    private final Vector initialPosition;
    private final Vector initialVelocity;
    private final Color color;

    public String getName() { return name; }
    public double getMass() { return mass; }
    public double getRadius() { return radius; }
    public Vector getInitialPosition() { return initialPosition.copy(); }
    public Vector getInitialVelocity() { return initialVelocity.copy(); }
    public Color getColor() { return color; }

    public BodyData(
        BodyData parent,
        String name,
        double mass,
        double radius,
        Vector initialPosition,
        Vector initialVelocity,
        Color color
    ) {
        this.name = name;
        this.mass = mass;
        this.radius = radius;
        this.initialPosition = initialPosition.copy();
        this.initialVelocity = initialVelocity.copy();

        if (parent != null) {
            this.initialPosition.add(parent.getInitialPosition());
            this.initialVelocity.add(parent.getInitialVelocity());
        }

        this.color = color;
    }
}
