package com.zdj.free;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;


public class ConnectionTestUtil {
    public static final String COPYRIGHT = 
        "Licensed Materials - Property of IBM "+
        "5724-V09" +
        "Copyright IBM Corp. 2009 "+
        "All Rights Reserved "+
        "US Government Users Restricted Rights "+
        "- Use, duplication ordisclosure restricted by GSA ADP Schedule Contract with IBM Corp.";
    
    public static final String ID = ""
        + "@(#)53 1.2 src/krz/java/com/ibm/tivoli/itm/agent/krz/config/ConnectionTestUtil.java, krz, omrac_631 8/26/10 03:19:37";
    
    /**
     * Read from input stream and save to StringBuffer
     */
    public static class InputStreamPollThread extends Thread{
        private StringBuffer outputBuffer;
        private InputStream is; 
        private String outputCharset;
        public InputStreamPollThread(String threadName,InputStream is,StringBuffer outputBuffer,String outputCharset){
            super(threadName);
            this.is = is;
            this.outputBuffer = outputBuffer;
            this.outputCharset = outputCharset;
            setDaemon(true);
        }
        
        public void run(){
            try{
                byte[] data=new byte[4096];
                int datalen=0;
                while( (datalen=is.read(data))>0 ){
                    String str = null;
                    if ( outputCharset == null ){
                        str = new String(data,0,datalen);
                    }else{
                        str = new String(data,0,datalen,outputCharset);
                    }
                    outputBuffer.append(str);
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                try{
                    is.close();
                }catch(Exception e){}
            }
        }
    }
    
    private static boolean isJdk15 = false;
    private static Map envs = new HashMap();
    
    private static final String OS_NAME =
        System.getProperty("os.name").toLowerCase(Locale.US);
    private static final String OS_ARCH =
        System.getProperty("os.arch").toLowerCase(Locale.US);
    private static final String OS_VERSION =
        System.getProperty("os.version").toLowerCase(Locale.US);
    private static final String PATH_SEP =
        System.getProperty("path.separator");

    public static final String FAMILY_WINDOWS = "windows";
    public static final String FAMILY_9X = "win9x";
    public static final String FAMILY_NT = "winnt";
    public static final String FAMILY_OS2 = "os/2";
    public static final String FAMILY_NETWARE = "netware";
    public static final String FAMILY_DOS = "dos";
    public static final String FAMILY_MAC = "mac";
    public static final String FAMILY_TANDEM = "tandem";
    public static final String FAMILY_UNIX = "unix";
    public static final String FAMILY_VMS = "openvms";
    public static final String FAMILY_ZOS = "z/os";
    public static final String FAMILY_OS400 = "os/400";

    public static final String LINE_SEP = System.getProperty("line.separator");

    public static boolean isName(String name) {
        return isOs(null, name, null, null);
    }

    public static boolean isArch(String arch) {
        return isOs(null, null, arch, null);
    }

    public static boolean isVersion(String version) {
        return isOs(null, null, null, version);
    }
    
    public static boolean isFamily(String family) {
        return isOs(family, null, null, null);
    }

