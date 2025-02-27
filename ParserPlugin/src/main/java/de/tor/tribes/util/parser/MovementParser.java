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

import de.tor.tribes.control.ManageableType;
import de.tor.tribes.io.DataHolder;
import de.tor.tribes.io.TroopAmountFixed;
import de.tor.tribes.io.UnitHolder;
import de.tor.tribes.types.Attack;
import de.tor.tribes.types.StandardAttack;
import de.tor.tribes.util.GlobalOptions;
import de.tor.tribes.util.ServerSettings;
import de.tor.tribes.util.SilentParserInterface;
import de.tor.tribes.util.attack.AttackManager;
import de.tor.tribes.util.attack.StandardAttackManager;
import de.tor.tribes.util.xml.JDomUtils;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author extremeCrazyCoder
 */
public class MovementParser implements SilentParserInterface {
    private static final Logger logger = LogManager.getLogger("MovementParser");
    
    public static final int RETURNING_TYPE = 0;
    public static final int ATTACK_TYPE = 1;
    public static final int SUPPORT_TYPE = 2;
    
    @Override
    public boolean parse(String pData) {
        StringTokenizer lineTok = new StringTokenizer(pData, "\n\r");
        List<Attack> movements = new ArrayList<>();
        Boolean insideOfTable = false;
        int rowsTODO = -1;
        int movementType;
        int currentYear = Calendar.getInstance(ParserVariableManager.getSingleton().getDateLocale()).get(Calendar.YEAR);
        
        while (lineTok.hasMoreElements()) {
            //parse single line for village
            String line = lineTok.nextToken().trim();
            logger.debug("Try line " + line);
            
            if(line.contains(getVariable("movement.tableHeader.command")) &&
                    line.contains(getVariable("movement.tableHeader.srcVillage")) &&
                    line.contains(getVariable("movement.tableHeader.arriveTime"))) {
                insideOfTable = true;
                
                //get the number of Table rows to read
                rowsTODO = Integer.parseInt(line.substring(line.indexOf('(') + 1, line.indexOf(')')));
            }
            else if(insideOfTable) {
                //check if we have a valid movement here
                rowsTODO--;
                
                logger.debug("getting Attack type");
                //determination of movement type
                if(line.startsWith(getVariable("movement.type.returning.1")) ||
                        line.startsWith(getVariable("movement.type.returning.2")) ||
                        line.startsWith(getVariable("movement.type.returning.3")) ||
                        line.startsWith(getVariable("movement.type.abortedMovement"))) {
                    movementType = RETURNING_TYPE;
                } else if (line.startsWith(getVariable("movement.type.attack"))) {
                    movementType = ATTACK_TYPE;
                } else if (line.startsWith(getVariable("movement.type.support"))) {
                    movementType = SUPPORT_TYPE;
                } else {
                    //this is ether not a movement
                    //or a renamend movement (wich makes it impossible for us to read
                    //so just carry on with the next
                    
                    if(rowsTODO < 0) {
                        //we do not have to find more
                        insideOfTable = false;
                    }
                    continue;
                }
                if(rowsTODO < 0) {
                    //we have found to much movements...
                    //just log and ignore
                    logger.warn("To much movements found");
                }
                
                
                logger.debug("getting source an target village");
                //decode movement
                String[] parts = line.split("\t");
                Attack parsed = new Attack();
                
                //get source and target village
                switch(movementType) {
                    case ATTACK_TYPE:
                    case SUPPORT_TYPE:
                        parsed.setSource(VillageParser.parseSingleLine(parts[1]));
                        parsed.setTarget(VillageParser.parseSingleLine((parts[0])));
                        break;
                    case RETURNING_TYPE:
                        parsed.setSource(VillageParser.parseSingleLine(parts[0]));
                        parsed.setTarget(VillageParser.parseSingleLine(parts[1]));
                        break;
                    default:
                        logger.fatal("Run into impossible else path");
                        return false;
                }
                
                //get arrive time
                logger.debug("getting arrive time");
                //replace date names that simple Date Format can't handle
                if(parts[2].startsWith(getVariable("movement.date.today"))) {
                    parts[2] = parts[2].substring(parts[2].indexOf(' '));
                    parts[2] = new SimpleDateFormat(getVariable("movement.date.format.ouput"), ParserVariableManager.getSingleton().getDateLocale()).format(new Date()) + parts[2];
                } else if(parts[2].startsWith(getVariable("movement.date.tomorrow"))) {
                    parts[2] = parts[2].substring(parts[2].indexOf(' '));
                    parts[2] = new SimpleDateFormat(getVariable("movement.date.format.ouput"), ParserVariableManager.getSingleton().getDateLocale()).format(new Date(new Date().getTime() + (1000 * 60 * 60 * 24))) + parts[2];
                }

                boolean useMillis = ServerSettings.getSingleton().isMillisArrival();
                String movementFormat;
                if (!useMillis) {
                    movementFormat = getVariable("movement.date.format");
                } else {
                    movementFormat = getVariable("movement.date.format.ms");
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat(movementFormat, ParserVariableManager.getSingleton().getDateLocale());
                boolean containsYear = movementFormat.contains("y") || movementFormat.contains("Y");

                try {
                    Date arrive = dateFormat.parse(parts[2]);
                    if(!containsYear) {
                        Calendar c = Calendar.getInstance(ParserVariableManager.getSingleton().getDateLocale());
                        c.setTime(arrive);
                        c.set(Calendar.YEAR, currentYear);
                        arrive = c.getTime();
                    }
                    parsed.setArriveTime(arrive);
                }catch(Exception e) {
                    logger.warn("Exception during parsing the Date '" + parts[2] + "'", e);
                    continue;
                }

                logger.debug("getting units");                
                //get units
                List<UnitHolder> allUnits = DataHolder.getSingleton().getSendableUnits();
                TroopAmountFixed units = new TroopAmountFixed(0);
                int cnt = 0;
                for(int i = 3; i < parts.length; i++) {
                    try {
                        units.setAmountForUnit(allUnits.get(cnt), Integer.parseInt(parts[i]));
                        cnt++;
                    } catch (Exception e) {
                        //cell with no troops
                    }
                }
                if (cnt < allUnits.size()) {
                    continue;
                }
                parsed.setTroops(units.transformToDynamic());
                
                logger.debug("getting standard attack");
                //set a default attack
                switch(movementType) {
                    case ATTACK_TYPE:
                        parsed.setType(StandardAttackManager.getSingleton()
                                .getElementByName(StandardAttackManager.OFF_TYPE_NAME).getIcon());
                        break;
                    case RETURNING_TYPE:
                        //TODO change this (workaround for now)
                        parsed.setType(StandardAttackManager.getSingleton()
                                .getElementByName(StandardAttackManager.SUPPORT_TYPE_NAME).getIcon());
                        break;
                    case SUPPORT_TYPE:
                        parsed.setType(StandardAttackManager.getSingleton()
                                .getElementByName(StandardAttackManager.SUPPORT_TYPE_NAME).getIcon());
                        break;
                    default:
                        logger.fatal("Run into impossible else path");
                        return false;
                }
                
                //figure out, if the found troops equal any standard attack
                for (ManageableType t : StandardAttackManager.getSingleton().getAllElements()) {
                    StandardAttack a = (StandardAttack) t;
                    if(a.equals(units)) {
                        parsed.setType(a.getIcon());
                    }
                }
                
                logger.debug("getting slowest unit");
                //find out wich unit is the slowest
                parsed.setUnit(units.getSlowestUnit());
                parsed.setTransferredToBrowser(true);
                
                //if the program runns till here the movement semms to be valid
                StringBuilder attStr = new StringBuilder();
                attStr.append("adding movement type:"); attStr.append(movementType);
                attStr.append("\ns:"); attStr.append(parsed.getSource());
                attStr.append("\nd:"); attStr.append(parsed.getTarget());
                attStr.append("\ntime:");
                attStr.append(dateFormat.format(parsed.getArriveTime()));
                attStr.append("\nunits: ");
                attStr.append(JDomUtils.toShortString(units.toXml("troops")));
                attStr.append(" \ns:"); attStr.append(parsed.getType());
                attStr.append("\nu:"); attStr.append(parsed.getUnit().getPlainName());
                logger.info(attStr.toString());
                
                movements.add(parsed);
            }
        }
        
        if(movements.size() > 0) {
            String plan = GlobalOptions.getProperty("parser.movement.plan");
            
            if(GlobalOptions.getProperties().getBoolean("parser.movement.delete.all.on.import")) {
                if(AttackManager.getSingleton().groupExists(plan))
                    AttackManager.getSingleton().removeAllElementsFromGroup(plan);
                else
                    AttackManager.getSingleton().addGroup(plan);
            } else {
                if(!AttackManager.getSingleton().groupExists(plan))
                    AttackManager.getSingleton().addGroup(plan);
            }
            
            
            AttackManager.getSingleton().invalidate();
            for (Attack a : movements) {
                if(!AttackManager.getSingleton().getAllElements(plan).contains(a))
                    AttackManager.getSingleton().addManagedElement(plan, a);
            }
            AttackManager.getSingleton().revalidate(plan, true);
            return true;
        }
        
        return false;
    }

    String getVariable(String pProperty) {
        return ParserVariableManager.getSingleton().getProperty(pProperty);
    }
    
    public static void main(String[] args) throws Exception {
        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        String data = (String) t.getTransferData(DataFlavor.stringFlavor);
        new MovementParser().parse(data);
    }
}
