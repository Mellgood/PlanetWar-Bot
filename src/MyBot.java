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
        DoTurn_old(pw);
        
        List<Planet> myPlanetList = pw.MyPlanets();
        List<Planet> notMyPlanetList = pw.NotMyPlanets();
        
        myPlanetList.sort(new PlanetFleetSimpleComparator()); //at 0 i have my strongest planet
        notMyPlanetList.sort(new PlanetFleetSimpleComparator()); //at 0 i have the not-mine weakest planet
        
        //every planet can play autonomous its game, but it can not attack the same planet attacked by a strongest planet
        for (Planet p : myPlanetList){
            //every planet has its own parameter to be set on the dynamic comparator
            notMyPlanetList.sort(new PlanetFleetDynamicComparator(pw, p));
            
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

