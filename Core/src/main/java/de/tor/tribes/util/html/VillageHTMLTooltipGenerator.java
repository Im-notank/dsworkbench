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
package de.tor.tribes.util.html;

import de.tor.tribes.io.DataHolder;
import de.tor.tribes.io.UnitHolder;
import de.tor.tribes.types.*;
import de.tor.tribes.types.ext.Ally;
import de.tor.tribes.types.ext.Barbarians;
import de.tor.tribes.types.ext.Tribe;
import de.tor.tribes.types.ext.Village;
import de.tor.tribes.ui.ImageManager;
import de.tor.tribes.ui.panels.MapPanel;
import de.tor.tribes.ui.windows.DSWorkbenchMainFrame;
import de.tor.tribes.util.BBCodeFormatter;
import de.tor.tribes.util.DSCalculator;
import de.tor.tribes.util.GlobalOptions;
import de.tor.tribes.util.ServerSettings;
import de.tor.tribes.util.TimeManager;
import de.tor.tribes.util.translation.TranslationManager;
import de.tor.tribes.util.translation.Translator;
import de.tor.tribes.util.conquer.ConquerManager;
import de.tor.tribes.util.farm.FarmManager;
import de.tor.tribes.util.mark.MarkerManager;
import de.tor.tribes.util.note.NoteManager;
import de.tor.tribes.util.report.ReportManager;
import de.tor.tribes.util.tag.TagManager;
import de.tor.tribes.util.troops.TroopsManager;
import de.tor.tribes.util.troops.VillageTroopsHolder;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 *
 * @author Torridity
 */
public class VillageHTMLTooltipGenerator {
    private static Translator tans = TranslationManager.getTranslator("de.tor.tribes.util.html.VillageHTMLTooltipGenerator");
    
    public static String buildToolTip(Village pVillage) {
        return buildToolTip(pVillage, true);
    }
    
