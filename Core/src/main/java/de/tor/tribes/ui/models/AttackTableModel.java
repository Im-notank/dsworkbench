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
package de.tor.tribes.ui.models;

import de.tor.tribes.io.DataHolder;
import de.tor.tribes.io.DataHolderListener;
import de.tor.tribes.io.TroopAmountElement;
import de.tor.tribes.io.UnitHolder;
import de.tor.tribes.types.Attack;
import de.tor.tribes.types.ext.Ally;
import de.tor.tribes.types.ext.BarbarianAlly;
import de.tor.tribes.types.ext.Barbarians;
import de.tor.tribes.types.ext.NoAlly;
import de.tor.tribes.types.ext.Tribe;
import de.tor.tribes.types.ext.Village;
import de.tor.tribes.util.attack.AttackManager;
import de.tor.tribes.util.translation.TranslationManager;
import de.tor.tribes.util.translation.Translator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 *
 * @author Torridity
 */
public class AttackTableModel extends AbstractTableModel {
    private static Logger logger = LogManager.getLogger("AttackTableModel");
    private Translator trans = TranslationManager.getTranslator("ui.models.AttackTableModel");

    private String sPlan = null;
    private final List<String> columnNames = new LinkedList<>();
    private final List<Class> columnTypes = new LinkedList<>();
    private final List<Boolean> editable = new LinkedList<>();
    private int unitAfter;
    private List<UnitHolder> units;

    public AttackTableModel(String pPlan) {
        sPlan = pPlan;
        generateColumns();
        
        DataHolder.getSingleton().addDataHolderListener(new DataHolderListener() {
            @Override
            public void fireDataHolderEvent(String arg0) {
            }

            @Override
            public void fireDataLoadedEvent(boolean arg0) {
                generateColumns();
            }
        });
    }
    
    private void generateColumns() {
        columnNames.clear();
        columnTypes.clear();
        editable.clear();
        
        columnNames.add(trans.get("attacker")); columnTypes.add(Tribe.class); editable.add(false);
        columnNames.add(trans.get("attacker_tribe")); columnTypes.add(Ally.class); editable.add(false);
        columnNames.add(trans.get("source")); columnTypes.add(Village.class); editable.add(true);
        columnNames.add(trans.get("defender")); columnTypes.add(Tribe.class); editable.add(false);
        columnNames.add(trans.get("defender_tribe")); columnTypes.add(Ally.class); editable.add(false);
        columnNames.add(trans.get("target")); columnTypes.add(Village.class); editable.add(true);
        columnNames.add(trans.get("unit")); columnTypes.add(UnitHolder.class); editable.add(true);
        columnNames.add(trans.get("type")); columnTypes.add(Integer.class); editable.add(true);
        units = DataHolder.getSingleton().getSendableUnits();
        for (UnitHolder unit : units) {
            columnNames.add(unit.getName());
            columnTypes.add(TroopAmountElement.class);
            editable.add(true);
        }
        unitAfter = columnNames.size();
        columnNames.add(trans.get("send_time")); columnTypes.add(Date.class); editable.add(true);
        columnNames.add(trans.get("arrive_time")); columnTypes.add(Date.class); editable.add(true);
        columnNames.add(trans.get("runtime")); columnTypes.add(Long.class); editable.add(false);
        columnNames.add(trans.get("remaining")); columnTypes.add(Long.class); editable.add(false);
        columnNames.add(trans.get("show_on_map")); columnTypes.add(Boolean.class); editable.add(true);
        columnNames.add(trans.get("transfer")); columnTypes.add(Boolean.class); editable.add(true);
        columnNames.add(trans.get("times")); columnTypes.add(Short.class); editable.add(true);
        
        fireTableStructureChanged();
    }

    public void setPlan(String pPlan) {
        sPlan = pPlan;
        fireTableDataChanged();
    }

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnTypes.get(columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if(columnNames.get(columnIndex).equals(trans.get("unit"))) {
            Attack a = (Attack) AttackManager.getSingleton().getAllElements(sPlan).get(rowIndex);
            
            UnitHolder slowest = a.getTroops().getSlowestUnit();
            if(slowest != null) {
                return false;
            }
        }
        return editable.get(columnIndex);
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames.get(columnIndex);
    }