    public static boolean isOs(String family, String name, String arch,
                               String version)
    {
        boolean retValue = false;

        if (family != null || name != null || arch != null
            || version != null) {

            boolean isFamily = true;
            boolean isName = true;
            boolean isArch = true;
            boolean isVersion = true;

            if (family != null) {

                //windows probing logic relies on the word 'windows' in
                //the OS
                boolean isWindows = OS_NAME.indexOf(FAMILY_WINDOWS) > -1;
                boolean is9x = false;
                boolean isNT = false;
                if (isWindows) {
                    //there are only four 9x platforms that we look for
                    is9x = (OS_NAME.indexOf("95") >= 0
                            || OS_NAME.indexOf("98") >= 0
                            || OS_NAME.indexOf("me") >= 0
                            //wince isn't really 9x, but crippled enough to
                            //be a muchness. Ant doesnt run on CE, anyway.
                            || OS_NAME.indexOf("ce") >= 0);
                    isNT = !is9x;
                }
                if (family.equals(FAMILY_WINDOWS)) {
                    isFamily = isWindows;
                } else if (family.equals(FAMILY_9X)) {
                    isFamily = isWindows && is9x;
                } else if (family.equals(FAMILY_NT)) {
                    isFamily = isWindows && isNT;
                } else if (family.equals(FAMILY_OS2)) {
                    isFamily = OS_NAME.indexOf(FAMILY_OS2) > -1;
                } else if (family.equals(FAMILY_NETWARE)) {
                    isFamily = OS_NAME.indexOf(FAMILY_NETWARE) > -1;
                } else if (family.equals(FAMILY_DOS)) {
                    isFamily = PATH_SEP.equals(";") && !isFamily(FAMILY_NETWARE);
                } else if (family.equals(FAMILY_MAC)) {
                    isFamily = OS_NAME.indexOf(FAMILY_MAC) > -1;
                } else if (family.equals(FAMILY_TANDEM)) {
                    isFamily = OS_NAME.indexOf("nonstop_kernel") > -1;
                } else if (family.equals(FAMILY_UNIX)) {
                    isFamily = PATH_SEP.equals(":")
                        && !isFamily(FAMILY_VMS)
                        && (!isFamily(FAMILY_MAC) || OS_NAME.endsWith("x"));
                } else if (family.equals(FAMILY_ZOS)) {
                    isFamily = OS_NAME.indexOf(FAMILY_ZOS) > -1
                        || OS_NAME.indexOf("os/390") > -1;
                } else if (family.equals(FAMILY_OS400)) {
                    isFamily = OS_NAME.indexOf(FAMILY_OS400) > -1;
                } else if (family.equals(FAMILY_VMS)) {
                    isFamily = OS_NAME.indexOf(FAMILY_VMS) > -1;
                } else {
                	return false;
                }
            }
            if (name != null) {
                isName = name.equals(OS_NAME);
            }
            if (arch != null) {
                isArch = arch.equals(OS_ARCH);
            }
            if (version != null) {
                isVersion = version.equals(OS_VERSION);
            }
            retValue = isFamily && isName && isArch && isVersion;
        }
        return retValue;
    }
    
    private static void loadEnvs(){
        //Get command to run
        String cmds[] = null;
        if (isFamily("os/2")) {
            cmds = new String[] {"cmd", "/c", "set" };
        } 
        else if (isFamily("windows")) 
        {
            // Determine if we're running under XP/2000/NT or 98/95
            if (isFamily("win9x")) 
            {
                // Windows 98/95
                cmds = new String[] {"command.com", "/c", "set" };
            } else 
            {
                // Windows XP/2000/NT/2003
                cmds = new String[] {"cmd", "/c", "set" };
            }
        } 
        else if (isFamily("z/os") || isFamily("unix")) 
        {
            // On most systems one could use: /bin/sh -c env
            // Some systems have /bin/env, others /usr/bin/env, just try
            String[] cmd = new String[1];
            if (new File("/bin/env").canRead()) {
                cmd[0] = "/bin/env";
            } else if (new File("/usr/bin/env").canRead()) {
                cmd[0] = "/usr/bin/env";
            } else {
                // rely on PATH
                cmd[0] = "env";
            }
            cmds = cmd;
        }
        else if (isFamily("netware") || isFamily("os/400")) 
        {
            // rely on PATH
            cmds = new String[] {"env"};
        } 
        else if (isFamily("openvms")) 
        {
            cmds = new String[] {"show", "logical"};
        } 
        else 
        {
            // MAC OS 9 and previous
            cmds = null;
        }
        
        if (cmds == null){
            return;
        }
        //Run the command and collect output
        final StringBuffer processOutput = new StringBuffer();
        try 
        {
            Process process = Runtime.getRuntime().exec(cmds);
            final InputStream stdout = process.getInputStream();
            final InputStream stderr = process.getErrorStream();
            Thread stdoutThread = new InputStreamPollThread("getenv-stdout-poll-thread",stdout,processOutput,null);
            Thread stderrThread = new InputStreamPollThread("getenv-stderr-poll-thread",stderr,processOutput,null);
            stdoutThread.start();
            stderrThread.start();
            process.waitFor();

            //parse the output buffer
            BufferedReader in = new BufferedReader(new StringReader(processOutput.toString()));
            String var = null;
            String line, lineSep = LINE_SEP;
            while ((line = in.readLine()) != null) 
            {
                if (line.indexOf('=') == -1) {
                    // Chunk part of previous env var (UNIX env vars can
                    // contain embedded new lines).
                    if (var == null) {
                        var = lineSep + line;
                    } else {
                        var += lineSep + line;
                    }
                } else {
                    // New env var...append the previous one if we have it.
                    if (var != null) {
                        int idx = var.indexOf('=');
                        if ( idx>0 ){
                            String key = var.substring(0,idx);
                            String val = var.substring(idx+1);
                            envs.put(key, val);
                        }
                    }
                    var = line;
                }
            }
            // Since we "look ahead" before adding, there's one last env var.
            if (var != null) {
                int idx = var.indexOf('=');
                if ( idx>0 ){
                    String key = var.substring(0,idx);
                    String val = var.substring(idx+1);
                    envs.put(key, val);
                }
            }
        
        } catch (Exception e) 
        {
            //TraceUtils.rasException(e);
        }
        
    }
    
