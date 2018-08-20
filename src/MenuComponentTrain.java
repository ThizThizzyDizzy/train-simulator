import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.ImageStash;
import static simplelibrary.opengl.Renderer2D.drawCenteredText;
import static simplelibrary.opengl.Renderer2D.drawRect;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentTrain extends MenuComponent{
    private final Train train;
    public static final int wide = 420;
    public MenuComponentTrain(Train train){
        super(0, 0, wide, 125);
        this.train = train;
    }
    @Override
    public void render(){
        boolean atStation = train.stationLocation<2;
        //IMAGES
        GL11.glColor4d(1, 1, 1, 1);
        drawRect(x, y, x+width, y+height, 0);
        drawRect(x+1, y, x+width-1, y+50, ImageStash.instance.getTexture("/textures/track.png"));
        int station = (int) Math.round(train.mainlineLocation);
        double diff = train.mainlineLocation-station;
        double X = width/2;
        X+=diff*width;
        if(!atStation){
            if(train.headingLeft){
                drawRect(x+X+36, y, x+X-36, y+50, ImageStash.instance.getTexture("/textures/train.png"));
            }else{
                drawRect(x+X-36, y, x+X+36, y+50, ImageStash.instance.getTexture("/textures/train.png"));
            }
        }
        X = width/2;
        X+=train.stationLocation*width/2;
        if(atStation){
            drawRect(x+X-36, y, x+X+36, y+50, ImageStash.instance.getTexture("/textures/train.png"));
        }
        //TRACK OVERLAYS
        if(atStation){
            GL11.glColor4d(.5, .5, .5, .5);
            double entranceThreshold = TrainStation.stationEntranceLength/(TrainStation.stationEntranceLength+TrainStation.stationLength);
            double exitThreshold = TrainStation.stationExitLength/(TrainStation.stationExitLength+TrainStation.stationLength);
            drawRect(x+entranceThreshold*width/2, y, x+width-exitThreshold*width/2, y+50, 0);
        }
        //COLORS
        double val = train.getCargoTotal();
        double max = Train.maxCargo;
        if(val<max/2){
            GL11.glColor4d(1, val/(max/2d), 0, 1);
        }else{
            GL11.glColor4d(1-((val-max/2d)/max/2d), 1, 0, 1);
        }
        drawRect(x, y+100, x+width*(val/max), y+125, 0);
        val = train.velocity*20;
        max = Train.speed;
        if(val<max/2){
            GL11.glColor4d(1, val/(max/2d), 0, 1);
        }else{
            GL11.glColor4d(1-((val-max/2d)/max/2d), 1, 0, 1);
        }
        drawRect(x, y+75, x+width*(val/max), y+100, 0);
        //TEXT
        GL11.glColor4d(0, 0, 0, 1);
        drawText(x, y+50, x+100, y+75, ""+Math.round(train.mainlineLocation));
        if(train.currentJob!=null){
            drawCenteredText(x, y+50, x+width, y+75, train.currentJob.toString());
        }else{
            drawCenteredText(x, y+50, x+width, y+75, train.stationLocation==0?"Idle":"Returning to roundhouse");
        }
        double inPerSec = train.velocity*87;
        double ftPerSec = inPerSec/12;
        double miPerSec = ftPerSec/5180;
        double miPerMin = miPerSec*60;
        double miPerHour = miPerMin*60;
        drawCenteredText(x, y+75, x+width, y+100, Math.round(miPerHour*200)/10d+"mph");
        String cargo = "";
        for(ItemStack item : train.inventory){
            cargo+=", "+item.toString();
        }
        drawCenteredText(x, y+100, x+width, y+125, cargo.isEmpty()?"":cargo.substring(2));
    }
}