package landscapers;
import battlecode.common.*;
import java.util.ArrayList;

public class Miner extends Unit {

    int numDesignSchools = 0;
    int numRefineries = 0;
    ArrayList<MapLocation> soupLocations = new ArrayList<MapLocation>();
    ArrayList<MapLocation> refineryLocations = new ArrayList<MapLocation>();

    public Miner(RobotController r) {
        super(r);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        if (numDesignSchools == 0) {
            numDesignSchools += comms.getNewDesignSchoolCount();
        }
        
        if (numRefineries < 1) {
            MapLocation loc = comms.getNewRefineryLocation();
            if (loc.x != -1000){
                numRefineries += 1;
                refineryLocations.add(new MapLocation(loc.x, loc.y));
            }
        }

        comms.updateSoupLocations(soupLocations);
        checkIfSoupGone();

        for (Direction dir : Util.directions)
            if (tryMine(dir)) {
                System.out.println("I mined soup! " + rc.getSoupCarrying());
                MapLocation soupLoc = rc.getLocation().add(dir);
                if (!soupLocations.contains(soupLoc)) {
                    comms.broadcastSoupLocation(soupLoc);
                }
            }
        // mine first, then when full, deposit
        for (Direction dir : Util.directions)
            if (tryRefine(dir))
                System.out.println("I refined soup! " + rc.getTeamSoup());

        if (numDesignSchools < 1){
        	//pick direction
        	Direction dir = Util.randomDirection();
    		if(!rc.getLocation().add(dir).isAdjacentTo(hqLoc))
    			if(tryBuild(RobotType.DESIGN_SCHOOL, dir))
                    System.out.println("created a design school");
        }

        if (numRefineries < 1){
            System.out.println("trying to build a refinery!");
        	//pick direction
        	Direction dir = Util.randomDirection();
    		if(!rc.getLocation().add(dir).isAdjacentTo(hqLoc))
    			if(tryBuild(RobotType.REFINERY, dir)) {
                    System.out.println("created a refinery");
                }
        }

        if (rc.getSoupCarrying() == RobotType.MINER.soupLimit) {
            // first attempt to go back to a refinery, if any
            if(refineryLocations.size() > 0){
                if(nav.goTo(refineryLocations.get(0)))
                    System.out.println("moved towards refinary");
            } // otherwise refine at HQ 
            else if (nav.goTo(hqLoc))
                System.out.println("moved towards HQ");
        } else if (soupLocations.size() > 0) {
            nav.goTo(soupLocations.get(0));
            rc.setIndicatorLine(rc.getLocation(), soupLocations.get(0), 255, 255, 0);
        } else if (nav.goTo(Util.randomDirection())) {
            // otherwise, move randomly as usual
            System.out.println("I moved randomly!");
        }
    }

    /**
     * Attempts to mine soup in a given direction.
     *
     * @param dir The intended direction of mining
     * @return true if a move was performed
     * @throws GameActionException
     */
    boolean tryMine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canMineSoup(dir)) {
            rc.mineSoup(dir);
            return true;
        } else return false;
    }

    /**
     * Attempts to refine soup in a given direction.
     *
     * @param dir The intended direction of refining
     * @return true if a move was performed
     * @throws GameActionException
     */
    boolean tryRefine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canDepositSoup(dir)) {
            rc.depositSoup(dir, rc.getSoupCarrying());
            return true;
        } else return false;
    }

    void checkIfSoupGone() throws GameActionException {
        if (soupLocations.size() > 0) {
            MapLocation targetSoupLoc = soupLocations.get(0);
            if (rc.canSenseLocation(targetSoupLoc)
                    && rc.senseSoup(targetSoupLoc) == 0) {
                soupLocations.remove(0);
            }
        }
    }
}
