import java.util.*;



public class MyBot {
    
    
    
    
    
    
    public static void DoTurn(PlanetWars pw) {
        //DoTurn_old(pw);
        
        List<Planet> myPlanetList = pw.MyPlanets();
        List<Planet> notMyPlanetList = pw.NotMyPlanets();
        
        myPlanetList.sort(new PlanetFleetSimpleComparator()); //at 0 i have my strongest planet
        notMyPlanetList.sort(new PlanetFleetSimpleComparator()); //at 0 i have the not-mine weakest planet
        
        //every planet can play autonomous its game, but it can not attack the same planet attacked by a strongest planet
        for (Planet myPlanet : myPlanetList){
            //every planet has its own parameter to be set on the dynamic comparator
            //notMyPlanetList.sort(new PlanetFleetDynamicComparator(pw, p));
            for(Fleet fleet : pw.EnemyFleets()){
                //If I'm in range to counter the attack..
                //if (fleet.TurnsRemaining() > pw.Distance(myPlanet.PlanetID(), fleet.DestinationPlanet())){
                    Planet destination = pw.GetPlanet(fleet.DestinationPlanet());
                    //if I have more ships than the destination planet
                    //if (myPlanet.NumShips() > 1){
                    
                        //If enemy are attempting to attack me, the idea is: def a planet is better than attack an enemy one (check it XD)
                        if (destination.Owner() == 1){
                            int myShipsAtArrival = destination.NumShips() + (destination.GrowthRate() * (fleet.TurnsRemaining() - 1));
                            int enemyShipsAtArrival = 0;
                            for(Fleet myFleet : pw.MyFleets()){
                                if (myFleet.DestinationPlanet() == destination.PlanetID()){
                                    myShipsAtArrival += myFleet.NumShips();
                                }
                            }
                            // i want to know the number of enemy ships wich are coming
                            int firstEnemyComingIn = 10000;
                            for(Fleet enemyFleet : pw.EnemyFleets()){
                                if(enemyFleet.DestinationPlanet() == destination.PlanetID()){
                                    enemyShipsAtArrival += enemyFleet.NumShips();
                                    if(enemyFleet.TurnsRemaining() < firstEnemyComingIn){
                                        firstEnemyComingIn = enemyFleet.TurnsRemaining();
                                    }
                                }
                            }
                            //se nel pianeta non ci sono abbastanza navi per difendersi, invio rinforsi.
                            if (enemyShipsAtArrival > myShipsAtArrival){
                                int requiredShips = enemyShipsAtArrival - myShipsAtArrival;
                                if(requiredShips > myPlanet.NumShips()){
                                    requiredShips = (myPlanet.NumShips() -1);
                                }
                                //System.err.println("Command 1 source,dest,ships,availableships: " + myPlanet.PlanetID() + " " + destination.PlanetID() + " " + requiredShips + " " + myPlanet.NumShips());
                                
                                if(myPlanet.PlanetID() != destination.PlanetID() && myPlanet.PlanetID() !=0 && destination.PlanetID() != 0 && requiredShips > 0 && !(firstEnemyComingIn == 10000) && firstEnemyComingIn > pw.Distance(myPlanet.PlanetID(), destination.PlanetID())){
                                    System.err.println("Command DEF ALLIED PLANET source,dest,ships,availableships: " + myPlanet.PlanetID() + " " + destination.PlanetID() + " " + requiredShips + " " + myPlanet.NumShips());
                                    boolean isUnderAttack = false;
                                    for(Fleet f : pw.EnemyFleets()){
                                        if (f.DestinationPlanet() == myPlanet.PlanetID()){
                                            isUnderAttack = true;
                                        }
                                    }
                                    
                                    boolean yetReinforced = false;
                                    for(Fleet f : pw.MyFleets()){
                                        //System.err.println("BBB");
                                        if(f.DestinationPlanet() == destination.PlanetID()){
                                            yetReinforced = true;
                                            //System.err.println("AAA");
                                        }
                                    }
                                    
                                    if(!isUnderAttack && !yetReinforced){
                                        pw.IssueOrder(myPlanet, destination, requiredShips);
                                        Fleet f1 = new Fleet(1, requiredShips, myPlanet.PlanetID(), destination.PlanetID(), pw.Distance(myPlanet.PlanetID(), destination.PlanetID()), pw.Distance(myPlanet.PlanetID(), destination.PlanetID()));
                                        pw.addFleet(f1);
                                        myPlanet.RemoveShips(requiredShips);
                                        //System.err.println("--------------------SIZE: " + pw.MyFleets().size());
                                        //break;
                                    }
                                    
                                    
                                    
                                }
                                
                            }
                            
                            
                        }else  
                            
                            //if the enemy Is trying to attack a neutral planet, try to deny it (the idea is:
                            //do not let enemy logic control the game. It is more probable that
                            //enemy logic will do stupid or inefficent things if it can not do what it wants to do)
                            if(pw.GetPlanet(fleet.DestinationPlanet()).Owner() == 0){
                                int myShipsAtArrival = 0;
                                int enemyShipsComing = 0;
                                //aggiungo le mie flotte gia in viaggio
                                pw.EnemyFleets().sort(new FleetDistanceComparator());
                                List<Fleet> enemyFleetsToDestinationList = new LinkedList<>();
                                for(Fleet myFleets : pw.MyFleets()){
                                    if(myFleets.DestinationPlanet() == destination.PlanetID()){
                                        myShipsAtArrival += myFleets.NumShips();
                                        
                                    }
                                }
                                
                                for(Fleet enemyFleet : pw.EnemyFleets()){
                                    if(enemyFleet.DestinationPlanet() == destination.PlanetID()){
                                        enemyShipsComing += enemyFleet.NumShips();
                                        enemyFleetsToDestinationList.add(fleet);
                                    }
                                }
                                int sheepsNeeded = enemyShipsComing - destination.NumShips() + destination.GrowthRate() +1 ;
                            
                                if(myPlanet.NumShips() > sheepsNeeded){
                                    System.err.println("I Wanna go to neutral. enemy fleet turns remaining: " + enemyFleetsToDestinationList.get(0).TurnsRemaining());
                                    System.err.println("my distance is: " + pw.Distance(myPlanet.PlanetID(), destination.PlanetID()));
                                    if(enemyFleetsToDestinationList.get(0).TurnsRemaining() == (-1 + pw.Distance(myPlanet.PlanetID(), destination.PlanetID()))){
                                        
                                        boolean yetReinforced = false;
                                        for(Fleet f : pw.MyFleets()){
                                            //System.err.println("BBB");
                                            if(f.DestinationPlanet() == destination.PlanetID()){
                                                yetReinforced = true;
                                                //System.err.println("AAA");
                                            }
                                        }
                                        
                                        if(!yetReinforced && myPlanet.NumShips() > ( 1+sheepsNeeded)){
                                            System.err.println("Command DEF NEUTRAL PLANET (source dest numsheeps mysheeps) " + myPlanet.PlanetID() + " " + fleet.DestinationPlanet() + " " + sheepsNeeded + " " + myPlanet.NumShips() );
                                            pw.IssueOrder(myPlanet.PlanetID(), fleet.DestinationPlanet(), sheepsNeeded);
                                            pw.MyFleets().add(new Fleet(1, sheepsNeeded, myPlanet.PlanetID(), destination.PlanetID(), pw.Distance(myPlanet.PlanetID(), destination.PlanetID()), pw.Distance(myPlanet.PlanetID(), destination.PlanetID())));
                                            myPlanet.RemoveShips(sheepsNeeded);
                                        }
                                    }
                                }
                            }
                        
                    //}
                    
                //}
            }
            
        }
        
        //_______________________________________________________
        //ATTACK PHASE
        //_______________________________________________________
        
        //List<Planet> myPlanetList = pw.MyPlanets();
        //List<Planet> notMyPlanetList = pw.NotMyPlanets();
        
        myPlanetList.sort(new PlanetFleetSimpleComparator()); //at 0 i have my strongest planet
        notMyPlanetList.sort(new PlanetFleetSimpleComparator()); //at 0 i have the not-mine weakest planet
        
        for (Planet source : myPlanetList){
            int distance = 1000;
            Planet destination = null;
            int numShips = 0;
            if (source.NumShips() > 20){
                numShips = source.NumShips() /2;
            }
            
            for(Planet dest : myPlanetList){
                if (dest.GrowthRate() > source.GrowthRate()){
                    if (pw.Distance(source.PlanetID(), dest.PlanetID()) < distance){
                        distance = pw.Distance(source.PlanetID(), dest.PlanetID());
                        destination = dest;
                    }
                }
            }
            
            boolean underAttack = false;
            for(Fleet f : pw.EnemyFleets()){
                if (f.DestinationPlanet() == source.PlanetID()){
                    underAttack = true;
                }
            }
            
            if (!underAttack && source != null && destination != null && source.PlanetID() != destination.PlanetID() && numShips != 0){
                pw.IssueOrder(source, destination, distance);
            }
        }
        
        
        
        
        
        
        
//        for(Planet source : myPlanetList){
//            Planet dest = notMyPlanetList.get(0);
//            boolean isAttackable = true;
//            int destShips = 1+ dest.NumShips();
//            //se è del nemico, aggiungo il growth rate
//            if(dest.Owner() == 2){
//                destShips += dest.GrowthRate() * pw.Distance(source.PlanetID(), dest.PlanetID());
//            }
//            for (Fleet enemyFleet : pw.EnemyFleets()){
//                //se sono sotto attacco, è la difesa a gestire la cosa.It's not my business :P
//                //se il pianeta è già attaccato da un nemico, non mi interessa attaccarlo (la difesa escogiterà un piano per prevenire l'espansione nemica in zone vicine)
//                if (enemyFleet.DestinationPlanet() == dest.PlanetID() || enemyFleet.DestinationPlanet() == source.PlanetID()){
//                    isAttackable = false;
//                }
//            }            
//            
//            if (isAttackable && source.NumShips() > destShips){
//                System.err.println("Command ATTACK!! source,dest,destShips,available: " + source + " " + dest + " " + destShips + " " + source.NumShips());
//                pw.IssueOrder(source, dest, destShips);
//            }
//        }
        
        

    }
    

    public static void main(String[] args) {
	String line = "";
	String message = "";
	int c;
	try {
	    while ((c = System.in.read()) >= 0) {
		switch (c) {
		case '\n':
		    if (line.equals("go")) {
			PlanetWars pw = new PlanetWars(message);
			DoTurn(pw);
		        pw.FinishTurn();
			message = "";
		    } else {
			message += line + "\n";
		    }
		    line = "";
		    break;
		default:
		    line += (char)c;
		    break;
		}
	    }
	} catch (Exception e) {
	    // Owned.
	}
    }
}

