import body.Body;
import body.SpaceShip;
import body.StationaryBody;
import environments.MissionEnvironment;
import support.Constants.Planet;
import support.Constants.Moon;
import support.LaunchData;

import java.util.Calendar;

// note: all units are SI (m, s, kg, ...)
public class Main {
    public static void main(String[] args) {

        try {

            Body sun = new StationaryBody(Planet.SUN);

            Body mercury = new Body(Planet.MERCURY);
            Body venus = new Body(Planet.VENUS);
            Body earth = new Body(Planet.EARTH);
            Body mars = new Body(Planet.MARS);
            Body jupiter = new Body(Planet.JUPITER);
            Body saturn = new Body(Planet.SATURN);
            Body uranus = new Body(Planet.URANUS);
            Body neptune = new Body(Planet.NEPTUNE);

            Body moon = new Body(Moon.MOON);
            Body titan = new Body(Moon.TITAN);

            Calendar endTime = Calendar.getInstance();
            endTime.set(Calendar.YEAR, 2028);
            endTime.set(Calendar.MONTH, Calendar.JANUARY);
            endTime.set(Calendar.DAY_OF_MONTH, 1);
            endTime.set(Calendar.HOUR_OF_DAY, 0);
            endTime.set(Calendar.MINUTE, 0);
            endTime.set(Calendar.SECOND, 0);

            MissionEnvironment env = new MissionEnvironment(
                    endTime,
                    sun, mercury, venus, earth, mars, jupiter, saturn, uranus, neptune,
                    moon, titan
            );

            double speed = 15.477791e3;
            // 15.47779e3 and close speeds work too

            double[] zSpeeds = new double[]{
                    0,
                    -1,
                    1
            };

            int i = 0;
            for (double zSpeed : zSpeeds)
                env.addLaunch(new LaunchData(
                        titan, "space ship " + i++ + " z-speed: " + zSpeed + "m/s",
                        speed, 0, 0,
                        2019, Calendar.MARCH, 28,
                        0, 0, 0
                ));

            env.run(10);
        } catch (MissionEnvironment.DoneMessage doneMessage) {
            SpaceShip closest = doneMessage.closest;

            System.out.println("best ship:");
            System.out.println("  closest approach: " + closest.closestApproach);
            System.exit(1);
        }
    }
}