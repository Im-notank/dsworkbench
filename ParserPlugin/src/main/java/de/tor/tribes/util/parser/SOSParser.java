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
package de.tor.tribes.util.parser;

import de.tor.tribes.io.DataHolder;
import de.tor.tribes.io.UnitHolder;
import de.tor.tribes.types.Attack;
import de.tor.tribes.types.SOSRequest;
import de.tor.tribes.types.TargetInformation;
import de.tor.tribes.types.ext.Tribe;
import de.tor.tribes.types.ext.Village;
import de.tor.tribes.util.GenericParserInterface;
import de.tor.tribes.util.ServerSettings;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Torridity
 */
public class SOSParser implements GenericParserInterface<SOSRequest> {
    private static Logger logger = LogManager.getLogger("SOSParser");
    /*
     * [b]Verteidiger[/b] Name: [player]Rattenfutter[/player] Stamm: [ally][KdS][/ally] Punkte: 1887516
     *
     * [b]Angegriffenes Dorf[/b] [coord]444|867[/coord] Punkte: 10000 Stufe des Walls: 20
     *
     *
     * [b]Anwesende Truppen[/b] [unit]axe[/unit] 8000 [unit]spy[/unit] 6
     *
     * [b]1. Angriff[/b] Angreifer: [player]Rattenfutter[/player] Stamm: [ally][KdS][/ally] Punkte: 1887516 Herkunft: [coord]444|868[/coord]
     * Ankunftszeit: 24.03.12 18:30:10:935 [b]Angegriffenes Dorf[/b] [coord]443|871[/coord] Punkte: 10014 Stufe des Walls: 20
     *
     *
     * [b]Anwesende Truppen[/b] [unit]axe[/unit] 41 [unit]spy[/unit] 45 [unit]ram[/unit] 8
     *
     * [b]1. Angriff[/b] Angreifer: [player]Rattenfutter[/player] Stamm: [ally][KdS][/ally] Punkte: 1887516 Herkunft: [coord]444|868[/coord]
     * Ankunftszeit: 23.03.12 18:44:00:931
     *
     * [b]2. Angriff[/b] Angreifer: [player]Rattenfutter[/player] Stamm: [ally][KdS][/ally] Punkte: 1887516 Herkunft: [coord]444|867[/coord]
     * Ankunftszeit: 23.03.12 18:44:15:931
     */

    @Override
    public List<SOSRequest> parse(String pData) {
        logger.debug("Start parsing SOS request");
        List<SOSRequest> requests = new LinkedList<>();
        try {
            HashMap<Tribe, SOSRequest> parsedData = parseRequests(pData);
            if (parsedData.isEmpty()) {
                logger.debug("Check short version");
                parsedData = parseRequestsShort(pData);
            } else {
                logger.debug("Got results for long version");
            }
            CollectionUtils.addAll(requests, parsedData.values());
        } catch (Exception ignored) {
        }
        return requests;
    }