    @Override
    public int getRowCount() {
        if (sPlan == null) {
            return 0;
        }
        return AttackManager.getSingleton().getAllElements(sPlan).size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (sPlan == null) {
            return null;
        }
        try {
            Attack a = (Attack) AttackManager.getSingleton().getAllElements(sPlan).get(rowIndex);
            if(columnIndex == 0) {
                Tribe attacker = a.getSource().getTribe();
                if (attacker == null) {
                    return Barbarians.getSingleton();
                }
                return attacker;
            } else if(columnIndex == 1) {
                Tribe attacker = a.getSource().getTribe();
                if (attacker == null) {
                    return BarbarianAlly.getSingleton();
                }
                Ally ally = attacker.getAlly();
                if (ally == null) {
                    return NoAlly.getSingleton();
                }
                return ally;
            } else if(columnIndex == 2) {
                return a.getSource();
            } else if(columnIndex == 3) {
                Tribe defender = a.getTarget().getTribe();
                if (defender == null) {
                    return Barbarians.getSingleton();
                }
                return defender;
            } else if(columnIndex == 4) {
                Tribe defender = a.getTarget().getTribe();
                if (defender == null) {
                    return Barbarians.getSingleton();
                }
                Ally ally = defender.getAlly();
                if (ally == null) {
                    return NoAlly.getSingleton();
                }
                return ally;
            } else if(columnIndex == 5) {
                return a.getTarget();
            } else if(columnIndex == 6) {
                return a.getRealUnit();
            } else if(columnIndex == 7) {
                return a.getType();
            } else if(columnIndex > 7 && columnIndex < unitAfter) {
                return a.getTroops().getElementForUnit(units.get(columnIndex - 8));
            } else if(columnIndex == unitAfter) {
                return a.getSendTime();
            } else if(columnIndex == unitAfter + 1) {
                return a.getArriveTime();
            } else if(columnIndex == unitAfter + 2) {
                return a.getRuntime();
            } else if(columnIndex == unitAfter + 3) {
                long sendTime = a.getSendTime().getTime();
                long t = sendTime - System.currentTimeMillis();
                return (t <= 0) ? 0 : t;
            } else if(columnIndex == unitAfter + 4) {
                return a.isShowOnMap();
            } else if(columnIndex == unitAfter + 5) {
                return a.isTransferredToBrowser();
            } else if(columnIndex == unitAfter + 6) {
                return a.getMultiplier();
            }
        } catch (Exception ignored) {};
        return null;
    }

    @Override
    public void setValueAt(Object pValue, int pRow, int pCol) {
        if (sPlan == null) {
            return;
        }
        try {
            Attack a = (Attack) AttackManager.getSingleton().getAllElements(sPlan).get(pRow);
            if(pCol == 2) {
                if (pValue != null) {
                    a.setSource((Village) pValue);
                }
            } else if(pCol == 5) {
                if (pValue != null) {
                    a.setTarget((Village) pValue);
                }
            } else if(pCol == 6) {
                if (pValue != null) {
                    a.setUnit((UnitHolder) pValue);
                }
            } else if(pCol == 7) {
                if (pValue == null) {
                    a.setType(Attack.NO_TYPE);
                } else {
                    a.setType((Integer) pValue);
                }
            } else if(pCol > 7 && pCol < unitAfter) {
                if (pValue == null) {
                    a.getTroops().setAmount(new TroopAmountElement(units.get(pCol - 8), "0"));
                } else {
                    a.getTroops().setAmount(new TroopAmountElement(units.get(pCol - 8), (String) pValue));
                }
            } else if(pCol == unitAfter) {
                if (pValue == null) {
                    a.setArriveTime(null);
                } else {
                    a.setSendTime((Date) pValue);
                }
            } else if(pCol == unitAfter + 1) {
                if (pValue == null) {
                    a.setArriveTime(null);
                } else {
                    a.setArriveTime((Date) pValue);
                }
            } else if(pCol == unitAfter + 4) {
                a.setShowOnMap((Boolean) pValue);
            } else if(pCol == unitAfter + 5) {
                a.setTransferredToBrowser((Boolean) pValue);
            } else if(pCol == unitAfter + 6) {
                a.setMultiplier((Short) pValue);
            }
        } catch (Exception ignored) {
        }
        AttackManager.getSingleton().revalidate(sPlan, true);
    }
}