    public static String buildToolTip(Village pVillage, boolean pWithUnits) {
        boolean showMoral = GlobalOptions.getProperties().getBoolean("show.popup.moral") &&
                ServerSettings.getSingleton().getMoralType() != ServerSettings.NO_MORAL;
        boolean showRanks = GlobalOptions.getProperties().getBoolean("show.popup.ranks");
        boolean showConquers = GlobalOptions.getProperties().getBoolean("show.popup.conquers");
        boolean showFarmSpace = GlobalOptions.getProperties().getBoolean("show.popup.farm.space");
        StringBuilder b = new StringBuilder();
        b.append("<html><head>").append(BBCodeFormatter.getStyles()).append("</head><table width=\"400\" style=\"border: solid 1px black; cellspacing:0px;cellpadding: 0px;background-color:#EFEBDF;color:black;\">\n");
        b.append(buildVillageRow(pVillage));
        NumberFormat nf = NumberFormat.getInstance();
        SimpleDateFormat df = TimeManager.getSimpleDateFormat("dd.MM.yy HH:mm:ss");
        nf.setMinimumFractionDigits(0);
        nf.setMaximumFractionDigits(0);
        b.append(buildInfoRow(tans.get("points") + ":", nf.format(pVillage.getPoints()), false));
        if (pVillage.getTribe() != Barbarians.getSingleton()) {
            b.append(buildInfoRow(tans.get("owner") + ":", pVillage.getTribe(), showRanks));
            if (showConquers) {
                b.append(buildSubInfoRow(tans.get("killedOff") + ":", nf.format(pVillage.getTribe().getKillsAtt()) + " (" + nf.format(pVillage.getTribe().getRankAtt()) + ". " + tans.get("rank") + ")"));
                b.append(buildSubInfoRow(tans.get("killedDef") + ":", nf.format(pVillage.getTribe().getKillsDef()) + " (" + nf.format(pVillage.getTribe().getRankDef()) + ". " + tans.get("rank") + ")"));
            }
            
            if (pVillage.getTribe().getAlly() != null) {
                b.append(buildInfoRow(tans.get("ally") + ":", pVillage.getTribe().getAlly(), showRanks));
            }
            if (showMoral) {
                Tribe current = GlobalOptions.getSelectedProfile().getTribe();
                if (current != null) {
                    if (current.getId() != pVillage.getTribe().getId()) {
                        b.append(buildInfoRow(tans.get("moral") + ":", DSCalculator
                                .calculateMorale(current, pVillage.getTribe()) , false));
                    }
                }
            }
        } else {
            if (showMoral) {
                b.append(buildInfoRow(tans.get("moral") + ":", "100%", false));
            }
        }
        
        
        if (pVillage.getType() != 0) {
            b.append(buildInfoRow(tans.get("bonus") + ":", Village.getBonusDescription(pVillage), false));
        }
        
        List<Tag> tags = TagManager.getSingleton().getTags(pVillage);
        if (tags != null && !tags.isEmpty()) {
            String tagString = "";
            for (Tag t : tags) {
                tagString += t.getName() + ";";
            }
            tagString = tagString.substring(0, tagString.lastIndexOf(";"));
            b.append(buildInfoRow(tans.get("tags") + ":", tagString, false));
        }
        
        FightReport r = ReportManager.getSingleton().findLastReportForVillage(pVillage);
        if (r != null) {
            String imgString = "<img src=\"";
            if (r.areAttackersHidden()) {
                imgString += VillageHTMLTooltipGenerator.class.getResource("/res/ui/bullet_ball_grey.png");
            } else if (r.isSpyReport()) {
                imgString += VillageHTMLTooltipGenerator.class.getResource("/res/ui/bullet_ball_blue.png");
            } else if (r.wasLostEverything()) {
                imgString += VillageHTMLTooltipGenerator.class.getResource("/res/ui/bullet_ball_red.png");
            } else if (r.wasLostNothing()) {
                imgString += VillageHTMLTooltipGenerator.class.getResource("/res/ui/bullet_ball_green.png");
            } else {
                imgString += VillageHTMLTooltipGenerator.class.getResource("/res/ui/bullet_ball_yellow.png");
            }
            imgString += "\"/>";
            b.append(buildInfoRow(tans.get("lastReport") + ":", imgString + " " + df.format(r.getTimestamp()), false));
        }
        
        FarmInformation fi = FarmManager.getSingleton().getFarmInformation(pVillage);
        if (fi != null) {
            b.append(buildInfoRow(tans.get("lastFarm") + ":", (fi.getLastReport() > 0) ? df.format(fi.getLastReport()) : tans.get("noInformation"), false));
            b.append(buildInfoRow(tans.get("resInStorage") + ":",
                    nf.format(fi.getWoodInStorage()) + "&nbsp;<img src=\"" + VillageHTMLTooltipGenerator.class.getResource("/res/holz.png") + "\"/>&nbsp;"
                    + nf.format(fi.getClayInStorage()) + "&nbsp;<img src=\"" + VillageHTMLTooltipGenerator.class.getResource("/res/lehm.png") + "\"/>&nbsp;"
                    + nf.format(fi.getIronInStorage()) + "&nbsp;<img src=\"" + VillageHTMLTooltipGenerator.class.getResource("/res/eisen.png") + "\"/>", false));
        }
        
        if (showFarmSpace) {
            b.append(buildFarmLevel(pVillage));
        }
        Conquer c = ConquerManager.getSingleton().getConquer(pVillage);
        if (c != null) {
            SimpleDateFormat f = TimeManager.getSimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            b.append(buildInfoRow(tans.get("conquerAt") + ":", f.format(c.getTimestamp() * 1000L), false));
            b.append(buildInfoRow(tans.get("acceptance") + ":", c.getCurrentAcceptance(), false));
        }
        
        b.append(buildNotes(pVillage));
        
        if (pWithUnits) {
            b.append(buildUnitTableRow(pVillage));
        }
        b.append("</table>\n").append("<html>\n");
        return b.toString();
    }
    
    static String buildVillageRow(Village pVillage) {
        String res = "<tr>\n";
        res += "<td colspan='3' bgcolor='#E1D5BE'><strong>" + pVillage.getFullName() + "</strong></td>\n";
        res += "</tr>\n";
        return res;
    }
    
