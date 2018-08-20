public class ItemStack{
    public final Item item;
    public double d;
    public ItemStack(ItemStack item, double d){
        this(item.item, d);
    }
    public ItemStack(Item item, double d){
        this.item = item;
        this.d = d;
    }
    ItemStack(ItemStack stack){
        this(stack.item, stack.d);
    }
    ItemStack(Item item){
        this(item, 1);
    }
    @Override
    public String toString(){
        return Math.round(d*10)/10d+" "+item;
    }
}