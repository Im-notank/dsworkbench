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

import java.awt.Color;

/**
 *
 * @author Torridity
 */
public class Constants {
    public final static String VERSION = "4.3.0";
    public final static String VERSION_ADDITION = "";
    public final static Color DS_BACK = new Color(225, 213, 190);
    public final static Color DS_BACK_LIGHT = new Color(239, 235, 223);
    public final static Color DS_ROW_A = new Color(246, 235, 202);
    public final static Color DS_ROW_B = new Color(251, 244, 221);
    public final static Color DS_DEFAULT_MARKER = new Color(130, 60, 10);
    public final static Color DS_DEFAULT_BACKGROUND = new Color(35, 125, 0);
    public final static Color ENEMY_MARKER = Color.RED;
    public final static Color NAP_MARKER = new Color(127, 0, 127);
    public final static Color ALLY_MARKER = new Color(0, 160, 244);
    public final static String SERVER_DIR = "./servers";
    public final static boolean DEBUG = false;
    
    public final static Color WINNER_GREEN = new Color(96, 238, 90);
    public final static Color LOSER_RED = new Color(255, 99, 71);
    
    static {
        if (DEBUG) {
            System.err.println("DEBUG MODE ENABLED!");
        }
    }
}
