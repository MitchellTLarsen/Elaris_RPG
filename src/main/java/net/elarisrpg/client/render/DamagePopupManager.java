package net.elarisrpg.client.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DamagePopupManager {
    private static final List<DamagePopup> popups = new ArrayList<>();

    public static void addPopup(DamagePopup popup) {
        popups.add(popup);
    }

    public static void tick() {
        Iterator<DamagePopup> iter = popups.iterator();
        while (iter.hasNext()) {
            DamagePopup popup = iter.next();
            popup.tick();
            if (popup.isExpired()) {
                iter.remove();
            }
        }
    }

    public static List<DamagePopup> getPopups() {
        return popups;
    }
}