    private HashMap<Tribe, SOSRequest> parseRequests(String pData) {
        String[] lines = pData.split("\n");
        HashMap<Tribe, SOSRequest> requests = new HashMap<>();
        SOSRequest currentRequest = null;
        boolean waitForTarget = false;
        boolean waitForTroops = false;
        Village source = null;
        Village target = null;

        SimpleDateFormat dateFormat;
        boolean useMillis = ServerSettings.getSingleton().isMillisArrival();
        if (!useMillis) {
            dateFormat = new SimpleDateFormat(getVariable("sos.date.format"), ParserVariableManager.getSingleton().getDateLocale());
        } else {
            dateFormat = new SimpleDateFormat(getVariable("sos.date.format.ms"), ParserVariableManager.getSingleton().getDateLocale());
        }

        for (String line : lines) {
            if (line.contains(getVariable("sos.defender"))) {
                //start new support request
                if (currentRequest != null && currentRequest.getDefender() != null) {
                    requests.put(currentRequest.getDefender(), currentRequest);
                }

                currentRequest = new SOSRequest();
                //  System.out.println("Create new reuqest");
            } else if (line.contains(getVariable("sos.name"))) {
                //defender name
                String defender = line.replaceAll(getVariable("sos.name"), "").replaceAll("\\[player\\]", "").replaceAll("\\[\\/player\\]", "").trim();
                // System.out.println("Defender: " + defender);
                Tribe defTribe = DataHolder.getSingleton().getTribeByName(defender);
                if (requests.get(defTribe) != null) {
                    currentRequest = requests.get(defTribe);
                } else {
                    currentRequest.setDefender(defTribe);
                    requests.put(defTribe, currentRequest);
                }
            } else if (line.contains(getVariable("sos.destination"))) {
                //new target village
                waitForTarget = true;
                //  System.out.println("Wait for target");
            } else if (line.contains(getVariable("sos.source"))) {
                Village sourceVillage = VillageParser.parseSingleLine(line);
                if (sourceVillage != null) {
                    //set target
                    source = sourceVillage;
                    //System.out.println("source: " + source);
                }
            } else if (line.contains(getVariable("sos.arrive.time"))) {
                //arrive time
                try {
                    String arrive = line.replaceAll(getVariable("sos.arrive.time"), "").trim();
                    Date arriveDate = dateFormat.parse(arrive);
                    // System.out.println("Got arrive");
                    if (!useMillis) {//add current millis to be able to compare times
                        arriveDate = new Date(arriveDate.getTime() + Calendar.getInstance().get(Calendar.MILLISECOND));
                    }
                    currentRequest.getTargetInformation(target).addAttack(source, arriveDate);
                } catch (Exception e) {
                    //  System.out.println("NO ARRIVE!!");
                }
            } else if (line.contains(getVariable("sos.wall.level"))) {
                try {
                    int wallLevel = Integer.parseInt(line.replaceAll(getVariable("sos.wall.level"), "").trim());
                    currentRequest.getTargetInformation(target).setWallLevel(wallLevel);
                } catch (Exception ignored) {
                }
            } else if (line.contains(getVariable("sos.troops.in.village"))) {
                // System.out.println("WAIT FOR TROOPS");
                waitForTroops = true;
            } else {
                if (waitForTarget) {
                    Village targetVillage = VillageParser.parseSingleLine(line);
                    if (targetVillage != null) {
                        //set target
                        target = targetVillage;
                        currentRequest.addTarget(target);
                        // System.out.println("Got target " + target);
                        waitForTarget = false;
                    }
                } else if (waitForTroops) {
                    //System.out.println("WAIT TROOPS " + line);
                    try {
                        String plainUnit = line.replaceAll("\\[unit\\]", "").replaceAll("\\[\\/unit\\]", "").trim();
                        //System.out.println("UNIT: " + plainUnit);
                        if (plainUnit.trim().length() != 0) {
                            //skip empty line

                            String[] unitAmount = plainUnit.split(" ");
                            UnitHolder u = DataHolder.getSingleton().getUnitByPlainName(unitAmount[0]);

                            //System.out.println("UNIT : " + u);
                            Integer amount = Integer.parseInt(unitAmount[1]);
                            // System.out.println("AM " + amount);
                            currentRequest.getTargetInformation(target).addTroopInformation(u, amount);
                        }
                    } catch (Exception e) {
                        waitForTroops = false;
                    }
                }
            }
        }

        return requests;
    }

