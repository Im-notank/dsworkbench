/* 
 * Copyright 2015 Torridity.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tor.tribes.util;

import de.tor.tribes.io.DataHolder;
import de.tor.tribes.io.TroopAmountFixed;
import de.tor.tribes.io.UnitHolder;
import de.tor.tribes.types.UnknownUnit;
import de.tor.tribes.types.ext.Village;
import de.tor.tribes.util.troops.TroopsManager;
import de.tor.tribes.util.troops.VillageTroopsHolder;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 *
 * @author Torridity
 */
public class TroopHelper {
    private final static Logger logger = LogManager.getLogger("TroopHelper");

    public static List<Village> fillSourcesWithAttacksForUnit(Village source,
            HashMap<UnitHolder, List<Village>> villagesForUnitHolder, List<Village> existingSources,
            UnitHolder unitHolder) {
        
        List<Village> sourcesForUnit = existingSources != null ? existingSources : villagesForUnitHolder.get(unitHolder);
        if (sourcesForUnit == null) {
            sourcesForUnit = new LinkedList<>();
            sourcesForUnit.add(source);
            villagesForUnitHolder.put(unitHolder, sourcesForUnit);
        } else {
            sourcesForUnit.add(source);
        }

        return sourcesForUnit;
    }

    public static void sendTroops(Village pVillage, TroopAmountFixed pTroops) {
        VillageTroopsHolder own = TroopsManager.getSingleton().getTroopsForVillage(pVillage,
                TroopsManager.TROOP_TYPE.OWN);
        VillageTroopsHolder inVillage = TroopsManager.getSingleton().getTroopsForVillage(pVillage,
                TroopsManager.TROOP_TYPE.IN_VILLAGE);
        VillageTroopsHolder onTheWay = TroopsManager.getSingleton().getTroopsForVillage(pVillage,
                TroopsManager.TROOP_TYPE.ON_THE_WAY);

        if (own != null) {// check for case that no troops are available at all
            own.getTroops().removeAmount(pTroops);
        }
        if (inVillage != null) {// check for case that troops are from place
            inVillage.getTroops().removeAmount(pTroops);
        }
        if (onTheWay != null) {// check for case that troops are from place
            onTheWay.getTroops().addAmount(pTroops);
        }
    }

    public static VillageTroopsHolder getRandomOffVillageTroops(Village pVillage) {
        TroopAmountFixed units = new TroopAmountFixed(0);
        for (UnitHolder unit : DataHolder.getSingleton().getUnits()) {
            if (unit.isOffense()) {
                units.setAmountForUnit(unit, (int) Math.rint(Math.random() * 7000.0 / unit.getPop()));
            }
        }
        VillageTroopsHolder holder = new VillageTroopsHolder(pVillage, new Date(System.currentTimeMillis()));
        holder.setTroops(units);
        return holder;
    }

    public static int getAttackForce(Village pVillage, UnitHolder pSlowestUnit) {
        VillageTroopsHolder holder = TroopsManager.getSingleton().getTroopsForVillage(pVillage,
                TroopsManager.TROOP_TYPE.OWN);
        if (holder == null) {
            return 0;
        }

        TroopAmountFixed troops = holder.getTroops();

        int force = 0;
        for (UnitHolder unit : DataHolder.getSingleton().getUnits()) {
            int value = troops.getAmountForUnit(unit);
            if (value > 0 && unit.getSpeed() <= pSlowestUnit.getSpeed()) {
                force += unit.getAttack() * value;
            }
        }
        return force;
    }

    public static int getNeededSupports(Village pVillage, TroopAmountFixed pTargetAmount, TroopAmountFixed pSplitAmount,
            boolean pAllowSimilar) {
        boolean useArcher = !DataHolder.getSingleton().getUnitByPlainName("archer").equals(UnknownUnit.getSingleton());

        VillageTroopsHolder holder = TroopsManager.getSingleton().getTroopsForVillage(pVillage,
                TroopsManager.TROOP_TYPE.IN_VILLAGE);
        TroopAmountFixed troops;
        if (holder == null) {
            holder = TroopsManager.getSingleton().getTroopsForVillage(pVillage, TroopsManager.TROOP_TYPE.SUPPORT);
        }
        
        if(holder == null) {
            troops = new TroopAmountFixed(0);
        } else {
            troops = holder.getTroops();
        }

        if (pAllowSimilar) {
            int defSplit = pSplitAmount.getDefInfantryValue();
            int defCavSplit = pSplitAmount.getDefCavalryValue();
            int defArchSplit = pSplitAmount.getDefArcherValue();

            int defDiff = pTargetAmount.getDefInfantryValue() - troops.getDefInfantryValue();
            int defCavDiff = pTargetAmount.getDefCavalryValue() - troops.getDefCavalryValue();
            int defArchDiff = pTargetAmount.getDefArcherValue() - troops.getDefArcherValue();

            int defSupport = (defDiff == 0) ? 0 : (int) (Math.ceil((double) defDiff / (double) defSplit));
            int defCavSupport = (defCavDiff == 0) ? 0 : (int) (Math.ceil((double) defCavDiff / (double) defCavSplit));
            int defArchSupport = (defArchDiff == 0) ? 0
                    : (int) (Math.ceil((double) defArchDiff / (double) defArchSplit));

            int supportsNeeded = Math.max(defSupport, defCavSupport);
            if (useArcher)
                    supportsNeeded = Math.max(supportsNeeded, defArchSupport);
            return supportsNeeded;
        } else {
            int supportsNeeded = 0;
            for (UnitHolder unit : DataHolder.getSingleton().getUnits()) {
                if (unit.isDefense() && !unit.getPlainName().equals("knight")) {
                    int diff = pTargetAmount.getAmountForUnit(unit) - troops.getAmountForUnit(unit);
                    int unitSupports = (pSplitAmount.getAmountForUnit(unit) == 0) ? 0
                            : (int) (Math.ceil((double) diff / (double) pSplitAmount.getAmountForUnit(unit)));

                    supportsNeeded = Math.max(supportsNeeded, unitSupports);
                }
            }

            return supportsNeeded;
        }
    }

    public static TroopAmountFixed getRequiredTroops(Village pVillage, TroopAmountFixed pTargetAmounts) {
        VillageTroopsHolder holder = TroopsManager.getSingleton().getTroopsForVillage(pVillage,
                TroopsManager.TROOP_TYPE.IN_VILLAGE);
        TroopAmountFixed result = pTargetAmounts.clone();
        result.removeAmount(holder.getTroops());
        return result;
    }
}