    public static String candleHome;
    public static String runArch;
    private static ResourceBundle bundle;
    private static String[] runArchs = new String[]{
        "li6263","li6243","sol283","sol286","sol293",
        "sol606","aix523","aix526","aix533","aix536",
        "lpp266","hp11","hp116","hpi116","lx8266",
        "lia266","ls3243","ls3246","ls3263","ls3266"
    };
    private static String[] supportedRunArchs = new String[]{
        "tpw","tpd","tps","tms"};
    
    public static String isValidRzVer(File f){
        String fn = f.getName().toLowerCase();
        String curArch = null;
        if ( fn.startsWith("rz") && fn.endsWith(".ver")){
            fn = fn.substring(0, fn.length()-4);
            if ( fn.length()>2 ){
                curArch = fn.substring(2);
            }
        }
        if ( curArch==null ){
            return null;
        }
        for(int i=0;i<supportedRunArchs.length;i++){
            if ( supportedRunArchs[i].equalsIgnoreCase(curArch)){
                return null;
            }
        }
        for(int i=0;i<runArchs.length;i++){
            if ( runArchs[i].equalsIgnoreCase(curArch)){
                return curArch;
            }
        }
        return curArch;
    }
    
    static{
        staticInit();
    }
    
    public static Map getEnvs(){
        return envs;
    }

    public static boolean isRunArch64(){
        boolean result = false;
        if ( runArch.equalsIgnoreCase("li6263")){
            result = false;
        }else if ( runArch.equalsIgnoreCase("li6243")){
            result = false;
        }else if ( runArch.equalsIgnoreCase("sol283")){
            result = false;
        }else if ( runArch.equalsIgnoreCase("sol286")){
            result = true;
        }else if ( runArch.equalsIgnoreCase("sol293")){
            result = false;
        }else if ( runArch.equalsIgnoreCase("sol296")){
            result = true;
        }else if ( runArch.equalsIgnoreCase("sol606")){
            result = true;
        }else if ( runArch.equalsIgnoreCase("aix523")){
            result = false;
        }else if ( runArch.equalsIgnoreCase("aix526")){
            result = true;
        }else if ( runArch.equalsIgnoreCase("aix533")){
            result = false;
        }else if ( runArch.equalsIgnoreCase("aix536")){
            result = true;
        }else if ( runArch.equalsIgnoreCase("lpp266")){
            result = true;
        }else if ( runArch.equalsIgnoreCase("hp11")){
            result = false;
        }else if ( runArch.equalsIgnoreCase("hp116")){
            result = true;
        }else if ( runArch.equalsIgnoreCase("hpi116")){
            result = true;
        }else if ( runArch.equalsIgnoreCase("lx8266")){
            result = true;
        }else if ( runArch.equalsIgnoreCase("lia266")){
            result = true;
        }else if ( runArch.equalsIgnoreCase("ls3243")){
            result = false;
        }else if ( runArch.equalsIgnoreCase("ls3246")){
            result = true;
        }else if ( runArch.equalsIgnoreCase("ls3263")){
            result = false;
        }else if ( runArch.equalsIgnoreCase("ls3266")){
            result = true;
        }else{
            //Unknown platform, check the latest char is 6
            char lastch = runArch.charAt(runArch.length()-1);
            result = lastch == '6';
        }
        return result;
    }
    
