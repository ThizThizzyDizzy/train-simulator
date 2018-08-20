import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import javazoom.jl.converter.Converter;
import javazoom.jl.decoder.JavaLayerException;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.Util;
import simplelibrary.Sys;
import simplelibrary.error.ErrorCategory;
import simplelibrary.error.ErrorLevel;
import simplelibrary.openal.SoundStash;
public class Sounds{
    public static boolean autoplay = false;
    private static boolean paused = false;
    private static void addMusic(){
        //TODO use addSound(...) and addSong(...) to add sound effects and music.
    }
    /**
     * Gets a list of all currently playable music.
     * @return a list of sound names that can be played as music at the current time (One will be randomly chosen)
     */
    private static void getPlayableMusic(ArrayList<String> playableMusic){
        throw new UnsupportedOperationException("Not yet implemented.");
        //TODO fill the list with the names of all songs that can be played at the moment.
    }
    private static boolean running = false;
    public static HashMap<String, String> soundNames = new HashMap<>();
    public static HashMap<String, String> songURLs = new HashMap<>();
    public static boolean mute;
    public static String nowPlaying;
    public static float vol = 2.5f;
    private static float volume = 1f;
    private static String fading = null;
    private static String fadingSource = null;
    private static long songTimer;
    private static int whichSound = 0;
    /**
     * Disables the sound system.
     * Equivalent to AL.destroy();
     */
    public static void destroy(){
        AL.destroy();
        running = false;
    }
    /**
     * Starts the sound system, music thread, and music downloading thread.
     * Equivalent to AL.destroy();
     */
    public static void create() throws LWJGLException{
        soundNames.clear();
        songURLs.clear();
        addMusic();
        running = true;
        AL.create();
        new Thread(() -> {
                while(running){
                    if(!isPlaying("music")){
                        nowPlaying = null;
                    }
                    try{
                        Thread.sleep(100);
                    }catch(InterruptedException ex){
                        Sys.error(ErrorLevel.severe, "Failed to wait: Too impatient!", ex, ErrorCategory.threading);
                    }
                    if(mute){
                        if(isPlaying("music")){
                            stopSound("music");
                        }
                        continue;
                    }
                    if(autoplay&&!isPlaying("music")){
                        ArrayList<String> strs = new ArrayList<>();
                        getPlayableMusic(strs);
                        if(!strs.isEmpty()){
                            playSoundOneChannel("music", strs.get(new Random().nextInt(strs.size())));
                        }
                    }
                }
        }).start();
        new Thread(() -> { //Music Downloader
                System.out.println("Starting Music Download...");
                for(String key : songURLs.keySet()){
                    if(!running){
                        return;
                    }
                    String filepath = soundNames.get(key);
                    String url = songURLs.get(key);
                    File from = new File(filepath.replace(".wav", ".mp3"));
                    File to = new File(filepath.replace(".mp3", ".wav"));
                    if(!from.exists()){
                        System.out.println("Downloading Song "+key+"...");
                        downloadFile(url, from);
                        System.out.println("Song Downloaded: "+key+"...");
                    }
                    if(to.exists()) continue;
                    Converter c = new Converter();
                    try{
                        System.out.println("Converting Song "+from.getName()+"...");
                        c.convert(from.getAbsolutePath(), to.getAbsolutePath());
                    }catch(JavaLayerException ex){
                        Sys.error(ErrorLevel.severe, "Failed to convert file: "+from.getName()+". Deleting...", ex, ErrorCategory.audio);
                        if(from.exists()) from.delete();
                        if(to.exists()) to.delete();
                    }
                        System.out.println("Song Converted: "+from.getName()+"...");
                }
                System.out.println("All songs are up to date.");
        }).start();
    }
    static void tick(boolean lastTick){
        if(lastTick){
            destroy();
            return;
        }
        vol = Math.max(0,Math.min(5,vol));
        if(nowPlaying!=null&&!canPlayMusic(nowPlaying)){
            fadeSound("music");
        }
        AL10.alSourcef(SoundStash.getSource("music"), AL10.AL_GAIN, volume*vol);
        if(fadingSource!=null){
            AL10.alSourcef(SoundStash.getSource(fadingSource), AL10.AL_GAIN, volume*vol);
            if(volume>0){
                volume-=0.025;
            }else{
                stopSound(fadingSource);
                volume = 1f;
                if(fading!=null){
                    playSoundOneChannel(fadingSource, fading);
                    fading = null;
                }
                AL10.alSourcef(SoundStash.getSource(fadingSource), AL10.AL_GAIN, vol);
                fadingSource = null;
            }
        }
    }
    public static int songTimer(){
        return (int) ((System.currentTimeMillis()-songTimer)/50);
    }
    public static synchronized void playSound(String source, String sound){
        whichSound++;
        if(whichSound>20){
            whichSound = 1;
        }
        if(isPlaying(source, whichSound)){
            stopSound(source, whichSound);
        }
        AL10.alSourcef(SoundStash.getSource(source), AL10.AL_GAIN, vol);
        try{
            AL10.alSourceUnqueueBuffers(SoundStash.getSource(source+whichSound));
            Util.checkALError();
        }catch(Exception ex){
            System.err.println(ex.getMessage());
        }
        AL10.alSourceQueueBuffers(SoundStash.getSource(source+whichSound), SoundStash.getBuffer(soundNames.get(sound)));
        AL10.alSourcePlay(SoundStash.getSource(source+whichSound));
    }
    public static synchronized void playSoundOneChannel(String source, String sound){
        if(!new File(soundNames.get(sound)).exists())return;
        if(source.equals("music")){
            if(!canPlayMusic(sound)) return;
        }
        if(isPlaying(source)){
            fadeSound(source, sound);
            return;
        }
        AL10.alSourcef(SoundStash.getSource(source), AL10.AL_GAIN, vol);
        try{
            AL10.alSourceUnqueueBuffers(SoundStash.getSource(source));
            Util.checkALError();
        }catch(Exception ex){
            System.err.println(ex.getMessage());
        }
        AL10.alSourceQueueBuffers(SoundStash.getSource(source), SoundStash.getBuffer(soundNames.get(sound)));
        AL10.alSourcePlay(SoundStash.getSource(source));
        if(source.equals("music")){
            nowPlaying = sound;
            songTimer = System.currentTimeMillis();
        }
    }
    public static boolean canPlayMusic(String music){
        ArrayList<String> playable = new ArrayList<>();
        getPlayableMusic(playable);
        return playable.contains(music);
    }
    public static synchronized void stopSounds(String source){
        for(int i = 1; i<=20; i++){
            if(isPlaying(source, i)){
                AL10.alSourceStop(SoundStash.getSource(source+i));
            }
        }
    }
    public static synchronized void stopSound(String source, int channel){
        if(isPlaying(source, channel)){
            AL10.alSourceStop(SoundStash.getSource(source+channel));
        }
    }
    public static synchronized void fadeSound(String source, String sound){
        if(source.equals("music")&&sound.equals(nowPlaying)) return;
        if(!isPlaying(source)){
            playSoundOneChannel(source, sound);
            return;
        }
        fadingSource = source;
        fading = sound;
    }
    public static synchronized void fadeSound(String source){
        if(!isPlaying(source)){
            return;
        }
        fadingSource = source;
    }
    public static synchronized void stopSound(String source){
        if(source.equals("music")){
            nowPlaying = null;
        }
        if(isPlaying(source)){
            AL10.alSourceStop(SoundStash.getSource(source));
        }
    }
    public static synchronized boolean isPlaying(String source){
        if(!running)return false;
        return AL10.alGetSourcei(SoundStash.getSource(source), AL10.AL_SOURCE_STATE)==AL10.AL_PLAYING;
    }
    public static synchronized boolean isPlaying(String source, int channel){
        if(!running)return false;
        return AL10.alGetSourcei(SoundStash.getSource(source+channel), AL10.AL_SOURCE_STATE)==AL10.AL_PLAYING;
    }
    private static File downloadFile(String link, File destinationFile){
        if(destinationFile.exists()||link==null){
            return destinationFile;
        }
        destinationFile.getParentFile().mkdirs();
        try {
            URL url = new URL(link);
            int fileSize;
            URLConnection connection = url.openConnection();
            connection.setDefaultUseCaches(false);
            if ((connection instanceof HttpURLConnection)) {
                ((HttpURLConnection)connection).setRequestMethod("HEAD");
                int code = ((HttpURLConnection)connection).getResponseCode();
                if (code / 100 == 3) {
                    return null;
                }
            }
            fileSize = connection.getContentLength();
            byte[] buffer = new byte[65535];
            int unsuccessfulAttempts = 0;
            int maxUnsuccessfulAttempts = 3;
            boolean downloadFile = true;
            while (downloadFile) {
                downloadFile = false;
                URLConnection urlconnection = url.openConnection();
                if ((urlconnection instanceof HttpURLConnection)) {
                    urlconnection.setRequestProperty("Cache-Control", "no-cache");
                    urlconnection.connect();
                }
                String targetFile = destinationFile.getName();
                FileOutputStream fos;
                int downloadedFileSize;
                try (InputStream inputstream=Main.getRemoteInputStream(targetFile, urlconnection)) {
                    fos=new FileOutputStream(destinationFile);
                    downloadedFileSize=0;
                    int read;
                    while ((read = inputstream.read(buffer)) != -1) {
                        fos.write(buffer, 0, read);
                        downloadedFileSize += read;
                    }
                }
                fos.close();
                if (((urlconnection instanceof HttpURLConnection)) && 
                    ((downloadedFileSize != fileSize) && (fileSize > 0))){
                    unsuccessfulAttempts++;
                    if (unsuccessfulAttempts < maxUnsuccessfulAttempts){
                        downloadFile = true;
                    }else{
                        throw new Exception("failed to download "+targetFile);
                    }
                }
            }
            return destinationFile;
        }catch (Exception ex){
            return null;
        }
    }
    /**
     * Adds a song so it can be played. Songs are downloaded on sound system startup as MP3s and decompressed into .wav files.
     * @param name the name of the song, used when playing it.
     * @param songName The file name, without the extention
     * @param url the URL the song can be downloaded at, as an MP3
     */
    private static void addSong(String name, String songName, String url){
        soundNames.put(name, Main.getAppdataRoot()+"\\Music\\"+songName+".wav");
        songURLs.put(name, url);
    }
    /**
     * Adds a sound effect so it can be played. Sound effects are not downloaded. They are found in the jarfile under /sounds
     * All sounds should be .wav files
     * @param name the name of the song, used when playing it.
     * @param songName The file name, without the extention.
     */
    private static void addSound(String name, String songName){
        soundNames.put(name, "/sounds/"+songName+".wav");
    }
    public static void pauseMusic(){
        if(!paused)
            AL10.alSourcePause(SoundStash.getSource("music"));
        paused = true;
    }
    public static void unpauseMusic(){
        if(paused)
            AL10.alSourcePlay(SoundStash.getSource("music"));
        paused = false;
    }
    static boolean isPlayingMusic(){
        return isPlaying("music")||paused;
    }
    static void toggleMusic(){
        if(paused){
            unpauseMusic();
        }else{
            pauseMusic();
        }
    }
}