    static String buildInfoRow(String pField, Object pValue, boolean pExtended) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(0);
        nf.setMaximumFractionDigits(0);
        StringBuilder b = new StringBuilder();
        b.append("<tr>\n");
        b.append("<td width='150'><strong>").append(pField).append("</strong></td>\n");
        if (pValue instanceof Tribe) {
            Tribe t = (Tribe) pValue;
            Marker m = MarkerManager.getSingleton().getMarker(t);
            if (m != null) {
                String rgb = Integer.toHexString(m.getMarkerColor().getRGB());
                if (pExtended) {
                    String tribeText = t.getName();
                    tribeText += " <i>(" + nf.format(t.getPoints()) + " " + tans.get("points") + ", " + nf.format(t.getRank()) + ". " + tans.get("rank") + ")</i>";
                    b.append("<td width='300'>").append(tribeText).append("</td>\n");
                } else {
                    b.append("<td width='300'>").append(t).append("</td>\n");
                }
                b.append("<td width='5' bgcolor='#").append(rgb.substring(2)).append("'>&nbsp;</td>\n");
            } else {
                if (pExtended) {
                    String tribeText = t.getName();
                    tribeText += " <i>(" + nf.format(t.getPoints()) + " " + tans.get("points") + ", " + nf.format(t.getRank()) + ". " + tans.get("rank") + ")</i>";
                    b.append("<td width='300'>").append(tribeText).append("</td>\n");
                } else {
                    b.append("<td width='300'>").append(t).append("</td>\n");
                }
            }
        } else if (pValue instanceof Ally) {
            Ally a = (Ally) pValue;
            Marker m = MarkerManager.getSingleton().getMarker(a);
            if (m != null) {
                String rgb = Integer.toHexString(m.getMarkerColor().getRGB());
                if (pExtended) {
                    String allyText = a.getName() + " <i><b>" + a.getTag() + "</b></i>";
                    allyText += " <i>(" + nf.format(a.getPoints()) + " " + tans.get("points") + ", " + nf.format(a.getRank()) + ". " + tans.get("rank") + ")</i>";
                    b.append("<td width='300'>").append(allyText).append("</td>\n");
                } else {
                    b.append("<td width='300'>").append(a.getName()).append(" <i><b>").append(a.getTag()).append("</b></i>" + "</td>\n");
                }
                b.append("<td width='5' bgcolor='#").append(rgb.substring(2)).append("'>&nbsp;</td>\n");
            } else {
                if (pExtended) {
                    String allyText = a.getName() + " <i><b>" + a.getTag() + "</b></i>";
                    allyText += " <i>(" + nf.format(a.getPoints()) + " P" + tans.get("points") + "unkte, " + nf.format(a.getRank()) + ". " + tans.get("rank") + ")</i>";
                    b.append("<td width='300'>").append(allyText).append("</td>\n");
                } else {
                    b.append("<td width='300'>").append(a.getName()).append(" <i><b>").append(a.getTag()).append("</b></i>" + "</td>\n");
                }
            }
        } else {
            b.append("<td colspan='2' width='300'>").append(pValue).append("</td>\n");
        }
        
        b.append("</tr>\n");
        
