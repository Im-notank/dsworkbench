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
package de.tor.tribes.util.bb;

import de.tor.tribes.util.troops.VillageTroopsHolder;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Torridity
 */
public class TroopListFormatter extends BasicFormatter<VillageTroopsHolder> {

    private static final String[] VARIABLES = new String[] {LIST_START, LIST_END, ELEMENT_COUNT, ELEMENT_ID};
    private static final String TEMPLATE_PROPERTY = "troops.list.bbexport.template";

    @Override
    public String formatElements(List<VillageTroopsHolder> pElements, boolean pExtended) {
        StringBuilder b = new StringBuilder();
        int cnt = 1;
        NumberFormat f = getNumberFormatter(pElements.size());
        String beforeList = getHeader();
        String listItemTemplate = getLineTemplate();
        String afterList = getFooter();
        String replacedStart = StringUtils.replaceEach(beforeList, new String[] {ELEMENT_COUNT}, new String[] {f.format(pElements.size())});

        VillageTroopsHolder dummyHolder = new VillageTroopsHolder();
        //replace unit icons
        replacedStart = StringUtils.replaceEach(replacedStart, dummyHolder.getBBVariables(), dummyHolder.getReplacements(pExtended));
        b.append(replacedStart).append("\n");
        formatElementsCore(b, pElements, pExtended, listItemTemplate, f);
        String replacedEnd = StringUtils.replaceEach(afterList, new String[] {ELEMENT_COUNT}, new String[] {f.format(pElements.size())});
        b.append(replacedEnd);
        return b.toString();
    }

    @Override
    public String getPropertyKey() {
        return TEMPLATE_PROPERTY;
    }

    @Override
    public String getStandardTemplate() {
        return VillageTroopsHolder.getStandardTemplate();
    }

    @Override
    public String[] getTemplateVariables() {
        List<String> vars = new LinkedList<>();
        Collections.addAll(vars, VARIABLES);
        Collections.addAll(vars, new VillageTroopsHolder().getBBVariables());
        return vars.toArray(new String[vars.size()]);
    }
    
    @Override
    public Class<VillageTroopsHolder> getConvertableType() {
        return VillageTroopsHolder.class;
    }
}
