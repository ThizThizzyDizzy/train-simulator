import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
public class Main{
    private static String requiredSimpleLibraryVersion = "";
    private static String versionListURL = "";
    public static final String applicationName = "Unnamed";
    private static ArrayList<String[]> requiredLibraries = new ArrayList<>();
    public static final boolean jLayer = false;
    public static final boolean webcam = false;
    public static final boolean simplelibraryExtended = true;
    public static final boolean textToSpeech = false;
    public static final boolean mediaButtons = false;
    //Download details
    private static int total;
    private static int current;
    private static JFrame frame;
    private static JProgressBar bar;
    static{
        if(jLayer){
            addRequiredLibrary("https://www.dropbox.com/s/phv29x1suv4i4b0/jl1.0.1.jar?dl=1", "jl1.0.1.jar");
        }
        if(webcam){
            addRequiredLibrary("https://www.dropbox.com/s/t09a8brz3i5nl9c/bridj-0.6.2.jar?dl=1", "bridj-0.6.2.jar");
            addRequiredLibrary("https://www.dropbox.com/s/bvyy19zh5vvibrq/slf4j-api-1.7.2.jar?dl=1", "slf4j-api-1.7.2.jar");
            addRequiredLibrary("https://www.dropbox.com/s/1idoft8jafnr1x1/webcam-capture-0.3.10.jar?dl=1", "webcam-capture-0.3.10.jar");
        }
        if(simplelibraryExtended){
            addRequiredLibrary("https://www.dropbox.com/s/9kae7xcvddmrcz3/simplelibrary-extended.jar?dl=1", "simplelibrary-extended.jar");
        }
        if(mediaButtons){
            addRequiredLibrary("https://www.dropbox.com/s/kjwuh8poofqk36k/jintellitype-1.3.9.jar?dl=1", "jintellitype-1.3.9.jar");
        }
        if(textToSpeech){
            addRequiredLibrary("https://www.dropbox.com/s/y3skf4runw20h6w/cmu_time_awb.jar?dl=1", "cmu_time_awb.jar");
            addRequiredLibrary("https://www.dropbox.com/s/il7apy01wk4f8g9/cmu_us_kal.jar?dl=1", "cmu_us_kal.jar");
            addRequiredLibrary("https://www.dropbox.com/s/9s1rdcvknjwctc5/cmudict04.jar?dl=1", "cmudict04.jar");
            addRequiredLibrary("https://www.dropbox.com/s/pn9q7dnkfhpx0xk/cmulex.jar?dl=1", "cmulex.jar");
            addRequiredLibrary("https://www.dropbox.com/s/ibg7wq65yzounuu/cmutimelex.jar?dl=1", "cmutimelex.jar");
            addRequiredLibrary("https://www.dropbox.com/s/9b7e8sa4sqf1tju/en_us.jar?dl=1", "en_us.jar");
            addRequiredLibrary("https://www.dropbox.com/s/n9zh5p8gvb1aao9/freetts.jar?dl=1", "freetts.jar");
            addRequiredLibrary("https://www.dropbox.com/s/srssz73x0hiqf6h/freetts-jsapi10.jar?dl=1", "freetts-jsapi10.jar");
            addRequiredLibrary("https://www.dropbox.com/s/f369vf69mkjmwxd/jsapi.jar?dl=1", "jsapi.jar");
            addRequiredLibrary("https://www.dropbox.com/s/tfsvbdkvvy41v8h/mbrola.jar?dl=1", "mbrola.jar");
        }
    }
    private static void addRequiredLibrary(String url, String filename){
        requiredLibraries.add(new String[]{url,filename});
    }
    public static void main(String[] args) throws NoSuchMethodException, IOException, InterruptedException, URISyntaxException{
        args = update(args);
        if(args==null){
            return;
        }
        Core.main(args);
    }
    public static String getAppdataRoot(){
        return System.getenv("APPDATA")+"\\Thizzygaming Industries\\"+applicationName;
    }
    private static String[] update(String[] args) throws URISyntaxException, IOException, InterruptedException{
        ArrayList<String> theargs = new ArrayList<>(Arrays.asList(args));
        if(args.length<1||!args[0].equals("Skip Dependencies")){
            if(versionListURL.isEmpty()){
                System.err.println("Version list URL is empty! assuming latest version.");
            }else{
                Updater updater = Updater.read(versionListURL, VersionManager.currentVersion, applicationName);
                if(updater!=null&&updater.getVersionsBehindLatestDownloadable()>0&&JOptionPane.showConfirmDialog(null, "Version "+updater.getLatestDownloadableVersion()+" is out!  Would you like to update "+applicationName+" now?", applicationName+" "+VersionManager.currentVersion+"- Update Available", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                    startJava(new String[0], new String[]{"justUpdated"}, updater.update(updater.getLatestDownloadableVersion()));
                    System.exit(0);
                }
            }
            total = 8+requiredLibraries.size();
            frame = new JFrame("Download Progress");
            bar = new JProgressBar(0, total);
            frame.add(bar);
            frame.setSize(300, 70);
            bar.setBounds(0, 0, 300, 70);
            frame.setVisible(true);
            int OS_WINDOWS = 0;
            int OS_SOLARIS = 1;
            int OS_MACOSX = 2;
            int OS_LINUX = 3;
            int BIT_32 = 0;
            int BIT_64 = 1;
            String[][] nativesPaths = {
                {"https://dl.dropboxusercontent.com/s/1nt1g7ui7p4eb54/windows32natives.zip?dl=1&token_hash=AAFsOnqBqipIOxc4sNr138FnlZIjHBf-KPwMTNe8F5lqOQ",
                 "https://dl.dropboxusercontent.com/s/y41peavuls3ptzu/windows64natives.zip?dl=1&token_hash=AAEJ6Ih8HGEsla1tJmIB7R-YBCTC8LVq_D4OFcFWDCEZ5Q"},
                {"https://dl.dropboxusercontent.com/s/h4z3j2pspuos15l/solaris32natives.zip?dl=1&token_hash=AAEmlE84CzHRTqya3xPN9xRh_1_v0nGccJFp-bfru4jSRw",
                 "https://dl.dropboxusercontent.com/s/vq3x3n81x0qvc3u/solaris64natives.zip?dl=1&token_hash=AAEyl6swuFIukpTNZjrgv96TGwSnMYxWt0hdQ71_KiqQqw"},
                {"https://dl.dropboxusercontent.com/s/ljvgoccqz33bcq1/macosx32natives.zip?dl=1&token_hash=AAGezz3pNxqa6Fi_O-xGCZdI2923D7b-ZsrWZ61HlFROYw",
                 null},
                {"https://dl.dropboxusercontent.com/s/nfv4ra6n68lna9n/linux32natives.zip?dl=1&token_hash=AAGzHZLGp9S4HAjzpzNZp9-YixYw4H56D6_DJ3dG5GDeFA",
                 "https://dl.dropboxusercontent.com/s/rp6uhdmec7697ty/linux64natives.zip?dl=1&token_hash=AAHl6tcg11VwWr31WtqMUlozabCSpr0LfS5MLS2MpmWnEA"}
            };
            String OS = System.getenv("OS");
            int whichOS = -1;
            switch(OS){
                case "Windows_NT":
                    whichOS = OS_WINDOWS;
                    break;
//                    whichOS = OS_SOLARIS;
//                    break;
//                    whichOS = OS_MACOSX;
//                    break;
//                    whichOS = OS_LINUX;
//                    break;
                default:
                    whichOS = JOptionPane.showOptionDialog(null, "Unrecognized OS \""+OS+"\"!\nPlease report this problem on the "+applicationName+" issue tracker.\nIn the meantime, which natives should I load?", "Unrecognized Operating System", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[]{"Windows", "Solaris", "Mac OSX", "Linux"}, "Windows");
                    if(whichOS<0||whichOS>3){
                        System.exit(0);
                    }
            }
            String version = System.getenv("PROCESSOR_ARCHITECTURE");
            int whichBitDepth = -1;
            switch(version){
                case "x86":
                    whichBitDepth = BIT_32;
                    break;
                case "AMD64":
                    whichBitDepth = BIT_64;
                    break;
                default:
                    whichBitDepth = JOptionPane.showOptionDialog(null, "Unrecognized processor architecture \""+version+"\"!\nPlease report this problem on the "+applicationName+" issue tracker.\nIn the meantime, should I load the 64 bit binaries with the 32 bit ones?", "Unrecognized Processor Architecture", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[]{"No, treat it as a 32 bit system", "Yes, treat it as a 64 bit system"}, "Yes, treat it as a 64 bit system");
                    if(whichBitDepth<0||whichBitDepth>1){
                        System.exit(0);
                    }
            }
            if(whichBitDepth==BIT_32){
                current++;
            }
            String[] osPaths = nativesPaths[whichOS];
            File bit32 = downloadFile(osPaths[BIT_32], new File(getAppdataRoot()+"\\natives32.zip"), applicationName);
            File bit64 = whichBitDepth==BIT_64?downloadFile(osPaths[BIT_64], new File(getAppdataRoot()+"\\natives64.zip"), applicationName):null;
            File nativesDir = new File(getAppdataRoot()+"\\natives");
            if(bit32==null||(whichBitDepth==BIT_64&&bit64==null&&osPaths[BIT_64]!=null)){
                JOptionPane.showMessageDialog(null, "Could not download the required natives!  "+applicationName+" will now exit.", "Native Download Failed", JOptionPane.OK_OPTION);
                System.exit(0);
            }
            extractFile(bit32, nativesDir);
            if(bit64!=null){
                extractFile(bit64, nativesDir);
            }
            File simplibVersions = downloadFile("https://www.dropbox.com/s/as5y1ik7gb8gp6k/versions.dat?dl=1", new File("C:\\temp\\simplib.versions"), applicationName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(simplibVersions)));
            ArrayList<String> versions = new ArrayList<>();
            HashMap<String, String> simplib = new HashMap<>();
            String line;
            while((line = reader.readLine())!=null){
                if(line.isEmpty())continue;
                versions.add(line.split("=", 2)[0]);
                simplib.put(line.split("=", 2)[0], line.split("=", 2)[1]);
            }
            if(!versions.contains(requiredSimpleLibraryVersion)){
                System.err.println("Unknown simplelibrary version "+requiredSimpleLibraryVersion+"! Downloading latest version");
                requiredSimpleLibraryVersion = versions.get(versions.size()-1);
            }
            addRequiredLibrary(simplib.get(requiredSimpleLibraryVersion), "Simplelibrary "+requiredSimpleLibraryVersion+".jar");
            reader.close();
            File[] requiredLibs = new File[requiredLibraries.size()];
            int n = 0;
            for(String[] lib : requiredLibraries){
                String url = lib[0];
                String filename = lib[1];
                requiredLibs[n] = downloadFile(url, new File(getAppdataRoot()+"\\"+filename), applicationName);
                n++;
            }
            File[] lwjglJars = {
                downloadFile("https://dl.dropboxusercontent.com/s/p7v72lix4gl96co/lwjgl.jar?dl=1&token_hash=AAG5TMAYw0Oq1_xwgVjKoE8FkKXMaWOfpj5cau1UuWKZlA", new File(getAppdataRoot()+"\\lwjgl.jar"), applicationName),
                downloadFile("https://dl.dropboxusercontent.com/s/9ylaq5w5vzj1lgi/jinput.jar?dl=1&token_hash=AAHILxU3uc-UU5vXj7N4i5s1huBKYSzKGgKq3MawNJB05w", new File(getAppdataRoot()+"\\jinput.jar"), applicationName),
                downloadFile("https://dl.dropboxusercontent.com/s/fog6w5pcxqf4zd9/lwjgl_util.jar?dl=1&token_hash=AAHwYq0uL4zeuTrLoi8EiG_RiUeMDZDsnlm4KYNScpy0Sw", new File(getAppdataRoot()+"\\lwjgl_util.jar"), applicationName),
                downloadFile("https://dl.dropboxusercontent.com/s/60en1x8in11leqn/lzma.jar?dl=1&token_hash=AAGUFJwmD9jKmk7j4M53Xr0_6Sisf5RSRW3JAjRgsml4gg", new File(getAppdataRoot()+"\\lzma.jar"), applicationName)
            };
            frame.dispose();
            String[] additionalClasspathElements = new String[requiredLibs.length+4];
            for(int i = 0; i<requiredLibs.length+4; i++){
                if(i<4){
                    additionalClasspathElements[i] = lwjglJars[i].getAbsolutePath();
                }else{
                    int j = i-4;
                    additionalClasspathElements[i] = requiredLibs[j].getAbsolutePath();
                }
            }
            System.out.println("Loading...");
            theargs.add(0, "Skip Dependencies");
            final Process p = restart(new String[]{"-Djava.library.path="+nativesDir.getAbsolutePath()}, theargs.toArray(new String[theargs.size()]), additionalClasspathElements, Main.class);
            final int[] finished = {0};
            new Thread(){
                public void run(){
                    BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line;
                    try{
                        while((line=in.readLine())!=null){
                            System.out.println(line);
                        }
                    }catch(IOException ex){
                        throw new RuntimeException(ex);
                    }
                    finished[0]++;
                    if(finished[0]>1){
                        System.exit(0);
                    }
                }
            }.start();
            new Thread(){
                public void run(){
                    BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                    String line;
                    try{
                        while((line=in.readLine())!=null){
                            System.err.println(line);
                        }
                    }catch(IOException ex){
                        throw new RuntimeException(ex);
                    }
                    finished[0]++;
                    if(finished[0]>1){
                        System.exit(0);
                    }
                }
            }.start();
            new Thread(){
                public void run(){
                    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                    PrintWriter out = new PrintWriter(p.getOutputStream());
                    String line;
                    try{
                        while((line=in.readLine())!=null){
                            out.println(line);
                            out.flush();
                        }
                    }catch(IOException ex){
                        throw new RuntimeException(ex);
                    }
                }
            }.start();
            return null;
        }
        theargs.remove(0);
        return theargs.toArray(new String[theargs.size()]);
    }
    private static File downloadFile(String link, File destinationFile, String applicationName){
        current++;
        bar.setValue(current);
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
                try (InputStream inputstream=getRemoteInputStream(targetFile, urlconnection)) {
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
    public static InputStream getRemoteInputStream(String currentFile, final URLConnection urlconnection) throws Exception {
        final InputStream[] is = new InputStream[1];
        for (int j = 0; (j < 3) && (is[0] == null); j++) {
            Thread t = new Thread() {
                public void run() {
                    try {
                        is[0] = urlconnection.getInputStream();
                    }catch (IOException localIOException){}
                }
            };
            t.setName("FileDownloadStreamThread");
            t.start();
            int iterationCount = 0;
            while ((is[0] == null) && (iterationCount++ < 5)){
                try {
                    t.join(1000L);
                } catch (InterruptedException localInterruptedException) {
                }
            }
            if (is[0] != null){
                continue;
            }
            try {
                t.interrupt();
                t.join();
            } catch (InterruptedException localInterruptedException1) {
            }
        }
        if (is[0] == null) {
            throw new Exception("Unable to download "+currentFile);
        }
        return is[0];
    }
    private static void extractFile(File fromZip, File toDir){
        if(!fromZip.exists()){
            return;
        }
        toDir.mkdirs();
        try(ZipInputStream in = new ZipInputStream(new FileInputStream(fromZip))){
            ZipEntry entry;
            while((entry = in.getNextEntry())!=null){
                File destFile = new File(toDir.getAbsolutePath()+"\\"+entry.getName().replaceAll("/", "\\"));
                delete(destFile);
                try(FileOutputStream out = new FileOutputStream(destFile)){
                    byte[] buffer = new byte[1024];
                    int read = 0;
                    while((read=in.read(buffer))>=0){
                        out.write(buffer, 0, read);
                    }
                }
            }
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }
    private static void delete(File file){
        if(!file.exists()){
            return;
        }
        if(file.isDirectory()){
            File[] files = file.listFiles();
            if(files!=null){
                for(File afile : files){
                    delete(afile);
                }
            }
            file.delete();
        }else{
            file.delete();
        }
    }
    /**
     * Restarts the program.  This method will return normally if the program was properly restarted or throw an exception if it could not be restarted.
     * @param vmArgs The VM arguments for the new instance
     * @param applicationArgs The application arguments for the new instance
     * @param additionalFiles Any additional files to include in the classpath
     * @param mainClass The program's main class.  The new instance is started with this as the specified main class.
     * @throws URISyntaxException if a URI cannot be created to obtain the filepath to the jarfile
     * @throws IOException if Java is not in the PATH environment variable
     */
    public static Process restart(String[] vmArgs, String[] applicationArgs, String[] additionalFiles, Class<?> mainClass) throws URISyntaxException, IOException{
        ArrayList<String> params = new ArrayList<>();
        params.add("java");
        params.addAll(Arrays.asList(vmArgs));
        params.add("-classpath");
        String filepath = mainClass.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        for(String str : additionalFiles){
            filepath+=";"+str;
        }
        params.add(filepath);
        params.add(mainClass.getName());
        params.addAll(Arrays.asList(applicationArgs));
        ProcessBuilder builder = new ProcessBuilder(params);
        return builder.start();
    }
    /**
     * Starts the requested Java application the program.  This method will return normally if the program was properly restarted or throw an exception if it could not be restarted.
     * @param vmArgs The VM arguments for the new instance
     * @param applicationArgs The application arguments for the new instance
     * @param file The program file.  The new instance is started with this as the specified main class.
     * @throws URISyntaxException if a URI cannot be created to obtain the filepath to the jarfile
     * @throws IOException if Java is not in the PATH environment variable
     */
    public static Process startJava(String[] vmArgs, String[] applicationArgs, File file) throws URISyntaxException, IOException{
        ArrayList<String> params = new ArrayList<>();
        params.add("java");
        params.addAll(Arrays.asList(vmArgs));
        params.add("-jar");
        params.add(file.getAbsolutePath());
        params.addAll(Arrays.asList(applicationArgs));
        ProcessBuilder builder = new ProcessBuilder(params);
        return builder.start();
    }
}