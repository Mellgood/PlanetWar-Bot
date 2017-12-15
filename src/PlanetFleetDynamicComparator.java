
import java.util.Comparator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Compares planets using 
 *
 * @author cdaga
 */
public class PlanetFleetDynamicComparator implements Comparator<Planet>{
    PlanetWars pw;
    Planet p;
    
    PlanetFleetDynamicComparator(PlanetWars pw, Planet p){
        this.pw = pw;
        this.p = p;
    }
    
    @Override
    public int compare(Planet a, Planet b) {
        int res;
        int numShipsA = a.NumShips();
        int numShipsB = b.NumShips();
        
        //now i have to add the growth rate, but only on enemy planets (neutral planets have no growth rate and i'm not sure if the value is set to 0 on pw)
        if (a.Owner() != 0){
            numShipsA = numShipsA + (pw.Distance(a.PlanetID(), this.p.PlanetID()) * a.GrowthRate());
        }
        if(b.Owner() != 0){
            numShipsB = numShipsB + (pw.Distance(b.PlanetID(), this.p.PlanetID()) * b.GrowthRate());
        }
        
        if(numShipsA == numShipsB){
            res =0;
        }else {
            res = numShipsA >= numShipsB ? 1 : -1 ;
        }
        return res;
    }
    
}
