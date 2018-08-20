import java.util.ArrayList;
public class Job{
    public final TrainStation pickup;
    public final ArrayList<ItemStack> items;
    public final ArrayList<ItemStack> permItems;//do not modify
    public final TrainStation dropoff;
    public boolean finished = false;
    public Job(TrainStation collect, TrainStation dropoff, ArrayList<ItemStack> items){
        this.pickup = collect;
        this.items = items;
        permItems = new ArrayList<>();
        for(ItemStack stack : items){
            permItems.add(new ItemStack(stack));
        }
        this.dropoff = dropoff;
    }
    public void finish(){
        finished = true;
    }
    @Override
    public String toString(){
        return permItems.get(0)+" "+pickup.shortString()+">"+dropoff.shortString();
    }
}