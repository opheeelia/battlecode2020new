package landscapers;
import battlecode.common.*;

public class Landscaper extends Unit {
	
	boolean north, east, south, west, stationed = false;

    public Landscaper(RobotController r) {
        super(r);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        // first, save HQ by trying to remove dirt from it
        if (hqLoc != null && hqLoc.isAdjacentTo(rc.getLocation())) {
            Direction dirtohq = rc.getLocation().directionTo(hqLoc);
            if(rc.canDigDirt(dirtohq)){
                rc.digDirt(dirtohq);
            }
        }

        if(rc.getDirtCarrying() == 0){
            tryDig();
        }
        
        //rotate around HQ until you can stand in north, east, south, or west of it. if near hq
        
        if (!stationed && hqLoc != null && hqLoc.isWithinDistanceSquared(rc.getLocation(), 4)) {
            for(Direction dir: Util.cornerDirections) {
                if(rc.getLocation().equals(hqLoc.add(dir))) {
                	stationed = true;
                	System.out.println("im stationed");
                }
            }
            if (!stationed) {
	            Direction hqDir = rc.getLocation().directionTo(hqLoc);
	        	Direction[] toTry = {hqDir, hqDir.rotateRight(), hqDir.rotateRight().rotateRight()};
	            for (Direction d : toTry){
	                if(nav.tryMove(d)) {
	                	System.out.println("rotating around the hq");
	                }
	            }
            }
        }
        
        //IF NEAR HQ
        //for loop to check if its in the right place
        //if not stationed by the end of for loop, then move. 


        MapLocation bestPlaceToBuildWall = null;
        // find best place to build
        if(hqLoc != null) {
            int lowestElevation = 9999999;
            for (Direction dir : Util.directions) {
                MapLocation tileToCheck = hqLoc.add(dir);
                if(rc.getLocation().distanceSquaredTo(tileToCheck) < 4
                        && rc.canDepositDirt(rc.getLocation().directionTo(tileToCheck))) {
                    if (rc.senseElevation(tileToCheck) < lowestElevation) {
                        lowestElevation = rc.senseElevation(tileToCheck);
                        bestPlaceToBuildWall = tileToCheck;
                        System.out.println("found place to build: " + bestPlaceToBuildWall);
                    }
                }
            }
        }

        if (stationed){
            // build the wall
            if (bestPlaceToBuildWall != null) {
                rc.depositDirt(rc.getLocation().directionTo(bestPlaceToBuildWall));
                rc.setIndicatorDot(bestPlaceToBuildWall, 0, 255, 0);
                System.out.println("building a wall");
            }
        }else {
	        // otherwise try to get to the hq
	        if(hqLoc != null){
	            nav.goTo(hqLoc);
	            System.out.println("trying to get to hq");
	        } else {
	            nav.goTo(Util.randomDirection());
	            System.out.println("tryna move random");
	        }
        }
    }

    boolean tryDig() throws GameActionException {
        Direction dir;
        if(hqLoc == null){
            dir = Util.randomDirection();
        } else {
            dir = hqLoc.directionTo(rc.getLocation());
        }
        if(rc.canDigDirt(dir)){
            rc.digDirt(dir);
            rc.setIndicatorDot(rc.getLocation().add(dir), 255, 0, 0);
            return true;
        }
        return false;
    }
}