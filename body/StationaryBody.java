package body;

import support.BodyData;

import java.util.List;

public class StationaryBody extends Body {
    public StationaryBody(BodyData data) {
        super(data);
    }

    @Override
    public void iteratePhysics(List<Body> allBodies, double timeStepInSeconds) {
        // do nothing
    }
}
