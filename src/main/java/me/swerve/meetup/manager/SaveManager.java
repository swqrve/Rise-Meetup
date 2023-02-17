package me.swerve.meetup.manager;

import lombok.Getter;
import lombok.Setter;
import me.swerve.meetup.file.FileManager;
import org.bson.Document;

import java.util.HashMap;
import java.util.UUID;

@Getter @Setter
public class SaveManager {

    private final UUID uuid;
    private HashMap<String, Integer> loadout;
    /* Current Session */
    private int sessionKills;

    /* All-Time Stats */
    private int lifeKills;
    private int lifeDeaths;
    private int lifeGamesWon;

    public SaveManager(Document doc, UUID uuid) {
        this.uuid = uuid;

        lifeKills =    (int) doc.get("kills");
        lifeDeaths =   (int) doc.get("deaths");
        lifeGamesWon = (int) doc.get("wins");

        loadout = FileManager.getPlayerLoadout(uuid);
    }

    public void updateStats(int death, int won) {
        lifeKills += sessionKills;
        lifeDeaths += death;
        lifeGamesWon += won;
    }

    public void updateLoadout(HashMap<String, Integer> newLoadout) {
        loadout = newLoadout;
    }

    public void saveInfo() {
        updateStats(0, 0);
        FileManager.savePlayerDocument(this, uuid);
        FileManager.savePlayerLoadout(uuid, loadout);
    }
}
