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

import de.tor.tribes.types.DefenseInformation;
import de.tor.tribes.types.DefenseInformation.DEFENSE_STATUS;
import de.tor.tribes.types.ext.Village;
import de.tor.tribes.util.translation.TranslationManager;
import de.tor.tribes.util.translation.Translator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Torridity
 */

public class DEPResultTableModel extends AbstractTableModel {
    private Translator trans = TranslationManager.getTranslator("ui.models.DEPResultTableModel");
    
    private String[] columnNames = new String[]{
        trans.get("Ziel"), trans.get("Unterstuetzungen"), trans.get("Status")
    };
    private Class[] types = new Class[]{
        Village.class, String.class, DEFENSE_STATUS.class
    };
    private final List<DefenseInformation> elements = new LinkedList<>();

    public DEPResultTableModel() {
        super();
    }

    public void clear() {
        elements.clear();
        fireTableDataChanged();
    }

    public void addRow(DefenseInformation pDefense) {
        elements.add(pDefense);
    }

    @Override
    public int getRowCount() {
        if (elements == null) {
            return 0;
        }
        return elements.size();
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        return types[columnIndex];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    public void removeRow(int row, int viewRow) {
        elements.remove(row);
        fireTableDataChanged();
    }

    public DefenseInformation getRow(int row) {
        return elements.get(row);
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (elements == null || elements.size() - 1 < row) {
            return null;
        }
        DefenseInformation element = elements.get(row);
        switch (column) {
            case 0:
                return element.getTarget();
            case 1:
                return element.getSupports().length + "/" + element.getNeededSupports();
            default:
                return element.getStatus();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
}
