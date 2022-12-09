package ru.ostrov77.lobby.bots;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import org.bukkit.entity.EntityType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mojang.datafixers.util.Pair;

public enum BotType {
	
	REGULAR(new String[] {"PirateChris", "litb", "NoLongerAPirate"}, "dry_ocean", EntityType.CREEPER, true, EntityType.VINDICATOR, EntityType.CREEPER, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.ILLUSIONER);
	
	public final Pair<String, String>[] txs;
	public final String biome;
	public final EntityType from;
	public final boolean invTgt;
	public final EntityType[] noTgt;
	
	private static final HashMap<String, BotType> types = BTByBiome();
	
	private static HashMap<String, BotType> BTByBiome() {
		final HashMap<String, BotType> bts = new HashMap<String, BotType>();
		for (final BotType bt : values()) {
			bts.put(bt.biome, bt);
		}
		return bts;
	}

	public static BotType getBotType(final String bm) {
		return types.get(bm);
	}
	
	@SuppressWarnings("unchecked")
	private BotType(final String[] nms, final String biome, final EntityType from, final boolean invTgt, final EntityType... noTgt) {
		txs = (Pair<String, String>[]) new Pair<?, ?>[nms.length];
		this.biome = biome;
		this.from = from;
		this.invTgt = invTgt;
		this.noTgt = noTgt;
		for (int i = 0; i < txs.length; i++) {
			txs[i] = getSkin(nms[i]);
		}
	}
	
    private Pair<String, String> getSkin(final String nm) {
    	try {
    		final InputStreamReader irn = new InputStreamReader(new URL("https://api.mojang.com/users/profiles/minecraft/" + nm).openStream());
    		final String id = (String) ((JSONObject) new JSONParser().parse(irn)).get("id");
    		
    		final InputStreamReader tsr = new InputStreamReader(new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + id + "?unsigned=false").openStream());
    		final JSONObject ppt = ((JSONObject) ((JSONArray) ((JSONObject) new JSONParser().parse(tsr)).get("properties")).get(0));
    		
    		//p.getBukkitEntity().sendMessage(ppt.toString());
    		return new Pair<String, String>((String) ppt.get("value"), (String) ppt.get("signature"));
		} catch (NullPointerException | IOException | ParseException e) {
			//final Property pr = (Property) ds.bf().t().get(0).fq().getProperties().get("textures").toArray()[0];
			return new Pair<String, String>("", "");
		}
	}
}