        return b.toString();
    }
    
    static String buildSubInfoRow(String pField, Object pValue) {
        StringBuilder b = new StringBuilder();
        return b.append("<tr>\n").append("<td width=\"150\" style=\"font-size:8px;\">&nbsp;&nbsp;&nbsp;").append(pField).append("</td>\n").append("<td colspan='2' width=\"300\" >").append(pValue).append("</td>\n").
                append("</tr>\n").toString();
    }
    
    static String buildFarmLevel(Village pVillage) {
        StringBuilder b = new StringBuilder();
        b.append("<tr>\n");
        b.append("<td width=\"150\"><strong>" + tans.get("farm") + ":</strong></td>\n");
        b.append("<td colspan='2' width=\"300\">\n");
        b.append("<table width='100%' style=\"font-size:8px;border: 0px black; padding: 0px;\">\n");
        
        VillageTroopsHolder own = TroopsManager.getSingleton().getTroopsForVillage(pVillage, TroopsManager.TROOP_TYPE.OWN);   
                
        if (own != null) {
            float farmSpace = DSCalculator.getFarmSpaceRatio(pVillage) * 100.f;
            URL red = VillageHTMLTooltipGenerator.class.getResource("/res/balken_pech.png");
            URL green = VillageHTMLTooltipGenerator.class.getResource("/res/balken_glueck.png");
            if (farmSpace == 100) {
                b.append("<tr>\n");
                b.append("<td width='100%' style='background:url(").append(green).append(");background-repeat:repeat-x;background-position:bottom;'>&nbsp;</td>\n");
                b.append("</tr>\n");
            } else {
                b.append("<tr>\n");
                b.append("<td style='background:url(").append(green).append(");background-repeat:repeat-x;background-position:bottom;' width=\"").append(farmSpace).append("%\">&nbsp;</td>\n");
                b.append("<td style='background:url(").append(red).append(");background-repeat:repeat-x;background-position:bottom;' width=\"").append(100 - farmSpace).append("%\">&nbsp;</td>\n");
                b.append("</tr>\n");
            }
        } else {
            b.append("<tr>\n");
            b.append("<td width='100%'>" + tans.get("noInformation") + "</td>\n");
            b.append("</tr>\n");
        }
        
        b.append("</table>\n");
        b.append("</td>\n");
        b.append("</tr>\n");
        return b.toString();
        
    }
    
    static String buildUnitTableRow(Village pVillage) {
        StringBuilder b = new StringBuilder();
        b.append("<tr>\n");
        b.append("<td colspan=\"3\">\n");
        b.append("<table width=\"100%\" style=\"border: solid 1px black; padding: 4px;background-color:#EFEBDF;\">\n");
        b.append("<tr>\n");
        //add unit table
        VillageTroopsHolder inVillage = TroopsManager.getSingleton().getTroopsForVillage(pVillage);
        VillageTroopsHolder outside = TroopsManager.getSingleton().getTroopsForVillage(pVillage, TroopsManager.TROOP_TYPE.OUTWARDS);
        VillageTroopsHolder onTheWay = TroopsManager.getSingleton().getTroopsForVillage(pVillage, TroopsManager.TROOP_TYPE.ON_THE_WAY);
        
        Village current = DSWorkbenchMainFrame.getSingleton().getCurrentUserVillage();
        int cnt = 0;
        for (UnitHolder unit : DataHolder.getSingleton().getUnits()) {
            if (cnt % 2 == 0) {
                b.append("<td style=\"background-color:#FFFFFF;font-size:95%;font-family:Verdana\"><div align=\"center\">");
            } else {
                b.append("<td style=\"background-color:#E1D5BE;font-size:95%;font-family:Verdana\"><div align=\"center\">");
            }
            b.append("<img src=\"").append(VillageHTMLTooltipGenerator.class.getResource("/res/ui/" + unit.getPlainName() + ".png")).append("\"/>");
            b.append("<BR/>\n");
            if (inVillage != null) {
                Integer amount = inVillage.getTroops().getAmountForUnit(unit);
                if (amount == 0) {
                    b.append("<font style=\"color:#DED3B9;\">0</font>\n");
                } else {
                    b.append("<font>").append(amount).append("</font>\n");
                }
                b.append("<BR/>\n");
                amount = (outside == null) ? 0 : outside.getTroops().getAmountForUnit(unit);
                if (amount == 0) {
                    b.append("<font style=\"color:#DED3B9;\">0</font>\n");
                } else {
                    b.append("<font>").append(amount).append("</font>\n");
                }
                b.append("<BR/>\n");
                amount = (onTheWay == null) ? 0 : onTheWay.getTroops().getAmountForUnit(unit);
                if (amount == 0) {
                    b.append("<font style=\"color:#DED3B9;\">0</font>\n");
                } else {
                    b.append("<font>").append(amount).append("</font>\n");
                }
                b.append("<BR/>\n");
            }
            Village toolSource = MapPanel.getSingleton().getToolSourceVillage();
            if (toolSource == null) {
                if (current != null && !current.equals(pVillage)) {
                    double runtime = DSCalculator.calculateMoveTimeInMinutes(current, pVillage, unit.getSpeed());
                    b.append("<i>").append(DSCalculator.formatTimeInMinutes(runtime)).append("</i>");
                }
            } else {
                //tool source village is not null
                double runtime = DSCalculator.calculateMoveTimeInMinutes(toolSource, pVillage, unit.getSpeed());
                b.append("<i>").append(DSCalculator.formatTimeInMinutes(runtime)).append("</i>");
            }
            b.append("</div>");
            b.append("</td>");
            
            cnt++;
        }
        b.append("</tr>\n");
        b.append("</table>\n");
        b.append("</td>\n");
        b.append("</tr>\n");
        return b.toString();
    }
    
    static String buildNotes(Village pVillage) {
        List<Note> notes = NoteManager.getSingleton().getNotesForVillage(pVillage);
        StringBuilder lines = new StringBuilder();
        for (Note n : notes) {
            //Note n = NoteManager.getSingleton().getNoteForVillage(pVillage);
            if (n == null) {
                return "";
            }
            try {
                StringBuilder b = new StringBuilder();
                b.append("<tr>\n");
                String text = n.getNoteText();
                if (text == null) {
                    text = "";
                }
                text = text.replace("\n", "<br/>");
                if (n.getNoteSymbol() == -1) {
                    b.append("<td colspan='3' bgcolor='#F7F5BF'>").append(BBCodeFormatter.toHtml(text)).append("</td>\n");
                } else {
                    b.append("<td bgcolor='#F7F5BF'>" + "<img src=\"").append(ImageManager.getNoteImageURL(n.getNoteSymbol())).append("\"/>" + "</td>\n");
                    b.append("<td colspan='2' bgcolor='#F7F5BF'>").append(BBCodeFormatter.toHtml(text)).append("</td>\n");
                }
                b.append("</tr>\n");
                // return res;
                lines.append(b.toString());
            } catch (Exception e) {
                return "";
            }
        }
        return lines.toString();
    }
}
