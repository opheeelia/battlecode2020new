package landscapers;
import battlecode.common.*;

public class HQ extends Shooter {
    static int numMiners = 0;

    public HQ(RobotController r) throws GameActionException {
        super(r);

        comms.sendHqLoc(rc.getLocation());
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        if (turnCount == 1) {
            MapLocation[] soupLocs = rc.senseNearbySoup();
            for(MapLocation loc : soupLocs) {
                comms.broadcastSoupLocation(loc);
            }
        }

        if(numMiners < 5) {
            for (Direction dir : Util.directions)
                if(tryBuild(RobotType.MINER, dir)){
                    numMiners++;
                }
        }
    }
}