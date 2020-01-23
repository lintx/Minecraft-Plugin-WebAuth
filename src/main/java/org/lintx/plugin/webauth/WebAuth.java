package org.lintx.plugin.webauth;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.bstats.bungeecord.Metrics;
import org.lintx.plugin.webauth.config.DatabaseConfig;
import org.lintx.plugin.webauth.httpserver.Caches;
import org.lintx.plugin.webauth.httpserver.NettyHttpServer;
import org.lintx.plugin.webauth.sql.Model;
import org.lintx.plugin.webauth.sql.MySql;
import org.lintx.plugin.webauth.sql.SQLite;

import java.io.*;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class WebAuth extends Plugin {
    public static WebAuth plugin;
    private SQLite sqLite;
    private MySql mySql;
    private Model model;
    private Config config;
    private NettyHttpServer httpServer;
    private ScheduledTask task;
    @Override
    public void onEnable() {
        autoFreedWeb();
        plugin = this;
        config = Config.getInstance();

        reload();

        getProxy().getPluginManager().registerListener(this,new Listeners(this));
        getProxy().getPluginManager().registerCommand(this,new Commands(this,"webauth",null,"auth","wa"));

        Metrics metrics = new Metrics(this);
    }

    @Override
    public void onDisable() {
        if (mySql!=null){
            mySql.close();
        }
        if (httpServer!=null){
            httpServer.stop();
        }
    }

    public Model getModel() {
        return model;
    }

    private void autoFreedWeb(){
        String rootFolder = "web/";
        File folder = this.getDataFolder();
        folder.mkdir();
        new File(folder,rootFolder).mkdir();

        try (ZipFile zipFile = new ZipFile(getFile())) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                copyFile(zipFile, entry, rootFolder);
            }
        } catch (IOException ignored) {

        }
    }

    private void copyFile(ZipFile zipFile,ZipEntry entry,String rootFolder){
        String name = entry.getName();
        if(!name.startsWith(rootFolder)){
            return;
        }

        File file = new File(getDataFolder(), name);
        if(entry.isDirectory()) {
            file.mkdirs();
        }else {
            if (file.exists()){
                return;
            }
            if (file.getParentFile().isDirectory()){
                file.getParentFile().mkdir();
            }
            FileOutputStream outputStream = null;
            InputStream inputStream = null;
            try {
                byte[] buffer = new byte[1024];
                outputStream = new FileOutputStream(file);
                inputStream = zipFile.getInputStream(entry);
                int len;
                while ((len = inputStream.read(buffer)) >= 0) {
                    outputStream.write(buffer,  0,  len);
                }
            } catch (IOException ignored) {

            } finally {
                try {
                    outputStream.close();
                } catch (IOException ignored) {

                }
                try {
                    inputStream.close();
                } catch (IOException ignored) {

                }
            }
        }
    }

    void reload(){
        int oldPort = config.getWebPort();
        config.load(this);
        if (oldPort!=config.getWebPort() && config.getWebPort()>1024){
            if (httpServer!=null){
                httpServer.stop();
            }
            if (task!=null){
                task.cancel();
            }
            httpServer = new NettyHttpServer(config.getWebPort(),this,new File(getDataFolder(),"web"));
            getProxy().getScheduler().runAsync(this, () -> httpServer.start());
            task = getProxy().getScheduler().schedule(this, Caches::checkCaches,10L,10L, TimeUnit.SECONDS);
        }

        if (config.getDatabaseConfig().getType()== DatabaseConfig.DatabaseType.MYSQL){
            if (mySql!=null){
                mySql.close();
            }
            mySql = new MySql(config.getDatabaseConfig().getMysqlUri(),config.getDatabaseConfig().getMysqlUser(),config.getDatabaseConfig().getMysqlPassword(),config.getDatabaseConfig().getTimeout());
            model = new Model(mySql);
        }else {
            sqLite = new SQLite(getDataFolder(),"database");
            model = new Model(sqLite);
        }
    }
}