    /**
     * [b]Dorf:[/b] [coord]454|943[/coord] [b]Wallstufe:[/b] 20 [b]Verteidiger:[/b] 18314 13982 0 18659 353 0 0 4825 0 17 0 0
     *
     * LKAV?, 20 Checkpoint (465|883) , K84 [coord]465|883[/coord] --> Ankunftszeit: 24.08.11 23:38:53:674 [player]Frank R.[/player] Ramm,
     * 31 dieBrüder 02 (460|888) , K84 [coord]460|888[/coord] --> Ankunftszeit: 25.08.11 17:14:33:064 [player]Frank R.[/player]
     *
     * [b]Dorf:[/b] [coord]453|943[/coord] [b]Wallstufe:[/b] 20 [b]Verteidiger:[/b] 14998 13360 5929 14998 100 2809 260 3000 300 40 0 0
     *
     * Ramm, 22 Checkpoint (452|898) , K84 [coord]452|898[/coord] --> Ankunftszeit: 25.08.11 12:01:23:952 [player]Frank R.[/player]
     *
     * [b]Dorf:[/b] [coord]452|944[/coord] [b]Wallstufe:[/b] 20 [b]Verteidiger:[/b] 18581 13354 0 18581 410 0 0 4994 0 19 0 0
     *
     * Ramm, 42 The White Knigth 04 (448|894) , K84 [coord]448|894[/coord] --> Ankunftszeit: 25.08.11 14:46:51:846 [player]Frank R.[/player]
     *
     * ================================================ Dorf: Just4Testing (454|943) K55 Wallstufe: 20 Verteidiger: 18314 13982 0 18659 353
     * 0 0 4825 0 17 0 0
     *
     * LKAV?, 20 Checkpoint (465|883) , K84 Just4Testing (465|883) K55 --> Ankunftszeit: 24.08.11 23:38:53:674 Frank R. Ramm, 31 dieBrüder
     * 02 (460|888) , K84 Just4Testing (460|888) K55 --> Ankunftszeit: 25.08.11 17:14:33:064 Frank R.
     *
     * Dorf: Just4Testing (453|943) K55 Wallstufe: 20 Verteidiger: 14998 13360 5929 14998 100 2809 260 3000 300 40 0 0
     *
     * Ramm, 22 Checkpoint (452|898) , K84 Just4Testing (452|898) K55 --> Ankunftszeit: 25.08.11 12:01:23:952 Torridity
     *
     * Dorf: Just4Testing (452|944) K55 Wallstufe: 20 Verteidiger: 18581 13354 0 18581 410 0 0 4994 0 19 0 0
     *
     * Ramm, 42 The White Knigth 04 (448|894) , K84 Just4Testing (448|894) K55 --> Ankunftszeit: 25.08.11 14:46:51:846 Torridity
     *
     */
    private HashMap<Tribe, SOSRequest> parseRequestsShort(String pData) {
        String[] lines = pData.split("\n");
        HashMap<Tribe, SOSRequest> requests = new HashMap<>();
        Village destination = null;
        SOSRequest request = null;
        SimpleDateFormat dateFormat = null;

        boolean useMillis = ServerSettings.getSingleton().isMillisArrival();
        if (!useMillis) {
            dateFormat = new SimpleDateFormat(getVariable("sos.date.format"), ParserVariableManager.getSingleton().getDateLocale());
        } else {
            dateFormat = new SimpleDateFormat(getVariable("sos.date.format.ms"), ParserVariableManager.getSingleton().getDateLocale());
        }

        for (String line : lines) {
           // System.out.println("L " + line);
            String usedLine = line.trim();
            if (usedLine.contains(getVariable("sos.short.village"))) {
                logger.debug("Village line '{}'", usedLine);
                if (request != null && destination != null) {
                    logger.debug("Store last request");
                    requests.put(destination.getTribe(), request);
                }

                destination = VillageParser.parseSingleLine(usedLine);
                //check if there is a village in the line
                if (destination != null) {
                    logger.debug("Destination: {}", destination);
                    //check for existing request
                    request = requests.get(destination.getTribe());

                    if (request == null) {
                        logger.debug("New request");
                        //create new request
                        request = new SOSRequest(destination.getTribe());
                        requests.put(destination.getTribe(), request);
                    }
                    logger.debug("Adding target {}", destination);
                    request.addTarget(destination);
                }
            }


            if (destination != null) {
                logger.debug("Check destination in '{}'", usedLine);
                if (usedLine.contains(getVariable("sos.short.wall.level"))) {
                    logger.debug("Check wall in line '{}'", usedLine);
                    String wallSplit[] = usedLine.split(" ");
                    if (wallSplit != null && wallSplit.length >= 2) {
                        logger.debug("Check for valid wall");
                        try {
                            Integer wall = Integer.parseInt(wallSplit[wallSplit.length - 1]);
                            logger.debug("Wall: {}", wall);
                            request.getTargetInformation(destination).setWallLevel(wall);
                        } catch (Exception e) {
                            logger.debug("Failed to get Wall", e);
                        }
                    } else {
                        logger.debug("Invalid wall entry '{}'", Arrays.toString(wallSplit));
                    }
                } else if (usedLine.contains(getVariable("sos.short.defender"))) {
                    logger.debug("Get units from line '{}'", usedLine);
                    int[] units = parseUnits(usedLine);
                    if (units.length != 0) {
                        logger.debug("Valid units, add to destination");
                        int cnt = 0;
                        for (UnitHolder unit : DataHolder.getSingleton().getUnits()) {
                            request.getTargetInformation(destination).addTroopInformation(unit, units[cnt]);
                            cnt++;
                        }
                    }
                } else if (usedLine.contains("-->")) {
                    //got attack?
                    logger.debug("Check attack in line '{}'", usedLine);
                    String[] attackSplit = usedLine.split(getVariable("sos.short.movement"));
                    if (attackSplit != null && attackSplit.length >= 2) {
                        logger.debug("Try to get attacker in split '{}'", attackSplit[0]);
                        Village source = VillageParser.parseSingleLine(attackSplit[0]);
                        if (source != null) {
                            logger.debug("Got source");
                            Date arrive = null;
                            UnitHolder unit = null;
                            boolean fake = false;
                            unit = guessUnit(attackSplit[0].replaceAll(Pattern.quote(source.getName()), ""));
                            fake = markedAsFake(attackSplit[0].replaceAll(Pattern.quote(source.getName()), ""));

                            try {
                                String timePart = attackSplit[1].replaceAll(getVariable("sos.arrive.time"), "").trim();
                                timePart = timePart.substring(0, timePart.indexOf("[player]") - 1).trim();
                                while(timePart.indexOf("  ") != -1) {
                                    timePart = timePart.replaceAll("  ", " ");
                                }
                                
                                logger.debug("Start to check arrive time '{}'", timePart);
                                logger.debug("Now {}", dateFormat.format(new Date()));
                                
                                arrive = dateFormat.parse(timePart);
                                if (!useMillis) {//add current millis to be able to compare times
                                    arrive = new Date(arrive.getTime() + Calendar.getInstance().get(Calendar.MILLISECOND));
                                }
                            } catch (Exception e) {
                                logger.debug("Failed to parse date", e);
                            }
                            if (arrive != null) {
                                try {
                                    //  System.out.println("T " + dateFormat.format(arrive));
                                    if (unit != null && unit.getPlainName().equals("ag")) {
                                        request.getTargetInformation(destination).addAttack(source, arrive, unit, false, true);
                                    } else if (fake) {
                                        request.getTargetInformation(destination).addAttack(source, arrive, unit, true, false);
                                    } else {
                                        request.getTargetInformation(destination).addAttack(source, arrive, unit);
                                    }
                                } catch (Throwable t) {
                                    logger.info("Failed to add attack with unit. Using old target information format");
                                    //old target information format!?
                                    request.getTargetInformation(destination).addAttack(source, arrive);
                                }
                            }
                        }
                    } else {
                        logger.debug("Invalid split");
                    }
                }
            }
        }

        if (request != null && destination != null) {
            logger.debug("Store last request");
            requests.put(destination.getTribe(), request);
        }
        return requests;
    }

