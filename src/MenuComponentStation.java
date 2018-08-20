import org.lwjgl.opengl.GL11;
import static simplelibrary.opengl.Renderer2D.drawCenteredText;
import static simplelibrary.opengl.Renderer2D.drawRect;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentStation extends MenuComponent{
    private final TrainStation station;
    private double textHeight = .2;//multiple of station panel height
    public MenuComponentStation(TrainStation station){
        super(0, 0, 200, 100);
        this.station = station;
    }
    @Override
    public void render(){
        //COLORS
        double val = station.getCargoTotal();
        double max = station.maxCargo;
        if(val<max/2){
            GL11.glColor4d(val/(max/2d), 1, 0, 1);
        }else{
            GL11.glColor4d(1, 1-((val-max/2d)/(max/2d)), 0, 1);
        }
        drawRect(x, y+height*textHeight, x+width*(val/max), y+100, 0);
        //TEXT
        GL11.glColor4d(0, 0, 0, 1);
        drawCenteredText(x, y, x+width, y+height*textHeight, station.toString());
        if(station.inventory.isEmpty()){
            return;
        }
        double txtHeight = Math.min(30, (height*(1-textHeight))/station.inventory.size());
        for (int i = 0; i < station.inventory.size(); i++) {
            ItemStack item = station.inventory.get(i);
            drawCenteredText(x, height*textHeight+y+i*txtHeight, x+width, height*textHeight+y+(i+1)*txtHeight, Math.round(item.d*10)/10d+" "+item.item);
        }
    }
}