public class Function{
    final ItemStack[] inputs;
    final ItemStack[] outputs;
    private int time = 20*60;
    private int timer;
    public Function(ItemStack[] inputs, ItemStack... outputs){
        if(inputs==null){
            for(ItemStack stack : outputs){
                stack.d/=10;
            }
        }
        this.inputs = inputs;
        this.outputs = outputs;
    }
    public void tick(TrainStation station){
        double spaceRequired = 0;
        if(inputs!=null){
            for(ItemStack stack : inputs){
                spaceRequired-=stack.d;
            }
        }
        for(ItemStack stack : outputs){
            spaceRequired+=stack.d;
        }
        if(station.getCargoTotal()>=station.maxCargo-spaceRequired){
            return;
        }
        if(inputs==null){
            timer++;
            if(timer>=time/10){
                timer-=time/10;
                ItemStack[] newOutputs = new ItemStack[outputs.length];
                for (int i = 0; i < outputs.length; i++) {
                    newOutputs[i] = new ItemStack(outputs[i], outputs[i].d/10);
                }
                station.addItems(newOutputs);
            }
        }else if(station.hasItems(inputs)){
            timer++;
            if(timer>=time){
                timer-=time;
                station.removeItems(inputs);
                station.addItems(outputs);
            }
        }else{
            timer = 0;
        }
    }
}