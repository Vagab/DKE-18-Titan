package environments;

import body.Body;
import body.SpaceShip;

import java.util.Calendar;

public class MissionEnvironment extends Environment {
    private long timeLimitMillis;

    public MissionEnvironment(long timeLimitMillis, Body ...bodies) {
        super(bodies);
        this.timeLimitMillis = timeLimitMillis;
    }

    public MissionEnvironment(Calendar timeLimit, Body ...bodies) {
        this(timeLimit.getTimeInMillis(), bodies);
    }

    private int lastYear = 2019;

    @Override
    public void iteratePhysics(int timeStepInSeconds) {
        super.iteratePhysics(timeStepInSeconds);

        boolean allDone = true;

        if (!spaceShips.isEmpty()) {
            for (SpaceShip spaceShip : spaceShips) {
                if (!spaceShip.left) {
                    allDone = false;
                    break;
                }
            }

            if (allDone) throwClosest();
        }

        if (currentMillis() > timeLimitMillis) throwClosest();

        int currentYear = currentDate.get(Calendar.YEAR);
        if (currentYear > lastYear) {
            System.out.println("year " + currentYear);
            lastYear = currentYear;
            System.out.println("current/limit: " + String.format("%.1e / %.1e",
                    (double) currentMillis(), (double) timeLimitMillis));
        }
    }

    private void throwClosest() {
        SpaceShip closest = spaceShips.get(0);
        for (SpaceShip spaceShip : spaceShips)
            if (spaceShip.closestApproach < closest.closestApproach)
                closest = spaceShip;

        throw new DoneMessage(closest);
    }

    public class DoneMessage extends RuntimeException {
        public final SpaceShip closest;
        public DoneMessage(SpaceShip closest) {
            this.closest = closest;
        }
    }
}
