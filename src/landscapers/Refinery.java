package landscapers;
import battlecode.common.*;

public class Refinery extends Building {
    public Refinery(RobotController r) {
        super(r);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        comms.broadcastRefineryCreation(rc.getLocation());
    }
}