    private UnitHolder guessUnit(String pString) {
        if (pString.toLowerCase().startsWith("ram")) {
            return DataHolder.getSingleton().getUnitByPlainName("ram");
        } else if (pString.toLowerCase().startsWith("ag")) {
            return DataHolder.getSingleton().getUnitByPlainName("ag");
        } else if (pString.toLowerCase().startsWith("kata") || pString.toLowerCase().startsWith("cata")) {
            return DataHolder.getSingleton().getUnitByPlainName("catapult");
        } else if (pString.toLowerCase().startsWith("lkav")) {
            return DataHolder.getSingleton().getUnitByPlainName("light");
        } else if (pString.toLowerCase().startsWith("axt")) {
            return DataHolder.getSingleton().getUnitByPlainName("axe");
        } else if (pString.toLowerCase().startsWith("späh") || pString.toLowerCase().startsWith("spy")) {
            return DataHolder.getSingleton().getUnitByPlainName("spy");
        } else if (pString.toLowerCase().startsWith("axt")) {
            return DataHolder.getSingleton().getUnitByPlainName("axe");
        } else if (pString.toLowerCase().startsWith("lkav")) {
            return DataHolder.getSingleton().getUnitByPlainName("light");
        } else if (pString.toLowerCase().startsWith("speer")) {
            return DataHolder.getSingleton().getUnitByPlainName("spear");
        } else if (pString.toLowerCase().startsWith("schwert")) {
            return DataHolder.getSingleton().getUnitByPlainName("sword");
        } else if (pString.toLowerCase().startsWith("skav")) {
            return DataHolder.getSingleton().getUnitByPlainName("heavy");
        }

        return null;
    }

    private boolean markedAsFake(String pString) {
        return pString.toLowerCase().startsWith("fake");
    }

    private int[] parseUnits(String pLine) {
        String line = pLine.replaceAll(getVariable("troops.own"), "").replaceAll(getVariable("troops.commands"), "").replaceAll(getVariable("troops"), "");
        StringTokenizer t = new StringTokenizer(line, " \t");
        int uCount = DataHolder.getSingleton().getUnits().size();
        int[] units = new int[uCount];
        int cnt = 0;
        while (t.hasMoreTokens()) {
            try {
                units[cnt] = Integer.parseInt(t.nextToken());
                cnt++;
            } catch (Exception e) {
                //token with no troops
            }
        }
        if (cnt < uCount) {
            return new int[]{};
        }
        return units;
    }

    public HashMap<Tribe, SOSRequest> attacksToSOSRequests(List<Attack> pAttacks) {
        HashMap<Tribe, SOSRequest> requests = new HashMap<>();

        for (Attack attack : pAttacks) {
            Tribe defender = attack.getTarget().getTribe();
            SOSRequest requestForDefender = requests.get(defender);
            if (requestForDefender == null) {
                requestForDefender = new SOSRequest(defender);
                requests.put(defender, requestForDefender);
            }
            requestForDefender.addTarget(attack.getTarget());
            TargetInformation targetInfo = requestForDefender.getTargetInformation(attack.getTarget());
            targetInfo.addAttack(attack.getSource(), attack.getArriveTime());
        }
        return requests;
    }

    private String getVariable(String pProperty) {
        return ParserVariableManager.getSingleton().getProperty(pProperty);
    }
    

    public static void main(String[] args) throws Exception {

        try {
            Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            String data = (String) t.getTransferData(DataFlavor.stringFlavor);
            new SOSParser().parse(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
