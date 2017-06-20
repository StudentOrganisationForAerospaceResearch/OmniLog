package ca.ucalgary.soar.omnilog;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Alex Hamilton on 2017-06-19.
 * University of Calgary
 * alexander.hamilton@ucalgary.ca
 */

public class ParachuteController {

    List<Integer> firstHalf, secondHalf;
    boolean primary_deployed, secondary_deployed;


    public void Parachute_Controller(){
        firstHalf = new LinkedList<Integer>();
        secondHalf = new LinkedList<Integer>();
        primary_deployed=false;
        secondary_deployed=false;
    }


    public void deploy_Primary(){
        primary_deployed=true;
        return;
    }

    public void deploy_Secondary(){
        secondary_deployed=true;
        return;
    }

}
