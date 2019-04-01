package net.simon987.cubotplugin;

import net.simon987.server.GameServer;
import net.simon987.server.assembly.HardwareModule;
import net.simon987.server.assembly.Status;
import net.simon987.server.game.item.Item;
import net.simon987.server.game.objects.Action;
import net.simon987.server.game.objects.Attackable;
import net.simon987.server.game.objects.GameObject;
import net.simon987.server.game.objects.InventoryHolder;
import org.bson.Document;

import java.awt.*;
import java.util.ArrayList;

public class CubotLaser extends HardwareModule {

    /**
     * Hardware ID (Should be unique)
     */
    static final char HWID = 0x0002;

    public static final int DEFAULT_ADDRESS = 2;

    private static final int LASER_WITHDRAW = 1;
    private static final int LASER_DEPOSIT = 2;
    private static final int LASER_ATTACK = 3;

    private static final int LASER_DAMAGE = 25;

    public CubotLaser(Cubot unit) {
        super(null, unit);
    }

    public CubotLaser(Document document, Cubot cubot) {
        super(document, cubot);
    }

    private Cubot getCubot() {
        return (Cubot) unit;
    }

    @Override
    public char getId() {
        return HWID;
    }

    @Override
    //TODO: Refactor this method..
    public void handleInterrupt(Status status) {

        int a = getCpu().getRegisterSet().getRegister("A").getValue();
        int b = getCpu().getRegisterSet().getRegister("B").getValue();

        if (a == LASER_WITHDRAW) {

            Point frontTile = unit.getFrontTile();
            ArrayList<GameObject> objects = unit.getWorld().getGameObjectsBlockingAt(frontTile.x, frontTile.y);

            if (unit.getCurrentAction() == Action.IDLE && objects.size() > 0) {
                if (objects.get(0) instanceof InventoryHolder) {

                    if (((InventoryHolder) objects.get(0)).canTakeItem(b)) {
                        if (unit.spendEnergy(30)) {
                            //Take the item
                            ((InventoryHolder) objects.get(0)).takeItem(b);
                            unit.giveItem(GameServer.INSTANCE.getRegistry().makeItem(b));
                            unit.setCurrentAction(Action.WITHDRAWING);
                        }
                    }
                }
            }

        } else if (a == LASER_DEPOSIT) {
            Point frontTile = unit.getFrontTile();
            ArrayList<GameObject> objects = unit.getWorld().getGameObjectsBlockingAt(frontTile.x, frontTile.y);

            CubotInventory inv = ((CubotInventory) getCubot().getHardware(CubotInventory.class));
            Item item = inv.getCurrentItem();

            if (unit.getCurrentAction() == Action.IDLE && objects.size() > 0) {
                if (objects.get(0) instanceof InventoryHolder) {
                    if (((InventoryHolder) objects.get(0)).canPlaceItem(item.getId())) {
                        if (unit.spendEnergy(30)) {
                            //Place the item
                            ((InventoryHolder) objects.get(0)).placeItem(inv.removeItem());
                            unit.setCurrentAction(Action.DEPOSITING);
                        }
                    }
                }
            }
        } else if (a == LASER_ATTACK) {

            if (unit.getCurrentAction() == Action.IDLE) {
                if (unit.spendEnergy(70)) {

                    //Get object directly in front of the Cubot
                    Point frontTile = unit.getFrontTile();
                    ArrayList<GameObject> objects = unit.getWorld().getGameObjectsAt(frontTile.x, frontTile.y);

                    //todo: Add option in config to allow PvP
                    if (objects.size() > 0 && objects.get(0) instanceof Attackable && !(objects.get(0) instanceof Cubot)) {
                        ((Attackable) objects.get(0)).damage(LASER_DAMAGE);
                    }
                }

                unit.setCurrentAction(Action.ATTACKING);
            }
        }
    }
}
