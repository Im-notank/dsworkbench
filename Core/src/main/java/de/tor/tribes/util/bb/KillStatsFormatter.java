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

import de.tor.tribes.types.TribeStatsElement.Stats;
import de.tor.tribes.util.translation.TranslationManager;
import de.tor.tribes.util.translation.Translator;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Torridity
 */
public class KillStatsFormatter extends BasicFormatter<Stats> {
    private static Translator trans = TranslationManager.getTranslator("types.KillStatsFormatter");

    private static final String[] VARIABLES = new String[] {LIST_START, LIST_END, ELEMENT_COUNT, ELEMENT_ID};
    private static final String TEMPLATE_PROPERTY = "kills.stats.bbexport.template";
    private final String[] STAT_SPECIFIC_VARIABLES = new String[] {
        "%PLAYER%", "%PLAYER_NO_BB%",
        "%KILLS_START%", "%KILLS_END%", "%KILLS_DIFFERENCE%", "%PERCENT_DIFFERENCE%"
    };

    @Override
    public String formatElements(List<Stats> pElements, boolean pShowAll) {
        StringBuilder b = new StringBuilder();
        int cnt = 1;
        NumberFormat f = getNumberFormatter(pElements.size());
        String beforeList = getHeader();
        String listItemTemplate = getLineTemplate();
        String afterList = getFooter();
        String replacedStart = StringUtils.replaceEach(beforeList, new String[] {ELEMENT_COUNT}, new String[] {f.format(pElements.size())});
        b.append(replacedStart);
        Collections.sort(pElements, Stats.BASH_OFF_COMPARATOR);
        int idx = 0;
        for (Stats s : pElements) {
            String[] replacements = getStatSpecificReplacements(s);
            String itemLine = StringUtils.replaceEach(listItemTemplate, STAT_SPECIFIC_VARIABLES, replacements);
            itemLine = StringUtils.replaceEach(itemLine, new String[] {ELEMENT_ID, ELEMENT_COUNT}, new String[] {f.format(cnt), f.format(pElements.size())});
            b.append(itemLine).append("\n");
            idx++;
            if (idx == 10 && !pShowAll) {
                //show only top10
                break;
            }
            cnt++;
        }
        String replacedEnd = StringUtils.replaceEach(afterList, new String[] {ELEMENT_COUNT}, new String[] {f.format(pElements.size())});
        b.append(replacedEnd);
        return b.toString();
    }

    private String[] getStatSpecificReplacements(Stats pStats) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(0);
        nf.setMaximumFractionDigits(0);
        String tribe = pStats.getParent().getTribe().toBBCode();
        String tribeNoBBVal = pStats.getParent().getTribe().getName();
        String killsBefore = nf.format(pStats.getBashOffStart());
        String killsAfter = nf.format(pStats.getBashOffEnd());
        String killsDiff = nf.format(pStats.getBashOffDiff());

        if (pStats.getBashOffDiff() > 0) {
            killsDiff = "[color=green]" + killsDiff + "[/color]";
        } else {
            killsDiff = "[color=red]" + killsDiff + "[/color]";
        }

        long pBefore = pStats.getBashOffStart();
        double perc = 0;
        if (pBefore > 0) {
            perc = (double) 100 * (double) pStats.getBashOffDiff() / (double) pBefore;
        }

        String percentDiff = ((perc >= 0) ? "+" : "") + nf.format(perc) + "%";

        if (perc > 0) {
            percentDiff = "[color=green]" + percentDiff + "[/color]";
        } else {
            percentDiff = "[color=red]" + percentDiff + "[/color]";
        }

        return new String[] {tribe, tribeNoBBVal, killsBefore, killsAfter, killsDiff, percentDiff};
    }

    @Override
    public String getPropertyKey() {
        return TEMPLATE_PROPERTY;
    }

    @Override
    public String getStandardTemplate() {
        return trans.get("standard_template");
    }

    @Override
    public String[] getTemplateVariables() {
        List<String> vars = new LinkedList<>();
        Collections.addAll(vars, VARIABLES);
        Collections.addAll(vars, STAT_SPECIFIC_VARIABLES);
        return vars.toArray(new String[vars.size()]);
    }
    
    @Override
    public Class<Stats> getConvertableType() {
        return Stats.class;
    }
}
