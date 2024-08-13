package ru.ostrov77.lobby;


import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.enums.Game;
import ru.komiss77.modules.games.GM;
import ru.komiss77.modules.games.GameInfo;
import ru.komiss77.modules.player.PM;
import ru.komiss77.modules.world.XYZ;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.ostrov77.lobby.area.AreaManager;
import ru.ostrov77.lobby.area.CuboidInfo;


public class OsComMenu implements InventoryProvider {

    private final ItemStack[] emt = getEmpty();

    private ItemStack[] getEmpty() {
        final ItemStack[] its = new ItemStack[54];
        final ItemStack rail = new ItemBuilder(Material.ACTIVATOR_RAIL).name("§8.").build();
        final ItemStack bubble = new ItemBuilder(Material.GLOW_LICHEN).name("§8.").build();
        for (int i = 0; i != 54; i++) {
            its[i] = switch (i % 9) {
                case 0, 8 -> rail;
                default -> bubble;
            };
        }
        its[53] = its[45] = new ItemBuilder(Material.LODESTONE).name("§8.").build();
        its[52] = its[46] = new ItemBuilder(Material.SMOOTH_STONE_SLAB).name("§8.").build();
        return its;
    }

    @Override
    public void init(final Player p, final InventoryContent its) {
        p.playSound(p.getLocation(), Sound.ITEM_TRIDENT_RETURN, 1, 1);
        its.getInventory().setContents(emt);

        for (final CuboidInfo ci : CuboidInfo.values()) {
            final Game gm = ci.game;
            if (gm != null) {
                final int slot = switch (ci) {
                    case DAARIA -> 3;
                    case SKYWORLD -> 5;
                    case ARCAIM -> 11;
                    case SEDNA -> 15;
                    case MIDGARD -> 29;
                    case PARKUR -> 33;
                    default -> 40;
                };

                final GameInfo gi = GM.getGameInfo(gm);
                its.set(slot, ClickableItem.of(new ItemBuilder(gi.mat).amount(Math.max(gi.getOnline(), 1))
                    .name(gm.displayName).build(), e -> {
                        PM.getOplayer(p, LobbyPlayer.class).transport(p,
                            new XYZ(AreaManager.getCuboid(ci).spawnPoint), false);
                        p.closeInventory();
                    }
                ));
            }
        }

        its.set(22, ClickableItem.of( new ItemBuilder(Material.RECOVERY_COMPASS)
            .name("§a§lМ§d§lИ§c§lН§e§lИ§9§lИ§5§lГ§4§lР§b§lЫ")
            .lore("")
            .lore("§e§lБедВарс")
            .lore("§5§lКонтра")
            .lore("§4§lГолодные Игры")
            .lore("§5§lСкайВарс")
            .lore("§3§lПрятки")
            .lore("§b§lКит-ПВП")
            .lore("§a§lБитва Строителей")
            .lore("§аи другие...").build(), e -> {
                PM.getOplayer(p, LobbyPlayer.class).transport(p, new XYZ(AreaManager.getCuboid(
                    Main.rnd.nextBoolean() ? CuboidInfo.PVP : CuboidInfo.NOPVP).spawnPoint), false);
                p.closeInventory();
            }
        ));
    }
}
