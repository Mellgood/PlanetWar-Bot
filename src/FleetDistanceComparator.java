
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
public class FleetDistanceComparator implements Comparator <Fleet>{
    @Override
    public int compare(Fleet a, Fleet b) {
        int res;
        if(a.TurnsRemaining() == b.TurnsRemaining()){
            res =0;
        }else {
            res = a.TurnsRemaining() < b.TurnsRemaining()? 1 : -1 ;
        }
        return res;
    }
    
}
