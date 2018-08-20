import java.util.ArrayList;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponentList;
import simplelibrary.opengl.gui.components.MenuComponentMulticolumnList;
public class MenuGame extends Menu{
    private MenuComponentMulticolumnList trains;
    private MenuComponentMulticolumnList stations;
    private boolean autotrain = false;
    public MenuGame(GUI gui, Menu parent){
        super(gui, parent);
        stations = add(new MenuComponentMulticolumnList(0, 0, Display.getWidth(), 200, 200, 100, 20));
        trains = add(new MenuComponentMulticolumnList(0, 200, Display.getWidth(), Display.getHeight()-220, MenuComponentTrain.wide, 125, 20));
        for(TrainStation station : Core.game.stations){
            stations.add(new MenuComponentStation(station));
        }
    }
    @Override
    public void tick(){
        Core.game.tick();
        if(Core.game.findJob()!=null&&autotrain){
            keyboardEvent('t', Keyboard.KEY_T, true, false);
        }
    }
    @Override
    public void keyboardEvent(char character, int key, boolean pressed, boolean repeat) {
        if(pressed){
            if(key==Keyboard.KEY_T){
                Train train = new Train();
                Core.game.trains.add(train);
                trains.add(new MenuComponentTrain(train));
            }
            if(key==Keyboard.KEY_RETURN){
                autotrain = !autotrain;
            }
            if(key==Keyboard.KEY_RBRACKET){
                Core.game.timeSpeed++;
                if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)){
                    Core.game.timeSpeed+=9;
                }
                if(Core.game.timeSpeed>50&&!Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)){
                    Core.game.timeSpeed = 50;
                }
            }
            if(key==Keyboard.KEY_LBRACKET){
                Core.game.timeSpeed--;
                if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)){
                    Core.game.timeSpeed-=9;
                }
                if(Core.game.timeSpeed<1){
                    Core.game.timeSpeed = 1;
                }
            }
        }
    }
    @Override
    public void renderBackground(){
        GL11.glColor4d(.85, .85, .85, 1);
        drawRect(0, 0, Display.getWidth(), Display.getHeight(), 0);
        GL11.glColor4d(1, 1, 1, 1);
    }
    @Override
    public void render(int millisSinceLastTick){
        super.render(millisSinceLastTick);
        trains.width = stations.width = Display.getWidth();
        trains.height = Display.getHeight()-stations.height-20;
        if(autotrain){
            GL11.glColor4d(0, 0, 0, 0.1);
            drawCenteredText(0, Display.getHeight()/2-100, Display.getWidth(), Display.getHeight()/2+100, "Auto-Train");
        }
        if(Core.game.timeSpeed>1){
            String fast = "";
            for(int i = 1; i<Core.game.timeSpeed; i++){
                fast+=">";
            }
            GL11.glColor4d(0, 0, 0, 0.1);
            if(Core.game.timeSpeed>50){
                ArrayList<Integer> times = new ArrayList<>();
                int time = Core.game.timeSpeed;
                while(time>=Display.getWidth()){
                    times.add(Display.getWidth());
                    time-=Display.getWidth();
                }
                times.add(time);
                for (int j = 0; j < times.size(); j++) {
                    int i = times.get(j);
                    int y = Display.getHeight()/2-times.size()/2+j;
                    drawRect(Display.getWidth()/2-i/2, y, Display.getWidth()/2+i/2, y+1, 0);
                }
            }else if(Core.game.timeSpeed>40){
                drawCenteredText(0, Display.getHeight()/2-20, Display.getWidth(), Display.getHeight()/2+20, fast);
            }else if(Core.game.timeSpeed>20){
                drawCenteredText(0, Display.getHeight()/2-25, Display.getWidth(), Display.getHeight()/2+25, fast);
            }else if(Core.game.timeSpeed>10){
                drawCenteredText(0, Display.getHeight()/2-50, Display.getWidth(), Display.getHeight()/2+50, fast);
            }else{
                drawCenteredText(0, Display.getHeight()/2-100, Display.getWidth(), Display.getHeight()/2+100, fast);
            }
        }
        GL11.glColor4d(0, 0, 0, 1);
        drawText(0, Display.getHeight()-20, Display.getWidth()/4, Display.getHeight(), Core.game.findJob()==null?"No jobs!":"Job Waiting: "+Core.game.findJob());
        GL11.glColor4d(1, 1, 1, 1);
        int idleTrains = 0;
        for(Train train : Core.game.trains){
            if(train.currentJob==null){
                idleTrains++;
            }
        }
        double val = Core.game.trains.size()-idleTrains;
        double max = Core.game.trains.size();
        if(val<max/2){
            GL11.glColor4d(1, val/(max/2d), 0, 1);
        }else{
            GL11.glColor4d(1-((val-max/2d)/max/2d), 1, 0, 1);
        }
        drawRect(Display.getWidth()/4, Display.getHeight()-20, Display.getWidth()/4+(Display.getWidth()*3/4d)*(val/max), Display.getHeight(), 0);
        GL11.glColor4d(0, 0, 0, 1);
        drawText(Display.getWidth()/4, Display.getHeight()-20, Display.getWidth(), Display.getHeight(), Core.game.trains.size()==0?"No Trains.":(Core.game.trains.size()+" Total trains, "+idleTrains+" Idle Train"+(idleTrains==1?"":"s")+" ("+(Core.game.trains.size()-idleTrains)*100/Core.game.trains.size()+"% Busy)"));
        GL11.glColor4d(1, 1, 1, 1);
    }
}