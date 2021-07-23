/*

The MIT License (MIT)

Copyright (c) 2021 Torridity

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”),
to deal in the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

The Software is provided “as is”, without warranty of any kind, express or implied, including but not limited to the warranties of merchantability,
fitness for a particular purpose and noninfringement. In no event shall the authors or copyright holders be liable for any claim,
damages or other liability, whether in an action of contract, tort or otherwise, arising from,
out of or in connection with the software or the use or other dealings in the Software.

*/
// ==UserScript==
// @name           DS Workbench Scripts (AttackInfo)
// @description    DS Workbench Attack Info Script
// @author         Torridity
// @namespace      https://www.dsworkbench.de/
// @include        https://de*.die-staemme.de/game.php?*screen=overview*
// @include        https://de*.die-staemme.de/game.php?*screen=place*
// @include        https://de*.die-staemme.de/game.php?*screen=info_village*
// @exclude        https://de*.die-staemme.de/game.php?*mode=combined*

// ==/UserScript==

var api = typeof unsafeWindow != 'undefined' ? unsafeWindow.ScriptAPI : window.ScriptAPI;
api.register( '142-Workbench Attackscript', true, 'Torridity', 'support-nur-im-forum@die-staemme.de');

(function(){
/**Settings (set externally)
var showAttacksInVillageInfo = 1;
var showAttacksOnConfirmPage = 1;
var showAttackOnCommandPage = 1;
var showAttacksInOverview = 1;
*/

/**Attacks (set externally)
var attacks = new Array({
'type':'axe.png',
'sourceName':'Source_Village_Plain_Name',
'source':Source_Village_ID,
'xs':Source_Village_X,
'ys':Source_Village_Y,
'target':Target_Village_ID,
'xt':Target_Village_X,
'yt':Target_Village_Y,
'targetName':'Target_Village_Plain_Name',
'unit':'ram.png',
'send':'15.02.11 13:19:24.753',
'arrive':'17.02.11 21:22:45.545',
'expired':1297974165
});
*/

/**Icons for overview tables*/
var icons = {
        outgoing: 'data:image/png;base64,'+
'iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAACXBIWXMAAAsSAAALEgHS3X78AAAA' +
'IGNIUk0AAHolAACAgwAA+f8AAIDpAAB1MAAA6mAAADqYAAAXb5JfxUYAAAKvSURBVHjapJPNb0xh' +
'FMZ/d+6dr5aZVmemRQ1KaUhJJEhZIEQY0QRJE5FYIMHCVuIPEEsLifhM2BBfKY2IEO1CIr6FllCm' +
'UbSd6YzOTDt35s7c972vBaYRdp7lyTm/k5zzPJpSiv+RAaBpWqUQfRKs9Q1qB2cHG9pnBMMLPLqu' +
'WSL/adzK3K6OcPLS4njqd69SCk0pVQEEuo1Y00Tk4pq5i0LRuhADuRzz6+bQGmolUxjn8dfurBlO' +
'7jsVfn3jb0AXsRYVvhlranZjuCioEuMjLbzOPGfzqgaW169mrmsJ9752ykFf386zkd5rSilcAFym' +
'tjrtvrC2Meq2bBspFaZdZmN0A3073nH14Qf6ci94Kx+ydtZW3ZcMnet42zId+AX4woFltdPDlm3j' +
'SIkG1PsDXBk6D8BgxxAX7r8gnu1nsNRL28z1gbHhiUOTAMnWar+H76LAeyvBq+wAz1Nx+u1HrOtZ' +
'8XNHR5LzD57RN/aUkG8aVZ6a9soXvC5jgWkVWD1/C8fmnaEkSyhAKQfLKSKUxNB0hju+03gtzNKN' +
'bXhdVc0VAALypTJ1UwIAeHUvAI5yEMrG0FyVN/t1DzkrTdEWkz4oF0W/ZdLWHb9Fz7dOhCNwpCBV' +
'zrCiJsaZJdcBaL0TZf3CBgpWgUR+tL9yA1Wma8IsMTpkIrPgM30ERRXbZ+2tDK/qWcjKhhrcUudb' +
'dpQP6WTX5BEFpxMpMzWeK5G3yljSxnQJdjUeBCD2ZBmzvQoldRwHbsRf5kq14sQk4DgZ4XV2D43k' +
'7cSISTpTREiHT3Yve95tBmsMoQyE43D3Y1x+1kf3lreRAP60MkfZZBT1i/URf2TqVIO6SBCUwG94' +
'KFg2b0aSY/mAvY9ddP4zCwAcoYYi+/HQ7vbrzYYBRVsOoHGbak5xmPRfYfof/RgATCBPwkTJV/8A' +
'AAAASUVORK5CYII=',
incoming: 'data:image/png;base64,'+
'iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAACXBIWXMAAAsSAAALEgHS3X78AAAA' +
'IGNIUk0AAHolAACAgwAA+f8AAIDpAAB1MAAA6mAAADqYAAAXb5JfxUYAAAJjSURBVHjapJNLSFRh' +
'FMd/d5yHr/ExTmLmA8zKcqKSqJSKqBYZmNGqFoEktNCg2raqaFFt2qgglToRtbTU2hgVKUZuMoZU' +
'0CRJrWYy5/24c++cFhcmwsCFfzibj3N+fP/zUESE9cgMoChK+kFcFODIvoir7hTFxVuw2SAWmWFq' +
'cpDcYLfS6/Wnc0VQRCQNkN00stPlpuHQBvJzwWaFSAwOHgeLGYZf+Bh71aI8+PxyFUC2c5L6Pc+o' +
'qraQaQWrwGIY9jaB/hF2NUD5Dhh/l+RR9xmlb2pIRAwLyY04ouVl7uySUgvxOIgKKwrcfg5z0/Bm' +
'BILfYS4Crv0W6qf7RE3UAL9MAF+ttGVUljlJxCEaBlMm3Oo3jL59CiWloKYg6IeFSTjcWERca083' +
'Uc/NbVZ0HcJBqKmFK71Gsfs6jLqhtBJIgZaCigo4cg42b2sGbiAiTLryArGztSIPL8uautMu8rpD' +
'5FpTWEQwAegpIRGOwabytQefXwi/vaAm/u6BGtdnQ1GtLv/JfbDY4OglI7nvKowPgzkLNA10HewF' +
'kFkPXu8sYPxgeTk6oKsasbCAuxPGegxAyz3YdwKyHFBSBWVbwV6EhEKkPBMDaUBekq7Aj8DKT28Y' +
'URXovgsfHhuQY+eN7gdDRqgayvtRv0mjIw04EMUni5ELWjCizS8EiIdT0HkTPEOQShh+1TiIhix+' +
'0/nkaVU8eIF/V3kim9OU5fQUFtsLreYM7M4sbPk5ZCQFsVqJfF1asc8vtSoz9P/3FgBGwBnMo83p' +
'yGqyWkzVZpMJxcQX1R8aLNLoqvThW3VM69GfAQCvujagQineSAAAAABJRU5ErkJggg==',
expired: 'data:image/png;base64,'+
'R0lGODlhEAAQAMQdAP87AP+VYWMAAP8zAHYAAF4AAHsAAP80AIgAAP8sAP9IAP9CAG0AAFUAAP9T' +
'AP+bYf8fAE4AAHcAAIMAAP8tAP9JAP9XAP8lAP8TAP8UAP8ZAP9PAE0AAP///wAAAAAAACH5BAEA' +
'AB0ALAAAAAAQABAAAAVQYCeOZGmeKGJKJ/JMJBEwpWs5higDh1BOjo3CsOudDJWFIgAY+FDIRfOJ' +
'KiYKqM5uQDlcTxKms0C5QBql7bMA0WQ4JYaT1MBETtTRPcvvl0IAOw=='};

/**IDs to address different table columns and to map between attack-field and table column*/
var TYPE_ID = 0;
var UNIT_ID = 1;
var SOURCE_ID = 2;
var TARGET_ID = 3;
var START_ID = 4;
var COUNTDOWN_ID = 5;
var ARRIVE_ID = 6;
var DIRECTION_ID = 7;
var SOURCE_TARGET_ID = 8;

/*****************************/
/*Custom types and prototypes*/
/*****************************/
String.prototype.trim = function() { return this.replace(/^\s+|\s+$/, ''); };


//'switch' browser
var win = window;
if(typeof unsafeWindow != 'undefined'){
    //firefox environment
    win = unsafeWindow;
}
var $ = typeof unsafeWindow != 'undefined' ? unsafeWindow.$ : window.$;

/**Building additional document data*/
    var modifyDocument = function() {

        //screen processing
        var formNode = document.getElementsByName("units")[0];
        if(isCommandScreen() && showAttackOnCommandPage == 1){
            //place view
            var columns = new Array({
            'id':TYPE_ID,
            'width':30
            },
            {'id':UNIT_ID,
            'width':30
            },
            {
            'id':TARGET_ID,
            'width':250
            },
            {
            'id':START_ID,
            'width':170
            },
            {
            'id':COUNTDOWN_ID,
            'width':100
            },
            {
            'id':ARRIVE_ID,
            'width':170
            });
            var titleNode = document.createElement("h3");
            titleNode.appendChild(document.createTextNode('Geplante Angriffe'));
            formNode.appendChild(titleNode);
            var villageID = getVillageID(formNode);
            var attacksForVillage = getAttacksForVillage(villageID);
            formNode.appendChild(generateTable(columns, villageID, attacksForVillage, true));
        }else{
            formNode = document.getElementsByTagName("form")[0];
            if(formNode != null && isConfirmScreen() && showAttacksOnConfirmPage == 1){
                var titleNode = document.createElement("h3");
                titleNode.appendChild(document.createTextNode('Geplante Angriffe'));
                formNode.appendChild(document.createElement("hr"));
                formNode.appendChild(titleNode);
                var columns = new Array({
                    'id':TYPE_ID,
                    'width':30
                    },
                    {'id':UNIT_ID,
                    'width':30
                    },
                    {
                    'id':TARGET_ID,
                    'width':250
                    },
                    {
                    'id':START_ID,
                    'width':170
                    },
                    {
                    'id':COUNTDOWN_ID,
                    'width':100
                    },
                    {
                    'id':ARRIVE_ID,
                    'width':170
                    });     

                var villageID = getVillageID(formNode);
              var attacksForVillage = getAttacksForVillage(villageID);
              formNode.appendChild(generateTable(columns, villageID, attacksForVillage, true));
            }else if(isVillageInfoScreen() && showAttacksInVillageInfo == 1){
              var mainTable = $x('//table[@class="main"]')[0];
                var infoTable = $x('//table[@class="main"]/tbody/tr/td[@id="content_value"]/table')[0];
                            
                //add attack content
                var Links = mainTable.getElementsByTagName('a');
                var villageID = -1;
                for(var link=0;link<Links.length;link++){
                    var ref = Links[link].getAttribute('href');
                    if(ref.indexOf('target=')>-1){
                        var idStart = ref.indexOf('target=') + 'target='.length;
                        var idEnd = ref.indexOf('&', idStart);
                        if(idEnd == -1){
                            villageID = ref.substring(idStart);
                            break;
                        }else{
                            villageID = ref.substring(idStart, idEnd);
                            break;
                        }
                    }
                }
                        
                var attacksForVillage = getAttacksForVillage(villageID);
                if(attacksForVillage.length == 0){
                    return;
                }
                
                var columns = new Array({
                'id':DIRECTION_ID,
                'width':30
                },
                {'id':TYPE_ID,
                'width':30
                },
                {'id':UNIT_ID,
                'width':30
                },
                {
                'id':SOURCE_TARGET_ID,
                'width':250
                },
                {
                'id':START_ID,
                'width':170
                },
                {
                'id':COUNTDOWN_ID,
                'width':100
                },
                {
                'id':ARRIVE_ID,
                'width':170
                });
                
                //insert empty row at the tables end
                var emptyRow = document.createElement('tr');
                var emptyCell = document.createElement('td');
                emptyCell.setAttribute('colspan', '2');
                emptyRow.appendChild(emptyCell);
                emptyCell.appendChild(document.createElement('hr'));
                infoTable.appendChild(emptyRow);
                //insert title row
                var titleRow = document.createElement('tr');
                var titleCell = document.createElement('td');
                titleCell.setAttribute('colspan', '2');
                titleRow.appendChild(titleCell);
                var titleNode = document.createElement('h3');
                titleNode.appendChild(document.createTextNode('Geplante Angriffe'));
                titleCell.appendChild(titleNode);
                infoTable.appendChild(titleRow);
                //insert attacks table
                var attacksRow = document.createElement('tr');
                var attacksCell = document.createElement('td');
                attacksCell.setAttribute('colspan', '2');
                attacksRow.appendChild(attacksCell);
                attacksCell.appendChild(generateTable(columns, villageID, attacksForVillage, true));
                infoTable.appendChild(attacksRow);
            }else if(showAttacksInOverview == 1){
                //overview
                modifyOverviewTable();
            }
        }
    }

    /**Get all planned attacks (incoming and outgoing) for a village with a given ID
    *Number pVillageID: ID of the village
    *Array return: Array that contains all attacks for this village
    */
    var getAttacksForVillage = function(pVillageID){
        var attacksForVillage = new Array();
        for (var i = 0; i < attacks.length; i++){
            if(attacks[i].source == pVillageID || attacks[i].target == pVillageID){
                attacksForVillage.push(attacks[i]);
            }
        }
        return attacksForVillage;
    }
    
    /**Generate an attack table. This function is used for any table*/  
    var generateTable = function(pColumns, pVillageID, pAttacksInTable, pShowHeader){
      var tBody = document.createElement('tbody');
        //show optionally header
        if(pShowHeader){
            var tHeader = document.createElement('tr')
            for(var column = 0;column<pColumns.length;column++){
                appendTableCell(null, pVillageID, pColumns[column], tHeader, true);
            }
            tBody.appendChild(tHeader);
        }
        
        var tableWidth;
        //build rows
        for(var attack=0;attack<pAttacksInTable.length;attack++){
            var tRow = document.createElement('tr')
            tableWidth = 0;
            for(var column = 0;column<pColumns.length;column++){
                tableWidth += pColumns[column].width;
                appendTableCell(pAttacksInTable[attack], pVillageID, pColumns[column], tRow, false);
            }
            tBody.appendChild(tRow);
        }
        
        var table = document.createElement('table');
        table.setAttribute('class', 'vis');
        table.appendChild(tBody);
        return table;
    }

    /**Append one single table cell (header and data)
    *Struct pAttack: Attack that is shown in the higher-ranking table's row
    *Number pVillageID: ID of the village for which the higher-ranking table is shown 
    *Struct pColumn: Column of this cell in the higher-ranking table
    *HTMLObject pTableRow: Row of the higher-ranking table, to which this cell is added
    *Boolean pIsHeader: Is pTableRow the header row of the table?
    */
    var appendTableCell = function(pAttack, pVillageID, pColumn, pTableRow, pIsHeader){
        var tableCell;
        //create new header or row element and set width and style attributes
        if(pIsHeader){
            tableCell = document.createElement('td');
            tableCell.setAttribute('style', 'font-size:90%;background-color:#DED3B9;white-space: nowrap');
        }else{
            tableCell = document.createElement('td');
            if(pColumn.style){
                tableCell.setAttribute('style', pColumn.style);
            }
        }
    
        tableCell.setAttribute('align', 'center');
    
        //add cell depending on column type ID
        switch(pColumn.id){
            case TYPE_ID:{
                if(pIsHeader){
                    tableCell.innerHTML = '<b>Typ</b>';
                }else{
                    if(pAttack.type != null){
                        var typeIcon = document.createElement("img");
                        typeIcon.setAttribute('src', 'http://www.dsworkbench.de/DSWorkbench/export/' + pAttack.type);
                        typeIcon.setAttribute('alt', '');
                        tableCell.appendChild(typeIcon);
                }else{
                    tableCell.appendChild(document.createTextNode('-'));
                    }
                }
                break;
            }
        case UNIT_ID:{
                if(pIsHeader){
                    tableCell.innerHTML = '<b>Einheit</b>';
                }else{
                    var unitIcon = document.createElement("img");
                    unitIcon.setAttribute('src', 'graphic/unit/unit_' + pAttack.unit);
                    unitIcon.setAttribute('alt', '');
                    tableCell.appendChild(unitIcon);
                }
                break;
            }
        case SOURCE_ID:{
                if(pIsHeader){
                    tableCell.innerHTML = '<b>Herkunft</b>';
                }else{
                    var sourceLink = document.createElement("a");
                    sourceLink.setAttribute('href','/game.php?village=' + pAttack.source + '&screen=info_village&id=' + pAttack.source);
                    sourceLink.appendChild(document.createTextNode(pAttack.sourceName));
                    tableCell.appendChild(sourceLink);
                }
                break;
            }
        case TARGET_ID:{
                if(pIsHeader){
                    tableCell.innerHTML = '<b>Ziel</b>';
                }else{
                    var sourceLink = document.createElement("a");
                    sourceLink.setAttribute('href','/game.php?village=' + pAttack.source + '&screen=info_village&id=' + pAttack.target);
                    sourceLink.appendChild(document.createTextNode(pAttack.targetName));
                    tableCell.appendChild(sourceLink);
                }
                break;
            }
        case START_ID:{
                if(pIsHeader){
                    tableCell.innerHTML = '<b>Startzeit</b>';
                }else{
                    tableCell.innerHTML = pAttack.send;
                }
                break;
            }
        case COUNTDOWN_ID:{
                if(pIsHeader){
                    tableCell.innerHTML = '<b>Start in</b>';
                }else{
                   var countdownSpanNode = document.createElement("span");

                   var expiredNode = document.createElement("td");
                   expiredNode.appendChild(document.createTextNode('Abgelaufen'));
                   expiredNode.setAttribute('class', 'warn');
                   expiredNode.setAttribute('style', 'display:hidden');
                    if(pAttack.expired < getRealServerTime(document.getElementById('serverTime'))){         
                        expiredNode.setAttribute('style', 'display:inline');
                        pTableRow.appendChild(expiredNode);
                        return;
                    }else{
                        expiredNode.setAttribute('style', 'display:none');
                        countdownSpanNode.setAttribute('class', 'timer');
                        countdownSpanNode.appendChild(document.createTextNode(win.getTimeString(getRemainingTime(pAttack.expired))));
                        var title = document.getElementsByTagName("title")[0];
                        if(title.getAttribute('class') != 'timer'){
                            title.setAttribute('class', 'timer');
                            title.appendChild(document.createTextNode(win.getTimeString(getRemainingTime(pAttack.expired))));
                        }
                        tableCell.appendChild(countdownSpanNode);
                        pTableRow.appendChild(tableCell);
                        return;
                    }
                }
                break;
            }
        case ARRIVE_ID:{
                if(pIsHeader){
                    tableCell.innerHTML = '<b>Ankunft</b>';
                }else{
                    tableCell.innerHTML = pAttack.arrive;
                }
                break;
            }
        case DIRECTION_ID:{
                if(pIsHeader){
                    tableCell.innerHTML = '';
                }else{
                    var directionIcon;
                    if(pAttack.source == pVillageID){
                        directionIcon  = document.createElement('img');
                        directionIcon.setAttribute('src', icons.outgoing);
                    }else{
                        directionIcon = document.createElement('img');
                        directionIcon.setAttribute('src', icons.incoming);
                    }
                    tableCell.appendChild(directionIcon);
                }
                break;
            }
        case SOURCE_TARGET_ID:{
                if(pIsHeader){
                    tableCell.innerHTML = '<b>Herkunft/Ziel</b>';
                }else{
                    var sourceTargetText = document.createElement("a");
                    if(pAttack.source == pVillageID){
                         sourceTargetText.setAttribute('href','/game.php?village=' + pAttack.source + '&screen=info_village&id=' + pAttack.target);
                         sourceTargetText.appendChild(document.createTextNode(pAttack.targetName));
                    }else{
                         sourceTargetText.setAttribute('href','/game.php?village=' + pAttack.source + '&screen=info_village&id=' + pAttack.source);
                         sourceTargetText.appendChild(document.createTextNode(pAttack.sourceName));
                    }
                    tableCell.appendChild(sourceTargetText);
                }
                break;
            }
        }
        pTableRow.appendChild(tableCell);
    }

    /**Modify an overview table by adding attack indicators to each village row
    */  
    var modifyOverviewTable = function() {
    
        var doneElems = new Array();
        var allElems = new Array();
        var attackedElems = new Array();
            
        var serverTime = getRealServerTime(document.getElementById('serverTime'));
        //get all elements with attribute 'data-id' ... these should represent villages
        var allElems = getAllElementsWithAttribute('data-id');
        
        for(var i = 0;i<allElems.length;i++){
            //get village id and attacks for it
            var villageId = allElems[i].getAttribute('data-id');
            var attacksForElem = getAttacksForVillage(villageId);   
            
            //add elem to attacked elems
            if(attacksForElem.length > 0 && doneElems.indexOf(allElems[i]) < 0){
                attackedElems.push(allElems[i]);
                doneElems.push(allElems[i]);
                //check if at least one attack is still valid
                var valid = 0;
                for(var j = 0;j < attacksForElem.length;j++){
                    if(attacksForElem[j].expired > serverTime){
                        //attack is valid, green indicator is shown
                        valid++;
                    }
                }
            
                //create new image node
                var node = allElems[i].getElementsByTagName('a')[0].parentNode;
                //create link to place
                var placeLink = document.createElement('a');
                placeLink.setAttribute('href', '/game.php?village=' + attacksForElem[0].source + '&screen=place');
                var img = document.createElement("img");
                img.setAttribute('alt', '');
            
                if(valid > 0){
                    //at least one attack is still valid
                    img.setAttribute('src', 'graphic/dots/green.png');
                    img.setAttribute('title', valid +  ' ausstehende(r) Angriff(e)');
                }else{
                    //all attacks are expired
                    img.setAttribute('src', 'graphic/dots/red.png');
                    img.setAttribute('title', 'Alle Angriffe abgelaufen');
                }
                placeLink.appendChild(img);
                //insert attack indicator before village link
                node.insertBefore(placeLink, node.getElementsByTagName('a')[0]);
            }
            
        }
        

    //add no-attack indicators
    for (var i = 0; i < allElems.length; i++){
      if(attackedElems.indexOf(allElems[i]) < 0){
        var node = allElems[i].getElementsByTagName('a')[0].parentNode;
        var img = document.createElement("img");
        img.setAttribute('src', 'graphic/overview/prod_avail.png?1');
        img.setAttribute('title', 'Keine geplanten Angriffe');
        img.setAttribute('alt', '');
        node.insertBefore(img, node.getElementsByTagName('a')[0]);
    }
       
    }
  };
    /**********HELPER FUNCTIONS***********/
   /**Get all elements with the specified attribute.
   */
   function getAllElementsWithAttribute(attribute) {
     var matchingElements = [];
     var allElements = document.getElementsByTagName('*');
     for (var i = 0, n = allElements.length; i < n; i++) {
        if (allElements[i].getAttribute(attribute)){
          // Element exists with attribute. Add to array.
          matchingElements.push(allElements[i]);
        }
     }
   return matchingElements;
   };
    
    var $x = function(p, context) {
        if(!context){
            context = document;
        }
        var i, arr = [], xpr = document.evaluate(p, context, null, XPathResult.UNORDERED_NODE_SNAPSHOT_TYPE, null);
        for (i = 0; item = xpr.snapshotItem(i); i++)
            arr.push(item);
        return arr;
    };
    
    /**Get the real server time based on the HH:mm:ss live-time and the current timestamp*/
    function getRemainingTime(pSendTime){
        var serverTime = getRealServerTime(document.getElementById('serverTime'));
        return pSendTime - serverTime;
    }
 
 /**Check if we are on the place's command page
    *Boolean return: TRUE if we are on the place's command page
    */
    var isCommandScreen = function(){
        //we are on the command screen if:
        // * 'screen' parameter is 'place'
        // * 'mode' paramter is explicitly 'command' or not set
        // * 'try=confirm' pair is not present
        return (location.href != null && location.href.indexOf('screen=place') > -1 && (location.href.indexOf('mode=command') > -1 || location.href.indexOf('mode=') == -1 && location.href.indexOf('try=confirm') == -1));
    }
        

    /**Check if we are on the simulator page
    *Boolean return: TRUE if we are on the simulator page
    */
    var isSimulatorScreen = function(){
        return (location.href != null && location.href.indexOf('screen=place') > -1 && location.href.indexOf('mode=sim') > -1);
    }
    
    /**Check if we are on the confirm attack page
    *Boolean return: TRUE if we are on the confirm attack page
    */
    var isConfirmScreen = function(){
        return (location.href != null && location.href.indexOf('screen=place') > -1 && location.href.indexOf('try=confirm') > -1);
    }

    /**Check if we are on the village info page
    *Boolean return: TRUE if we are on the confirm attack page
    */
    var isVillageInfoScreen = function(){
        return (location.href != null && location.href.indexOf('screen=info_village') > -1);
    }
    
    /**Get the ID of the current village from a form node (place and attack confirm page)
    **HTMLObject formNode: Form element which contains the villageID
    */
    var getVillageID = function(formNode){
        var villageURL = formNode.getAttribute('action');
        var idStart = villageURL.indexOf('village=') + 'village='.length;
        var idEnd = villageURL.indexOf('&', idStart);
        return villageURL.substring(idStart, idEnd);
    }

    /**Get the real server time
    *HTMLObject element: ServerTime element holding hour, minute and second
    */  
    var getRealServerTime = function(element) {
        currentDate = document.getElementById('serverDate').firstChild.nodeValue;
        splitDate = currentDate.split('/');
 
        date = splitDate[0];
        month = splitDate[1]-1;
        year = splitDate[2];
 
        // obtain time
        if(element.firstChild.nodeValue == null){
            return -1;
        }
        var part = element.firstChild.nodeValue.split(":");
 
        // remove leading zeros
        for(var j=1; j<3; j++) {
            if(part[j].charAt(0) == "0")
                part[j] = part[j].substring(1, part[j].length);
        }
 
        // calculate time
        var hours = parseInt(part[0]);
        var minutes = parseInt(part[1]);
        var seconds = parseInt(part[2]);
        var time = hours*60*60+minutes*60+seconds;
        var dateObject = new Date(year, month, date,hours, minutes,seconds);
        return dateObject.getTime()/1000;
    }
    
    //perform modification
    modifyDocument();

    /**Debugging helper*/
    var getValues = function(obj){
        var res = '';

        res += 'Objekt: '+obj+'\n\n';
        for(temp in obj) {
            res += temp +': '+obj[temp]+'\n';
        }
        alert(res);
} 
})();
