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

import de.tor.tribes.io.UnitHolder;
import de.tor.tribes.types.Attack;
import de.tor.tribes.types.ext.Village;
import de.tor.tribes.util.attack.AttackManager;
import de.tor.tribes.util.translation.TranslationManager;
import de.tor.tribes.util.translation.Translator;
import java.util.Date;
import javax.swing.table.AbstractTableModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Torridity
 */
public class DoItYourselfAttackTableModel extends AbstractTableModel {
    private static Logger logger = LogManager.getLogger("DoItYourselfAttackTable");
    private Translator trans = TranslationManager.getTranslator("ui.models.DoItYourselfAttackTableModel");
    
    protected Class[] types = new Class[]{
        Integer.class, UnitHolder.class, Village.class,
        Village.class, Date.class, Date.class,
        Long.class
    };
    protected String[] colNames = new String[]{
        trans.get("Angriffstyp"), trans.get("Einheit"), trans.get("Herkunft"),
        trans.get("Ziel"), trans.get("Abschickzeit"), trans.get("Ankunftzeit"),
        trans.get("Verbleibend")
    };
    protected boolean[] editableColumns = new boolean[]{true, true, true, true, true, true, false};

    public DoItYourselfAttackTableModel() {
    }

    @Override
    public int getColumnCount() {
        return colNames.length;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return types[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return editableColumns[columnIndex];
    }

    @Override
    public String getColumnName(int column) {
        return colNames[column];
    }

    public void removeRow(int pRow) {
        Attack a = (Attack) AttackManager.getSingleton().getDoItYourselfAttacks().get(pRow);
        AttackManager.getSingleton().removeElement(a);
        fireTableDataChanged();
    }

    public Attack getAttack(int pRow) {
        return (Attack) AttackManager.getSingleton().getDoItYourselfAttacks().get(pRow);
    }

    @Override
    public Object getValueAt(int pRow, int pCol) {
        try {
            Attack a = (Attack) AttackManager.getSingleton().getAllElements(AttackManager.MANUAL_ATTACK_PLAN).get(pRow);

            switch (pCol) {
                case 0:
                    return a.getType();
                case 1:
                    return a.getUnit();
                case 2:
                    return a.getSource();
                case 3:
                    return a.getTarget();
                case 4:
                    return a.getSendTime();
                case 5:
                    return a.getArriveTime();
                default: {
                    long t = a.getSendTime().getTime() - System.currentTimeMillis();
                    return (t <= 0) ? 0 : t;
                }
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int getRowCount() {
        return AttackManager.getSingleton().getAllElements(AttackManager.MANUAL_ATTACK_PLAN).size();
    }

    @Override
    public void setValueAt(Object pValue,
            int pRow,
            int pCol) {
        try {
            Attack a = (Attack) AttackManager.getSingleton().getAllElements(AttackManager.MANUAL_ATTACK_PLAN).get(pRow);
            switch (pCol) {
                case 0: {
                    if (pValue != null) {
                        a.setType((Integer) pValue);
                        a.setTroopsByType();
                    }
                    break;
                }
                case 1: {
                    if (pValue != null) {
                        a.setUnit((UnitHolder) pValue);
                    }
                    break;
                }
                case 2: {
                    if (pValue != null) {
                        a.setSource((Village) pValue);
                    }
                    break;
                }
                case 3: {
                    if (pValue != null) {
                        a.setTarget((Village) pValue);
                    }
                    break;
                }
                case 4: {
                    if (pValue != null) {
                        a.setSendTime((Date) pValue);
                    }
                    break;
                }
                case 5: {
                    if (pValue != null) {
                        a.setArriveTime((Date) pValue);
                    }
                    break;
                }
                default:
                    break;
            }
        } catch (Exception ignored) {
        }
        AttackManager.getSingleton().revalidate(AttackManager.MANUAL_ATTACK_PLAN, true);
    }
}
