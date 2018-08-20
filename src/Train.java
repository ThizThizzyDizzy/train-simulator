import java.util.ArrayList;
import java.util.Iterator;
public class Train{
    static double speed = 24;//inches per second  //warp speed: 135664973.3   //regular: 24
    private static double stationAccessSpeed = .6;//multiplier
    private static double stationSpeed = .2;//multiplier
    private static double accelerationTime = 10;//seconds   //warp speed: .010   //regular: 10
    private static double acceleration = speed/accelerationTime/400d;
    private static double trainLength = .08;//percent of mainline length
    Job currentJob = null;
    public double mainlineLocation = 0;//decimal between towns, 7.6 is 60% to besada from qualamazoo
    public double stationLocation = 0;//decimal, -1 = COMING IN, 0 = fully in station, 1 = HEADING OUT, 2 (instant) = on mainline.
    private static double transferRate = 1;//transfer rate of items per second
    ArrayList<ItemStack> inventory = new ArrayList<>();
    public static int maxCargo = 10;
    double velocity = 0;
    private TrainStation targetStation = null;
    public boolean headingLeft = false;
    public Train(){
        mainlineLocation = Core.game.railfactory.location;
    }
    private TrainStation getCurrentStation(){
        if(Math.round(mainlineLocation)!=mainlineLocation){
            return null;
        }
        return Core.game.getStation((int)mainlineLocation);
    }
    public void tick(){
        if(targetStation!=null){
            double targetVelocity = velocity;
            if(getCurrentStation()==targetStation){
                if(stationLocation>=0){
                    stationLocation = 0;
                    targetStation = null;
                }else{
                    double entranceThreshold = TrainStation.stationEntranceLength/(TrainStation.stationEntranceLength+TrainStation.stationLength);
                    if(stationLocation<entranceThreshold-1){
                        targetVelocity = (speed/20)*stationAccessSpeed;
                    }else{
                        targetVelocity = (speed/20)*stationSpeed;
                    }
                }
            }else{
                if(stationLocation<2&&stationLocation>=0){
                    double stationThreshold = TrainStation.stationLength/(TrainStation.stationExitLength+TrainStation.stationLength);
                    if(stationLocation<stationThreshold){
                        targetVelocity = (speed/20)*stationSpeed;
                    }else{
                        targetVelocity = (speed/20)*stationAccessSpeed;
                    }
                }else if(stationLocation>=2){
                    targetVelocity = (speed/20);
                }else{
                    targetVelocity = (speed/20)*stationSpeed;
                }
            }
            if(velocity>targetVelocity){
                velocity-=acceleration;
                if(velocity<targetVelocity){
                    velocity = targetVelocity;
                }
            }
            if(velocity<targetVelocity){
                velocity+=acceleration;
                if(velocity>targetVelocity){
                    velocity = targetVelocity;
                }
            }
            if(stationLocation==2){
                if(mainlineLocation<targetStation.location){
                    mainlineLocation+=velocity/Game.mainlineLength;
                    headingLeft = false;
                    if(mainlineLocation>targetStation.location){
                        mainlineLocation = targetStation.location;
                        stationLocation = -1;
                    }
                }else if(mainlineLocation>targetStation.location){
                    mainlineLocation-=velocity/Game.mainlineLength;
                    headingLeft = true;
                    if(mainlineLocation<targetStation.location){
                        mainlineLocation = targetStation.location;
                        stationLocation = -1;
                    }
                }else{
                    stationLocation = -1;
                }
            }else{
                if(stationLocation<0){
                    stationLocation+=velocity/(TrainStation.stationEntranceLength+TrainStation.stationLength);
                    if(stationLocation>0){
                        stationLocation = 0;
                    }
                }
                if(stationLocation>0||(stationLocation==0&&getCurrentStation()!=targetStation)){
                    stationLocation+=velocity/(TrainStation.stationExitLength+TrainStation.stationLength);
                    if(stationLocation>1){
                        stationLocation = 2;
                    }
                }
            }
            if(targetStation!=Core.game.roundhouse){
                return;
            }
        }else{
            velocity = 0;
        }
        //should be parked.
//        if(currentJob==null){
//            currentJob = Core.game.findJob(); this is handled by the game
//        }
        if(currentJob!=null){
            if(getCurrentStation()==currentJob.pickup){
                if(currentJob.items.isEmpty()){
                    targetStation = currentJob.dropoff;
                }
                for (Iterator<ItemStack> it = currentJob.items.iterator(); it.hasNext();) {
                    ItemStack stack = it.next();
                    //collect items from station
                    ItemStack toTransfer = new ItemStack(stack.item, Math.min(stack.d,transferRate/20));
                    if(getCurrentStation().hasItems(toTransfer)){
                        getCurrentStation().removeItems(toTransfer);
                        addItems(toTransfer);
                        stack.d-=toTransfer.d;
                        if(stack.d<=0){
                            it.remove();
                        }
                        break;
                    }
                }
            }else if(getCurrentStation()==currentJob.dropoff){
                if(!currentJob.items.isEmpty()){
                    targetStation = currentJob.pickup;
                }else if(inventory.isEmpty()){
                    currentJob.finish();
                    currentJob = null;
                }
                for (Iterator<ItemStack> it = inventory.iterator(); it.hasNext();) {
                    ItemStack stack = it.next();
                    //collect items from station
                    ItemStack toTransfer = new ItemStack(stack, Math.min(stack.d,transferRate/20));
                    if(hasItems(toTransfer)&&getCurrentStation().getCargoTotal()<=getCurrentStation().maxCargo-toTransfer.d){
                        getCurrentStation().addItems(toTransfer);
                        stack.d-=toTransfer.d;
                        if(stack.d<=0){
                            it.remove();
                        }
                        break;
                    }
                }
            }else{
                targetStation = currentJob.pickup;
            }
        }else{
            targetStation = Core.game.roundhouse;
        }
    }
    public boolean hasItems(ItemStack input){
        double has = 0;
        for(ItemStack stack : inventory){
            if(stack.item.equals(input.item)){
                has+=stack.d;
            }
            if(has>=input.d){
                return true;
            }
        }
        return false;
    }
    public void removeItems(ItemStack input){
        for(ItemStack stack : inventory){
            if(stack.item.equals(input.item)){
                stack.d-=input.d;
                return;
            }
        }
    }
    public void addItems(ItemStack input){
        for(ItemStack stack : inventory){
            if(stack.item.equals(input.item)){
                stack.d+=input.d;
                return;
            }
        }
        inventory.add(new ItemStack(input));
    }
    public boolean hasItems(ItemStack[] input){
        for(ItemStack stack : input){
            if(!hasItems(stack))return false;
        }
        return true;
    }
    public void removeItems(ItemStack[] input){
        for(ItemStack stack : input){
            removeItems(stack);
        }
    }
    public void addItems(ItemStack[] input){
        for(ItemStack stack : input){
            addItems(stack);
        }
    }
    public double getCargoTotal(){
        double total = 0;
        for(ItemStack stack : inventory){
            total+=stack.d;
        }
        return total;
    }
}