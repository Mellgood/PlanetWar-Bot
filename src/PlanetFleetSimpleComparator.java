
import java.util.Comparator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author cdaga
 */
public class PlanetFleetSimpleComparator implements Comparator<Planet> {
    @Override
    public int compare(Planet a, Planet b) {
        int res;
        if(a.NumShips() == b.NumShips()){
            res =0;
        }else {
            res = a.NumShips() >= b.NumShips() ? 1 : -1 ;
        }
        return res;
    }
}
