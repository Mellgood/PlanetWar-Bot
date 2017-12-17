import java.util.*;



public class MyBot {
    
    
    
    
    
    public static void DoTurn_old(PlanetWars pw){
        
        List<Planet> myPlanetList = pw.MyPlanets();
        List<Planet> notMyPlanetList = pw.NotMyPlanets();
        
        myPlanetList.sort(new PlanetFleetSimpleComparator()); //at 0 i have my strongest planet
        notMyPlanetList.sort(new PlanetFleetSimpleComparator()); //at 0 i have the not-mine weakest planet
        
        while(!myPlanetList.isEmpty() && !notMyPlanetList.isEmpty()){ //i want to cycle on all available planets
            Planet srcPlanet = myPlanetList.get(0);
            myPlanetList.remove(srcPlanet); // i do not need it no more on the list
            
            Planet destPlanet = notMyPlanetList.get(0); //notMyPlanetList.size() -1 );
            notMyPlanetList.remove(destPlanet);
            
            if (srcPlanet.NumShips() > destPlanet.NumShips()){
                int numOfShipsToSend = destPlanet.NumShips();
                numOfShipsToSend ++;
                //int numOfReinforcementsComing = 0; //fleets going to dest planet
                
                //finding a good number of ships to send
                for(Fleet f : pw.EnemyFleets()){
                    if(f.DestinationPlanet() == destPlanet.PlanetID()){ //they are going to the selected destination planet
                        numOfShipsToSend += f.NumShips(); //mandare altra flotta per correggere il n
                        //fare lista di pianeti "attivi" ovvero di interesse. questi pianeti devono essere monitorati ogni turno
                        //e comprendono i miei pianeti e i pianeti che sto attaccando.
                        //se trovo flotte verso i pianeti attivi, devo reagire di conseguenza, prima dell'arrivo delle flotte (se il nemico le ha
                        //mandate allora è probabile che ha le sue ragioni e devo difendermi)
                    }                    
                }
                
                if(numOfShipsToSend < srcPlanet.NumShips()){ //ho abbastanza navi e le mando
                    pw.IssueOrder(srcPlanet, destPlanet, numOfShipsToSend );
                }else{ //ho bisogno di piu navi
                    int shipsAvailable = srcPlanet.NumShips() -1;
                    numOfShipsToSend += - shipsAvailable; //sending all ships minus one
                    pw.IssueOrder(srcPlanet, destPlanet, shipsAvailable);
                }
                
            }
        }
        
        //return;
	//pw.IssueOrder(source, dest, numShips);
	
        
    }
    
    
    
    
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
                                
                                if(myPlanet.PlanetID() !=0 && destination.PlanetID() != 0 && requiredShips > 0 && !(firstEnemyComingIn == 10000) && firstEnemyComingIn > pw.Distance(myPlanet.PlanetID(), destination.PlanetID())){
                                    System.err.println("Command 1 source,dest,ships,availableships: " + myPlanet.PlanetID() + " " + destination.PlanetID() + " " + requiredShips + " " + myPlanet.NumShips());
                                    pw.IssueOrder(myPlanet, destination, requiredShips);
                                    
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
                                        
                                        if(myPlanet.NumShips() > ( 1+sheepsNeeded)){
                                            System.err.println("Command 2 (source dest numsheeps mysheeps) " + myPlanet.PlanetID() + " " + fleet.DestinationPlanet() + " " + sheepsNeeded + " " + myPlanet.NumShips() );
                                            pw.IssueOrder(myPlanet.PlanetID(), fleet.DestinationPlanet(), sheepsNeeded);
                                        }
                                    }
                                }
                            }
                        
                    //}
                    
                //}
            }
            
        }
        
        

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

