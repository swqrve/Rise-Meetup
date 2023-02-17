package me.swerve.meetup.file;

import me.swerve.meetup.RiseMeetup;
import me.swerve.meetup.manager.SaveManager;
import me.swerve.meetup.player.MeetupPlayer;
import org.bson.Document;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class FileManager {

    public static Document getPlayerDocument(UUID uuid) {
        File dataFolder = new File(RiseMeetup.getInstance().getDataFolder().getPath() + "/data/");
        if(!dataFolder.exists()) dataFolder.mkdirs();

        File playerFile = new File(dataFolder.getPath() + "/" + uuid.toString() + "-stats.json");
        Document document = new Document("uuid", uuid.toString()).append("uuid", uuid.toString());
        if(playerFile.exists()) document = FileUtility.readFromFile(playerFile);

        if(document.get("kills") == null) {
            document.append("kills", 0);
            document.append("deaths", 0);
            document.append("wins", 0);
        }

        return document;
    }

    public static void savePlayerDocument(SaveManager manager, UUID uuid) {
        Document document = new Document("uuid", uuid.toString()).append("uuid", uuid.toString());

        File dataFolder = new File(RiseMeetup.getInstance().getDataFolder().getPath() + "/data/");
        if(!dataFolder.exists()) dataFolder.mkdirs();

        document.append("kills", manager.getLifeKills());
        document.append("deaths", manager.getLifeDeaths());
        document.append("wins", manager.getLifeGamesWon());

        File playerFile = new File(dataFolder.getPath() + "/" + uuid.toString() + "-stats.json");
        FileUtility.write(playerFile, document);
    }

    public static HashMap<String, Integer> getPlayerLoadout(UUID uuid) {
        File dataFolder = new File(RiseMeetup.getInstance().getDataFolder().getPath() + "/data/");
        if (!dataFolder.exists()) dataFolder.mkdirs();

        File loadoutFile = new File(dataFolder.getPath() + "/" +uuid + "-loadout.json");
        Document document = new Document("uuid-loadout", uuid.toString());

        if (loadoutFile.exists()) document = FileUtility.readFromFile(loadoutFile);

        HashMap<String, Integer> defaultLoadout = new HashMap<>();

        defaultLoadout.put("SwordIndex", 0);
        defaultLoadout.put("RodIndex", 1);
        defaultLoadout.put("BowIndex", 2);
        defaultLoadout.put("BlockOneIndex", 3);
        defaultLoadout.put("LavaOneIndex", 4);
        defaultLoadout.put("WaterOneIndex", 5);
        defaultLoadout.put("GoldenAppleIndex", 6);
        defaultLoadout.put("GoldenHeadIndex", 7);
        defaultLoadout.put("PickaxeIndex", 8);

        defaultLoadout.put("AxeIndex", 35);
        defaultLoadout.put("LavaTwoIndex", 31);
        defaultLoadout.put("WaterTwoIndex", 32);
        defaultLoadout.put("ArrowIndex", 9);
        defaultLoadout.put("SteakIndex", 10);
        defaultLoadout.put("BlockTwoIndex", 30);
        defaultLoadout.put("AnvilIndex", 17);
        defaultLoadout.put("EnchantIndex", 16);
        defaultLoadout.put("XPIndex", 15);

        if(document.get("items") == null) {
            List<Document> itemDocs = new ArrayList<>();

            defaultLoadout.forEach((key, value) -> {
                itemDocs.add(new Document("key", key).append("value", value));
            });

            document.append("items", itemDocs);
        }

        HashMap<String, Integer> toReturn = new HashMap<>();
        ((List<Document>) document.get("items")).forEach(permDoc -> toReturn.put(permDoc.getString("key"), permDoc.getInteger("value")));

        return toReturn;
    }

    public static void savePlayerLoadout(UUID uuid, HashMap<String, Integer> map) {
        List<Document> itemDocuments = new ArrayList<>();

        map.forEach((key, value) -> itemDocuments.add(new Document("key", key).append("value", value)));

        Document mainDocument = new Document("uuid-loadout", uuid.toString()).append("items", itemDocuments);

        File dataFolder = new File(RiseMeetup.getInstance().getDataFolder().getPath() + "/data/");
        if(!dataFolder.exists()) dataFolder.mkdirs();

        File permissionFile = new File(dataFolder.getPath() + "/" + uuid.toString() + "-loadout.json");
        FileUtility.write(permissionFile, mainDocument);
    }
}
