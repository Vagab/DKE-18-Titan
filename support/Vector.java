package support;

import java.util.Locale;

// vector is mutable!
public class Vector {
    private double x, y, z;
    public double x() { return x; }
    public double y() { return y; }
    public double z() { return z; }
    public void x(double value) { x = value; }
    public void y(double value) { y = value; }
    public void z(double value) { z = value; }

    public static Vector zero() {
        return new Vector(0, 0, 0);
    }

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double[] coords() {
      double[] arr = {x, y, z};
      return arr;
    }

    public Vector(Vector vector) { this(vector.x, vector.y, vector.z); }

    public Vector copy() {
        return new Vector(this);
    }

    public double magnitude() {
        return Math.sqrt(x*x + y*y + z*z);
    }

    public Vector direction() {
        double magnitude = magnitude();
        return new Vector(
            x / magnitude,
            y / magnitude,
            z / magnitude
        );
    }

    public void scaleBy(double scale) {
        x *= scale;
        y *= scale;
        z *= scale;
    }

    // rotates counterclockwise by "angle" (in radians)
    // ignores z
    public void rotateBy(double angle) {
        double oldX = x;
        double oldY = y;
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        x = oldX * cos - oldY * sin;
        y = oldY * cos + oldX * sin;
    }

    public void add(Vector vector) { x += vector.x;
        y += vector.y;
        z += vector.z;
    }

    public Vector vectorTo(Vector vector) {
        return new Vector(
            vector.x - x,
            vector.y - y,
            vector.z - z
        );
    }

    public double distanceTo(Vector vector) {
        return vectorTo(vector).magnitude();
    }

    public double distance2dTo(Vector vector) {
        return Math.sqrt(Math.pow(vector.x - x, 2) + Math.pow(vector.y - y, 2));
    }

    public Vector perpendicularVector(Vector vector) {
        return new Vector(
            y * vector.z - z * vector.y,
            z * vector.x - x * vector.z,
            x * vector.y - y * vector.x
        );
    }

    @Override
    public String toString() {
        return String.format(
            Locale.US, // for decimal point instead of comma
            "(%.2e, %.2e, %.2e)",
            x, y, z
        );
    }
}