    public static boolean isJdk15(){
        return isJdk15;
    }
    
//    public static String getString(String key){
//        try{
//            String value = getMessageBundle().getString(key);
//            return value;
//        }catch(Exception e){
//            //TraceUtils.rasException(e, "Get resource string "+key+" failed.");
//            return key;
//        }
//    }
    
//    private static ResourceBundle getMessageBundle(){
//        if ( bundle != null ){
//            return bundle;
//        }
//        
//        if(ITCAMAgentConfigPanel.isInTEP()){ 
//            try{
//                Class helper = Class.forName("candle.fw.pres.dialogs.AgentManagementHelper");
//                Method loadBundleMethod = helper.getMethod("loadAgentSchemaBundle", new Class[]{String.class,Locale.class});
//                bundle = (ResourceBundle)loadBundleMethod.invoke(null,new Object[]{"rz", Locale.getDefault()});
//            }catch(Exception e){
//                e.printStackTrace();
//            }
//        }else{
//            Locale locale = Locale.getDefault();
//            String langPackDir = System.getProperty("com.ibm.tivoli.monitoring.install.langpackdir");
//            String langCode = locale.getLanguage();
//            String countryCode = locale.getCountry();
//            String baseName = "rz_dd.properties";
//            List files = new LinkedList();
//            if ( langPackDir != null ){
//                files.add(new File(candleHome+"/config",langPackDir+"/"+langCode+"_"+countryCode+"/"+baseName));
//                files.add(new File(candleHome+"/config",langPackDir+"/"+langCode+"/"+baseName));
//            }
//            files.add(new File(candleHome+"/config",langCode+"_"+countryCode+"/"+baseName));
//            files.add(new File(candleHome+"/config",langCode+"/"+baseName));
//            files.add(new File(candleHome+"/TMAITM6",baseName));
//            files.add(new File(candleHome+"/TMAITM6_x64",baseName));
//            files.add(new File(candleHome+"/config",baseName));
//            
//            File bundleFile = null;
//            for(int i=0;i<files.size();i++){
//                File file = (File)files.get(i);
//                if ( file.exists()&&file.canRead()){
//                    bundleFile = file;
//                    break;
//                }
//            }
//            if ( bundleFile != null ){
//                try{
//                    FileInputStream fis = new FileInputStream(bundleFile);
//                    bundle = new PropertyResourceBundle(fis);
//                    fis.close();
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//            }
//        }
//        return bundle;
//    }
//    
    public static String getenv(String env){
        if ( isJdk15 ){
            return System.getenv(env);
        }else{
            return (String)envs.get(env);
        }
    }
    
    private static void detectJdk15(){
        try{
            Class.forName("java.lang.ProcessBuilder");
            isJdk15 = true;
        }catch(Throwable t){
            isJdk15 = false;
        }
        if (!isJdk15){
            loadEnvs();
        }
    }
    
    private static void staticInit(){
        detectJdk15();
        
        candleHome = getenv("CANDLEHOME");
        if ( candleHome == null ){
            candleHome = getenv("CANDLE_HOME");
        }
        //Search runArch in ${CANDLEHOME}/registry
        File registryDir = new File(candleHome,"registry");
        File files[] = registryDir.listFiles();
        if ( files != null && files.length>0 ){
            for(int i=0;i<files.length;i++){
                File f = files[i];
                if ( isValidRzVer(f)!=null ){
                    runArch = isValidRzVer(f);
                    break;
                }
            }
        }
    }
    
    public static String unwrapQuota(String str){
    	String retValue=str;
        if ( retValue == null ){
            return null;
        }
        if ( retValue.startsWith("'")){
        	retValue = retValue.substring(1);
        }
        if ( retValue.startsWith("\"")){
        	retValue = retValue.substring(1);
        }
        
        if ( retValue.endsWith("'")){
        	retValue = retValue.substring(0, retValue.length()-1);
        }
        if ( retValue.endsWith("\"")){
        	retValue = retValue.substring(0, retValue.length()-1);
        }
        return retValue;
    }
    
    
}
