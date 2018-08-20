import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
public class Game{
    static double mainlineLength = 1055.34;//inches
    public ArrayList<Train> trains = new ArrayList<>();
    public ArrayList<TrainStation> stations = new ArrayList<>();
    public ArrayList<Job> currentJobs = new ArrayList<>();
    Random rand = new Random(1);
    final TrainStation warehouse;
    final TrainStation roundhouse;
    final TrainStation railfactory;
    int timeSpeed = 1;
    public static final ArrayList<Item> warehouseItems = new ArrayList<>();
    static{
        warehouseItems.add(Item.FURNITURE);
        warehouseItems.add(Item.JEWELRY);
        warehouseItems.add(Item.BOOK);
        warehouseItems.add(Item.STEAK);
        warehouseItems.add(Item.COOKED_FISH);
        warehouseItems.add(Item.BREAD);
        warehouseItems.add(Item.FUEL);
        warehouseItems.add(Item.DIRT);
        warehouseItems.add(Item.AXE);
        warehouseItems.add(Item.PICKAXE);
        warehouseItems.add(Item.HOE);
        warehouseItems.add(Item.SHOVEL);
        warehouseItems.add(Item.SWORD);
        warehouseItems.add(Item.REVOLVER);
        warehouseItems.add(Item.PISTOL);
        warehouseItems.add(Item.RIFLE);
        warehouseItems.add(Item.HELMET);
        warehouseItems.add(Item.CHESTPLATE);
        warehouseItems.add(Item.LEGGINGS);
        warehouseItems.add(Item.BOOTS);
        warehouseItems.add(Item.PLASTIC);
        warehouseItems.add(Item.POTTERY);
        warehouseItems.add(Item.GLASS);
        warehouseItems.add(Item.STONE);
    }
    public Job findJob(){
        for(Iterator<Job> it = currentJobs.iterator(); it.hasNext();){
            Job job = it.next();
            if(job.finished){
                it.remove();
            }
        }
        for(TrainStation pickup : stations){
            for(Job job : currentJobs){
                if(job.pickup==pickup){
                    for(ItemStack stack : job.items){
                        pickup.tempRemoveItems(stack);
                    }
                }
            }
            for(Function pickupFunc : pickup.functions){
                for(ItemStack output : pickupFunc.outputs){
                    if(pickup.hasItems(new ItemStack(output, Train.maxCargo))){
                        for(TrainStation dropoff : stations){
                            for(Job job : currentJobs){
                                if(job.dropoff==dropoff){
                                    for(ItemStack stack : job.permItems){
                                        dropoff.tempAddItems(stack);
                                    }
                                }
                            }
                            for(Function dropoffFunc : dropoff.functions){
                                if(dropoff.hasItems(new ItemStack(output, dropoff.maxCargo*(3/10d))))continue;
                                if(dropoffFunc.inputs==null)continue;
                                INPUT:for(ItemStack input : dropoffFunc.inputs){
                                    if(dropoff.hasItems(new ItemStack(input, Train.maxCargo)))continue;
                                    if(input.item.equals(output.item)){
                                        if(!dropoff.hasItems(new ItemStack(input, Train.maxCargo))){
                                            ArrayList<ItemStack> items = new ArrayList<>();
                                            items.add(new ItemStack(input, Train.maxCargo));
                                            Job job = new Job(pickup, dropoff, items);
                                            pickup.resetTemps();
                                            dropoff.resetTemps();
                                            return job;
                                        }
                                    }
                                }
                            }
                            dropoff.resetTemps();
                        }
                        if(warehouseItems.contains(output.item)){
                            ArrayList<ItemStack> items = new ArrayList<>();
                            items.add(new ItemStack(output, Train.maxCargo));
                            Job job = new Job(pickup, warehouse, items);
                            pickup.resetTemps();
                            return job;
                        }
                    }
                }
            }
            pickup.resetTemps();
        }
        return null;
    }
    public Game(){
        addStation("Drantevan", "Gold Mine").addFunction(new ItemStack(Item.GOLD, 1));
        addStation("Dentopeus", "Steel Mill").addFunction(Recipe.STEEL);
        addStation("Hefensay", "Furniture Factory").addFunction(Recipe.FURNITURE);
        addStation("Katromeda", "Oil Well").addFunction(new ItemStack(Item.OIL, 10));
        addStation("Timaleus", "Coal Mine").addFunction(new ItemStack(Item.COAL, 10));
        addStation("Qualamazoo", "Woodcutting Outpost").addFunction(new ItemStack(Item.LOG, 10));
        addStation("Beseda", "Butcher").addFunction(Recipe.BUTCHER);
        addStation("Quantrento", "Iron Mine").addFunction(new ItemStack(Item.IRON_ORE, 10));
        addStation("Kentraceyo", "Tool Factory").addFunction(Recipe.AXE,Recipe.PICKAXE,Recipe.HOE,Recipe.SHOVEL);
        addStation("Gemantadosaim", "Paper Mill").addFunction(Recipe.PAPER);
        addStation("Gandestopia", "Jewlery Factory").addFunction(Recipe.JEWELRY);
        addStation("Covanoi", "Sawmill").addFunction(Recipe.SAWMILL);
        addStation("Fondescent", "Wheat Farm").addFunction(new ItemStack(Item.WHEAT, 10));
        addStation("Sesodia", "Fish Farm").addFunction(new ItemStack(Item.FISH, 10));
        addStation("Gondotemas", "Book Factory").addFunction(Recipe.BOOK);
        addStation("Bontefansaeyo", "Diamond Mine").addFunction(new ItemStack(Item.DIAMOND, 2));
        addStation("Smithonia", "Cow farm").addFunction(new ItemStack(Item.COW, 10));
        addStation("Bakentesso", "Meat cookery").addFunction(Recipe.STEAK,Recipe.FISH);
        addStation("Felimantena", "Bakery").addFunction(Recipe.BREAD);
        addStation("Centenia", "Weapon Factory").addFunction(Recipe.SWORD,Recipe.REVOLVER,Recipe.PISTOL,Recipe.RIFLE);
        roundhouse = addStation("Cetopentolia", "Roundhouse");
        addStation("Alfheim", "City");
        addStation("Benteseria", "Armor Factory").addFunction(Recipe.HELMET,Recipe.CHESTPLATE,Recipe.LEGGINGS,Recipe.BOOTS);
        addStation("Generalimus", "Fuel Factory").addFunction(Recipe.FUEL);
        addStation("Geffandemanko", "Cotton farm").addFunction(new ItemStack(Item.COTTON, 10));
        warehouse = addStation("Betopatogandeso", "Warehouse");
        warehouse.maxCargo*= 100;
        railfactory = addStation("Kentrocenta", "Railroad factory");
        addStation("Pentesora", "Plastic Factory").addFunction(Recipe.PLASTIC);
        addStation("Cetopentenia", "Quarry").addFunction(new ItemStack(Item.DIRT, 3)).addFunction(new ItemStack(Item.SAND, 2)).addFunction(new ItemStack(Item.CLAY, 1)).addFunction(new ItemStack(Item.STONE, 4));
        addStation("Jalegando", "Iron Smeltery").addFunction(Recipe.IRON);
        addStation("Devonotia", "Pottery Factory").addFunction(Recipe.POTTERY);
        addStation("Gelasena", "Glass Factory").addFunction(Recipe.GLASS);
        addStation("Gendoventaline", "Home");
        int count = stations.size();
        for(int i = 0; i<count; i++){
            stations.add(new Fields());
        }
        Collections.shuffle(stations, rand);
        for (int i = 0; i < stations.size(); i++) {
            TrainStation station = stations.get(i);
            if(station.location<0)continue;
            station.location = i+1;
        }
        for (Iterator<TrainStation> it = stations.iterator(); it.hasNext();) {
            TrainStation station = it.next();
            if(station.location==-1){
                it.remove();
            }
        }
    }
    private TrainStation addStation(int location, String name, String desc){
        TrainStation station = new TrainStation(location, name, desc);
        stations.add(station);
        return station;
    }
    private TrainStation addStation(String name, String desc){
        TrainStation station = new TrainStation(0, name, desc);
        stations.add(station);
        return station;
    }
    public void tick(){
        for(int i = 0; i<timeSpeed; i++){
            for(TrainStation station : stations){
                station.tick();
            }
            for(Train train : trains){
                train.tick();
            }
            Job NONE = new Job(null, null, new ArrayList<>());
            Job job;
            do{
                job = findJob();
                if(job!=null){
                    double distance = Double.MAX_VALUE;
                    Train t = null;
                    for(Train train : trains){
                        if(train.currentJob!=null)continue;
                        double dist = Math.abs(train.mainlineLocation-job.pickup.location);
                        if(dist<distance){
                            distance = dist;
                            t = train;
                        }
                    }
                    if(t!=null){
                        t.currentJob = job;
                        currentJobs.add(job);
                        job = NONE;
                    }
                }
            }while(job==NONE);
        }
    }
    public TrainStation getStation(int loc){
        for(TrainStation station : stations){
            if(station.location==loc){
                return station;
            }
        }
        return null;
    }
    private static class Recipe{
        private static Function STEEL = new Function(new ItemStack[]{new ItemStack(Item.COAL, 2), new ItemStack(Item.IRON)}, new ItemStack(Item.STEEL));
        private static Function FURNITURE = new Function(new ItemStack[]{new ItemStack(Item.LUMBER), new ItemStack(Item.COTTON, 3)}, new ItemStack(Item.FURNITURE));
        private static Function BUTCHER = new Function(new ItemStack[]{new ItemStack(Item.COW)}, new ItemStack(Item.BEEF), new ItemStack(Item.LEATHER));
        private static Function AXE = new Function(new ItemStack[]{new ItemStack(Item.LUMBER), new ItemStack(Item.STEEL, 2)}, new ItemStack(Item.AXE));
        private static Function PICKAXE = new Function(new ItemStack[]{new ItemStack(Item.LUMBER), new ItemStack(Item.STEEL, 2)}, new ItemStack(Item.PICKAXE));
        private static Function HOE = new Function(new ItemStack[]{new ItemStack(Item.LUMBER), new ItemStack(Item.STEEL, 1)}, new ItemStack(Item.HOE));
        private static Function SHOVEL = new Function(new ItemStack[]{new ItemStack(Item.LUMBER), new ItemStack(Item.STEEL, 1)}, new ItemStack(Item.SHOVEL));
        private static Function PAPER = new Function(new ItemStack[]{new ItemStack(Item.SCRAP_WOOD)}, new ItemStack(Item.PAPER, 5));
        private static Function JEWELRY = new Function(new ItemStack[]{new ItemStack(Item.GOLD, 2), new ItemStack(Item.DIAMOND)}, new ItemStack(Item.JEWELRY));
        private static Function SAWMILL = new Function(new ItemStack[]{new ItemStack(Item.LOG, 2)}, new ItemStack(Item.LUMBER), new ItemStack(Item.SCRAP_WOOD));
        private static Function BOOK = new Function(new ItemStack[]{new ItemStack(Item.PAPER, 3), new ItemStack(Item.LEATHER)}, new ItemStack(Item.BOOK));
        private static Function STEAK = new Function(new ItemStack[]{new ItemStack(Item.BEEF)}, new ItemStack(Item.STEAK));
        private static Function FISH = new Function(new ItemStack[]{new ItemStack(Item.FISH)}, new ItemStack(Item.COOKED_FISH));
        private static Function BREAD = new Function(new ItemStack[]{new ItemStack(Item.WHEAT, 3)}, new ItemStack(Item.BREAD));
        private static Function SWORD = new Function(new ItemStack[]{new ItemStack(Item.LEATHER), new ItemStack(Item.STEEL, 4)}, new ItemStack(Item.SWORD));
        private static Function REVOLVER = new Function(new ItemStack[]{new ItemStack(Item.LUMBER), new ItemStack(Item.STEEL, 3)}, new ItemStack(Item.REVOLVER));
        private static Function PISTOL = new Function(new ItemStack[]{new ItemStack(Item.LUMBER), new ItemStack(Item.STEEL, 6)}, new ItemStack(Item.PISTOL));
        private static Function RIFLE = new Function(new ItemStack[]{new ItemStack(Item.LUMBER), new ItemStack(Item.STEEL, 9)}, new ItemStack(Item.RIFLE));
        private static Function HELMET = new Function(new ItemStack[]{new ItemStack(Item.STEEL, 4)}, new ItemStack(Item.HELMET));
        private static Function CHESTPLATE = new Function(new ItemStack[]{new ItemStack(Item.STEEL, 5)}, new ItemStack(Item.CHESTPLATE));
        private static Function LEGGINGS = new Function(new ItemStack[]{new ItemStack(Item.STEEL, 6)}, new ItemStack(Item.LEGGINGS));
        private static Function BOOTS = new Function(new ItemStack[]{new ItemStack(Item.STEEL, 2), new ItemStack(Item.LEATHER, 2)}, new ItemStack(Item.BOOTS));
        private static Function FUEL = new Function(new ItemStack[]{new ItemStack(Item.OIL, 2)}, new ItemStack(Item.FUEL));
        private static Function PLASTIC = new Function(new ItemStack[]{new ItemStack(Item.OIL)}, new ItemStack(Item.PLASTIC));
        private static Function IRON = new Function(new ItemStack[]{new ItemStack(Item.IRON_ORE), new ItemStack(Item.COAL)}, new ItemStack(Item.IRON));
        private static Function POTTERY = new Function(new ItemStack[]{new ItemStack(Item.CLAY)}, new ItemStack(Item.POTTERY));
        private static Function GLASS = new Function(new ItemStack[]{new ItemStack(Item.SAND)}, new ItemStack(Item.GLASS, 2));
    }
}