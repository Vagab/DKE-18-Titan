package environments;

import body.Body;
import body.SpaceShip;
import support.BodyData;
import support.LaunchData;
import support.Vector;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Environment {
    protected final List<Body> allBodies = new ArrayList<>();
    protected final List<SpaceShip> spaceShips = new ArrayList<>();

    protected boolean running = false;
    protected Thread runner = null;
    protected Calendar startDate;
    protected long startMillis;
    protected Calendar currentDate;

    protected long currentMillis() {
        return currentDate.getTimeInMillis();
    }

    protected long secondsPassed() {
        return TimeUnit.MILLISECONDS.toSeconds(currentMillis() - startMillis);
    }

    protected void addBody(Body body) {
        allBodies.add(body);

        if (body instanceof SpaceShip)
            spaceShips.add((SpaceShip) body);
    }

    public Environment(List<Body> inputBodies) {
        for (Body body : inputBodies)
            addBody(body);

        // initialize currentDate
        startDate = Calendar.getInstance();
        startDate.set(Calendar.YEAR, 2019);
        startDate.set(Calendar.MONTH, Calendar.JANUARY);
        startDate.set(Calendar.DAY_OF_MONTH, 1);
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);

        startMillis = startDate.getTimeInMillis();

        currentDate = (Calendar) startDate.clone();
    }

    public Environment(Body ...inputBodies) {
        this(new ArrayList<>(Arrays.asList(inputBodies)));
    }

    // adds body to surface of another body with the correct speed pointing prograde
    protected void launchShipFromEarthPrograde(LaunchData launchData) {
        Body sun = allBodies.get(0);
        Body earth = allBodies.get(3); // should be earth

        Vector progradeDirection = earth.getVelocity().direction(); // unit vector
        Vector towardsSunDirection = earth.getPosition().vectorTo(sun.getPosition()).direction();
        Vector perpendicularDirection = progradeDirection.perpendicularVector(towardsSunDirection).direction();

        Vector shipVelocity = earth.getVelocity().copy();

        progradeDirection.scaleBy(launchData.progradeVelocity);
        towardsSunDirection.scaleBy(launchData.towardsSunVelocity);
        perpendicularDirection.scaleBy(launchData.perpendicularVelocity);

        shipVelocity.add(progradeDirection);
        shipVelocity.add(towardsSunDirection);
        shipVelocity.add(perpendicularDirection);

        Vector shipPosition = earth.getPosition().copy();
        Vector earthSurface = shipVelocity.direction();
        earthSurface.scaleBy(earth.getRadius());
        shipPosition.add(earthSurface);

        System.out.println();
        System.out.println("new SpaceShip");
        System.out.println("  position: " + shipPosition);
        System.out.println("  velocity: " + shipVelocity);
        System.out.println("  earthPos: " + earth.getPosition());
        System.out.println("  earthVel: " + earth.getVelocity());

        BodyData bodyData = new BodyData(
            null, launchData.name,0, 0, shipPosition, shipVelocity, Color.WHITE
        );

        SpaceShip newSpaceShip = new SpaceShip(bodyData, launchData);
        addBody(newSpaceShip);
    }

    private final List<LaunchData> nextLaunches = new LinkedList<>();

    private void launchShips() {
        synchronized (nextLaunches) {
            while (!nextLaunches.isEmpty() && nextLaunches.get(0).launchTimeMillis <= currentMillis())
                launchShipFromEarthPrograde(
                        nextLaunches
                                .remove(0)
                );
        }
    }

    // no mass, no radius, position is calculated, velocity direction is calculated
    public void addLaunch(LaunchData data) {
        long atMillis = data.launchTimeMillis;

        if (atMillis < currentMillis())
            throw new IllegalStateException("couldn't launch, it's too late");

        synchronized (nextLaunches) {
            nextLaunches.add(data);
            Collections.sort(nextLaunches);
        }
    }

    public void iteratePhysics(int timeStepInSeconds) {
        for (Body body : allBodies)
            body.iteratePhysics(allBodies, timeStepInSeconds);

        currentDate.add(Calendar.SECOND, timeStepInSeconds);

        launchShips();
    }

    public void startThread(int timeStepInSeconds) {
        if (runner != null) return;

        runner = new Thread(() -> {
            while (running)
                iteratePhysics(timeStepInSeconds);
        });

        running = true;
        runner.start();
    }

    public void stopThread() {
        if (runner == null) return;

        running = false;
        runner = null;
    }

    public void run(int timeStepInSeconds) {
        while (true)
            iteratePhysics(timeStepInSeconds);
    }
}
