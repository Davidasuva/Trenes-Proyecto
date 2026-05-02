package environment;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class Environment {

    private String ip;
    private int port;
    private String serviceName;
    private static final String CONFIG_FILE_PATH="config.properties";

    private static Environment instance;

    private Environment() {
        System.setProperty("config.file.path", CONFIG_FILE_PATH);
        loadConfig();

        this.ip=System.getProperty("server.ip",System.getenv().getOrDefault("SERVER_IP","192.168.1.4"));
        this.port= Integer.parseInt(System.getProperty("server.port",System.getenv().getOrDefault("SERVER_PORT","1808")));
        this.serviceName = System.getProperty("server.name", System.getenv().getOrDefault("SERVER_NAME", "tickets"));
    }

    private void loadConfig() {
        Properties config= new Properties();
        try(FileInputStream fin=new FileInputStream(new File(System.getProperty("config.file.path",System.getenv().getOrDefault("CONFIG_FILE_PATH","config.properties"))))){
            config.load(fin);
            System.setProperty("server.ip", (String) config.get("SERVER_IP"));
            System.setProperty("server.port", (String) config.get("SERVER_PORT"));
            System.setProperty("server.name", (String) config.get("SERVER_NAME"));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static Environment getInstance() {
        if(instance==null){
            instance=new Environment();
        }
        return instance;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getServiceName() {
        return serviceName;
    }
}
