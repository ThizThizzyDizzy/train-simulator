import java.util.ArrayList;
import org.lwjgl.input.Keyboard;
public class TrainStation implements Comparable<TrainStation>{
    public int location;
    private final String name;
    public final String description;
    public ArrayList<Function> functions = new ArrayList<>();
    public ArrayList<ItemStack> inventory = new ArrayList<>();
    public int maxCargo = 100;
    public static double stationEntranceLength = 101.34;//inches
    public static double stationExitLength = 63.27;//inches
    public static double stationLength = 117.09;//inches
    private ArrayList<ItemStack> tempModifications = new ArrayList<>();
    public TrainStation(int location, String name, String description){
        this.location = location;
        this.name = name;
        this.description = description;
    }
    public TrainStation addFunction(ItemStack input, ItemStack... output){
        functions.add(new Function(new ItemStack[]{input}, output));
        return this;
    }
    public TrainStation addFunction(ItemStack output){
        functions.add(new Function(null, new ItemStack[]{output}));
        return this;
    }
    public TrainStation addFunction(Function function){
        functions.add(function);
        return this;
    }
    public void addFunction(Function... functions){
        for(Function func : functions){
            addFunction(func);
        }
    }
    public TrainStation addFunction(ItemStack[] inputs, ItemStack... output){
        functions.add(new Function(inputs, output));
        return this;
    }
    public void tick(){
        for(Function function : functions){
            function.tick(this);
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
        if(input.d<0){
            removeItems(new ItemStack(input, -input.d));
            return;
        }
//        if(getCargoTotal()>=maxCargo){
//            throw new IllegalStateException("Cannot add "+input+", Station is full!");
//        }
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
    @Override
    public String toString(){
        return Keyboard.isKeyDown(Keyboard.KEY_SPACE)?description:name+" ("+location+")";
    }
    @Override
    public int compareTo(TrainStation o){
        return location-o.location;
    }
    public void tempRemoveItems(ItemStack stack){
        removeItems(stack);
        tempModifications.add(new ItemStack(stack));
    }
    public void tempAddItems(ItemStack stack){
        addItems(stack);
        tempModifications.add(new ItemStack(stack, -stack.d));
    }
    public void resetTemps(){
        for(ItemStack stack : tempModifications){
            addItems(stack);
        }
        tempModifications.clear();
    }
    public String shortString(){
        return ""+location;
    }
}