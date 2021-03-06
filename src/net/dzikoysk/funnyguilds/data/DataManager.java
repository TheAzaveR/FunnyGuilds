package net.dzikoysk.funnyguilds.data;

import java.io.File;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.util.BasicType;
import net.dzikoysk.funnyguilds.data.database.DatabaseBasic;
import net.dzikoysk.funnyguilds.util.ActionType;
import net.dzikoysk.funnyguilds.util.IndependentThread;
import org.bukkit.scheduler.BukkitTask;

public class DataManager {

	private static DataManager instance;
	private static Config config;
	private static Messages messages;
	private volatile BukkitTask task = null;
	
	public DataManager(){
		loadDefaultFiles(new String[] { "messages.yml", "config.yml" });
		instance = this;
		messages = new Messages();
		config = new Config();
		if(Config.getInstance().mysql) new DatabaseBasic();
		else new Basic();
		new Data();
		this.start();
	}
	
	public void save(){
		if(Config.getInstance().flat)
			try {
				Basic.getInstance().save();
			} catch (Exception e) {
				FunnyGuilds.error("An error occurred while saving data to flat file! Caused by: Exception");
				if(FunnyGuilds.exception(e.getCause())) e.printStackTrace();
			}
		if(Config.getInstance().mysql)
			try {
				DatabaseBasic.getInstance().save();
			} catch (Exception e) {
				FunnyGuilds.error("An error occurred while saving data to database! Caused by: Exception");
				if(FunnyGuilds.exception(e.getCause())) e.printStackTrace();
			}
		Data.getInstance().save();
	}
	
	public void start(){
		if(this.task != null) return;
		this.task = FunnyGuilds.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(FunnyGuilds.getInstance(), new Runnable() {
			 public void run() {
				 IndependentThread.action(ActionType.SAVE_DATA);
			 }
		}, Config.getInstance().dataInterval*60*20, Config.getInstance().dataInterval*60*20);
	}
	
	public void stop(){
		if(this.task != null){
			this.task.cancel();
			this.task = null;
		}
	}
	
	public static void loadDefaultFiles(String[] files){
		for(String file : files){
			File cfg = new File(FunnyGuilds.getInstance().getDataFolder() + File.separator + file);
		    if (!cfg.exists()) FunnyGuilds.getInstance().saveResource(file, true);		 
		}
	}
	
	public static File loadCustomFile(BasicType type, String name){
		switch(type){
			case GUILD:
				try{
					File file = new File(FunnyGuilds.getInstance().getDataFolder() + File.separator + "guilds" + File.separator + name + ".yml");
					if(!file.exists()){
						file.getParentFile().mkdirs();
						file.createNewFile();
					}
					return file;
				} catch (Exception e) {
					FunnyGuilds.error("[Info] Cannot load custom file (for guild)! Caused by: Exception");
					if(FunnyGuilds.exception(e.getCause())) e.printStackTrace();
				}
			case REGION:
				try{
					File file = new File(FunnyGuilds.getInstance().getDataFolder() + File.separator + "regions" + File.separator + name + ".yml");
					if(!file.exists()){
						file.getParentFile().mkdirs();
						file.createNewFile();
					}
					return file;
				} catch (Exception e) {
					FunnyGuilds.error("[Info] Cannot load custom file (for guild)! Caused by: Exception");
					if(FunnyGuilds.exception(e.getCause())) e.printStackTrace();
				}
			default:
				break;
		}
		return null;
	}
	
	public static DataManager getInstance(){
		if(instance != null) return instance;
		return new DataManager();
	}
	
	public Config getConfig(){
		return config;
	}
	
	public Messages getMessages(){
		return messages;
	}
}
