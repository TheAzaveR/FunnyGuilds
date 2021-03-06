package net.dzikoysk.funnyguilds.data.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;
import net.dzikoysk.funnyguilds.basic.util.GuildUtils;
import net.dzikoysk.funnyguilds.basic.util.UserUtils;
import net.dzikoysk.funnyguilds.util.Parser;
import net.dzikoysk.funnyguilds.util.StringUtils;

public class DatabaseGuild {
	
	Guild guild;
	
	public DatabaseGuild(Guild guild){
		this.guild = guild;
	}
	
	public void save(Database db) {
		String update = getInsert();
		if(update != null) db.executeUpdate(update);
	}
	
	public void delete() {
		Database db = Database.getInstance();
		db.openConnection();
		StringBuilder update = new StringBuilder();
		update.append("DELETE FROM guilds WHERE uuid='");
		update.append(guild.getUUID().toString());
		update.append("';");
		db.executeUpdate(update.toString());
		db.closeConnection();
	}
	
	public void updatePoints(){
		Database db = Database.getInstance();
		db.openConnection();
		StringBuilder update = new StringBuilder();
		update.append("UPDATE guilds SET points=");
		update.append(guild.getRank().getPoints());
		update.append(" WHERE uuid='");
		update.append(guild.getUUID().toString());
		update.append("';");
		db.executeUpdate(update.toString());
		db.closeConnection();
	}

	public String getInsert(){
		StringBuilder sb = new StringBuilder();
		String members = StringUtils.toString(UserUtils.getNames(guild.getMembers()), false);
		String regions = StringUtils.toString(guild.getRegions(), false);
		String allies = StringUtils.toString(GuildUtils.getNames(guild.getAllies()), false);
		String enemies = StringUtils.toString(GuildUtils.getNames(guild.getEnemies()), false);
		sb.append("INSERT INTO guilds (");
		sb.append("uuid, name, tag, owner, home, region, members, regions, allies, enemies, points");
		sb.append(") VALUES (");
		sb.append("'" + guild.getUUID().toString() + "',");
		sb.append("'" + guild.getName() + "',");
		sb.append("'" + guild.getTag() + "',");
		sb.append("'" + guild.getOwner().getName() + "',");
		sb.append("'" + Parser.toString(guild.getHome()) + "',");
		sb.append("'" + guild.getRegion() + "',");
		sb.append("'" + members + "',");
		sb.append("'" + regions + "',");
		sb.append("'" + allies + "',");
		sb.append("'" + enemies + "',");
		sb.append("" + guild.getRank().getPoints() + "");
		sb.append(") ON DUPLICATE KEY UPDATE ");
		sb.append("name='" + guild.getName() + "',");
		sb.append("tag='" + guild.getTag() + "',");
		sb.append("owner='" + guild.getOwner().getName() + "',");
		sb.append("home='" + Parser.toString(guild.getHome()) + "',");
		sb.append("region='" + guild.getRegion() + "',");
		sb.append("members='" + members + "',");
		sb.append("regions='" + regions + "',");
		sb.append("allies='" + allies + "',");
		sb.append("enemies='" + enemies + "',");
		sb.append("points=" + guild.getRank().getPoints() + ";");
		return sb.toString();
	}
	
	public static Guild deserialize(ResultSet rs) throws SQLException{
		if(rs == null) return null;
		
		String id = rs.getString("uuid");
		String name = rs.getString("name");
		String tag = rs.getString("tag");
		String os = rs.getString("owner");
		String home = rs.getString("home");
		String region = rs.getString("region");
		String m = rs.getString("members");
		String rgs = rs.getString("regions");
		String als = rs.getString("allies");
		String ens = rs.getString("enemies");
		
		if(name == null || tag == null || os == null){
			FunnyGuilds.error("Cannot deserialize guild! Caused by: uuid/name/tag/owner is null");
			return null;
		}
		
		UUID uuid = UUID.randomUUID();
		if(id != null) uuid = UUID.fromString(id);
		
		User owner = User.get(os);
		List<User> members = new ArrayList<>();
		if(m != null && !m.equals("")) members = UserUtils.getUsers(StringUtils.fromString(m));
		List<String> regions = StringUtils.fromString(rgs);
		List<Guild> allies = new ArrayList<>();
		if(als != null && !als.equals("")) allies = GuildUtils.getGuilds(StringUtils.fromString(als));
		List<Guild> enemies = new ArrayList<>();
		if(ens != null && !ens.equals("")) enemies = GuildUtils.getGuilds(StringUtils.fromString(ens));

		Object[] values = new Object[10];
		values[0] = uuid;
		values[1] = name;
		values[2] = tag;
		values[3] = owner;
		values[4] = Parser.parseLocation(home);
		values[5] = region;
		values[6] = members;
		values[7] = regions;
		values[8] = allies;
		values[9] = enemies;
		Guild guild = Guild.deserialize(values);
		return guild;
	}

}
