package ru.ostrov77.lobby.quest;

import java.util.HashSet;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import eu.endercentral.crazy_advancements.NameKey;
import eu.endercentral.crazy_advancements.advancement.Advancement;
import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay;
import eu.endercentral.crazy_advancements.advancement.AdvancementFlag;
import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay.AdvancementFrame;
import ru.ostrov77.lobby.Main;
import ru.ostrov77.lobby.area.AreaManager;
import ru.ostrov77.lobby.area.LCuboid;
import eu.endercentral.crazy_advancements.advancement.AdvancementVisibility;


public class QuestAdvance {

	public static final HashSet<Advancement> adm = new HashSet<>();

	public static Advancement crtAdv(final String key, final String name, final String desc, final Material icon, final float x, final float y, final String back, final Advancement parent, final AdvancementFrame frame, final AdvancementVisibility vis, final AdvancementFlag... flags) {
    	final AdvancementDisplay dis = new AdvancementDisplay(icon, name, desc, frame, back, vis);
    	dis.setCoordinates(x, y);
		return parent == null ? new Advancement(new NameKey(key), dis, flags) : new Advancement(parent, new NameKey(key), dis, flags);
	}
	
	public static void loadQuestAdv() {
		adm.clear();
        int x = -1;
        adm.add(QuestAdvance.crtAdv("spawn", "§3§lАрхипелаг          ", "Начало вашего пути...", Material.HEART_OF_THE_SEA, 0, 0, "textures.block.stone", null, AdvancementFrame.TASK, QuestAdvance.visOnDisc(""), AdvancementFlag.SHOW_TOAST));
        adm.add(QuestAdvance.crtAdv("midgard", "§c§lХуторок          ", "Северная деревушка...", Material.CAMPFIRE, 0, 0, "textures.block.stone", null, AdvancementFrame.TASK, QuestAdvance.visOnDisc("midgard"), AdvancementFlag.SHOW_TOAST));
        adm.add(QuestAdvance.crtAdv("daaria", "§a§lПерелесок          ", "Обычная дубавая роща...", Material.OAK_LOG, 0, 0, "textures.block.stone", null, AdvancementFrame.TASK, QuestAdvance.visOnDisc("daaria"), AdvancementFlag.SHOW_TOAST));
        adm.add(QuestAdvance.crtAdv("skyworld", "§3§lОстровки          ", "И их дефицит ресурсов...", Material.FLOWERING_AZALEA, 0, 0, "textures.block.stone", null, AdvancementFrame.TASK, QuestAdvance.visOnDisc("skyworld"), AdvancementFlag.SHOW_TOAST));
        adm.add(QuestAdvance.crtAdv("nopvp", "§e§lОазис          ", "Развлекайся с друзьями...", Material.HONEYCOMB, 0, 0, "textures.block.stone", null, AdvancementFrame.TASK, QuestAdvance.visOnDisc("nopvp"), AdvancementFlag.SHOW_TOAST));
        adm.add(QuestAdvance.crtAdv("pvp", "§6§lДолина Войны          ", "Покажи свой скилл...", Material.NETHERITE_AXE, 0, 0, "textures.block.stone", null, AdvancementFrame.TASK, QuestAdvance.visOnDisc("pvp"), AdvancementFlag.SHOW_TOAST));
        adm.add(QuestAdvance.crtAdv("parkur", "§b§lБерезовый Парк          ", "С блока на блок...", Material.FEATHER, 0, 0, "textures.block.stone", null, AdvancementFrame.TASK, QuestAdvance.visOnDisc("parkur"), AdvancementFlag.SHOW_TOAST));
        adm.add(QuestAdvance.crtAdv("arcaim", "§9§lРисталище          ", "Прояви креативность...", Material.BEDROCK, 0, 0, "textures.block.stone", null, AdvancementFrame.TASK, QuestAdvance.visOnDisc("arcaim"), AdvancementFlag.SHOW_TOAST));
        adm.add(QuestAdvance.crtAdv("sedna", "§4§lКровавая Пустошь          ", "Выживает сильнейший...", Material.CRIMSON_NYLIUM, 0, 0, "textures.block.stone", null, AdvancementFrame.TASK, QuestAdvance.visOnDisc("sedna"), AdvancementFlag.SHOW_TOAST));
        adm.add(QuestAdvance.crtAdv("newbie", "§3§lМесто Прибытия          ", "Наконец-то здесь...", Material.OAK_BOAT, 0, 0, "textures.block.stone", null, AdvancementFrame.TASK, QuestAdvance.visOnDisc("newbie"), AdvancementFlag.SHOW_TOAST));
        for (final Quest q : Quest.values()) {
        	adm.add(crtAdv(String.valueOf(q.code), q.displayName, q.description, q.icon, x, 1, "block.stone", getParentAdv(q.attachedArea), AdvancementFrame.TASK, AdvancementVisibility.ALWAYS, AdvancementFlag.SHOW_TOAST));
            x += 1;
        }
	}

	public static Advancement getParentAdv(final String area) {
		for (final Advancement a : adm) {
			if (area.isEmpty()) {
				return getParentAdv("spawn");
			} else if (a.getName().getKey().equals(area)) {
				return a;
			}
		}
		return null;
	}

	public static AdvancementVisibility visOnDisc(final String area) {
		return new AdvancementVisibility() {
			@Override
			public boolean isVisible(final Player p, final Advancement adv) {
				final LCuboid lc = AreaManager.getCuboid(area);
				return lc == null ? true : Main.getLobbyPlayer(p).isAreaDiscovered(lc.id);
			}
		};
	}
}
