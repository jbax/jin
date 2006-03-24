/**
 * Jin - a chess client for internet chess servers.
 * More information is available at http://www.jinchess.com/.
 * Copyright (C) 2005 Alexander Maryanovsky.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */


package free.jin.console.prefs;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import free.jin.BadChangesException;
import free.jin.I18n;
import free.jin.console.ConsoleManager;
import free.jin.ui.PreferencesPanel;



/**
 * The main preferences panel for the console plugin. 
 */

public class ConsolePrefsPanel extends PreferencesPanel{
  

  
  /**
   * The text preferences panel.
   */
  
  private final PreferencesPanel textPrefsPanel;
  
  
  
  /**
   * The behaviour preferences panel.
   */
  
  private final PreferencesPanel behaviourPrefsPanel;


  
  /**
   * Creates a new <code>ConsolePrefsPanel</code>
   */
  
  public ConsolePrefsPanel(ConsoleManager consoleManager){
    this.textPrefsPanel = createTextPrefsPanel(consoleManager);
    this.behaviourPrefsPanel = createBehaviourPrefsPanel(consoleManager);
    
    I18n i18n = I18n.get(getClass(), ConsolePrefsPanel.class);
    
    ChangeListener changeListener = new ChangeListener(){
      public void stateChanged(ChangeEvent evt){
        fireStateChanged();
      }
    };
    
    textPrefsPanel.addChangeListener(changeListener);
    behaviourPrefsPanel.addChangeListener(changeListener);
    
    textPrefsPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
    behaviourPrefsPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
    
    JTabbedPane tabs = new JTabbedPane();
    tabs.addTab(i18n.getString("textDisplayTab.text"), null, textPrefsPanel, i18n.getString("textDisplayTab.tooltip"));
    tabs.addTab(i18n.getString("consoleBehaviourTab.text"), null, behaviourPrefsPanel, i18n.getString("consoleBehaviourTab.tooltip"));
    
    setLayout(new BorderLayout());
    add(tabs, BorderLayout.CENTER);
  }
  
  
  
  /**
   * Creates the <code>TextPrefsPanel</code> to be used in this console prefs
   * panel. This method returns a plain <code>TextPrefsPanel</code>, but allows
   * subclasses to return a subclass of it.
   */
  
  protected TextPrefsPanel createTextPrefsPanel(ConsoleManager cm){
    return new TextPrefsPanel(cm);
  }
  
  
  
  /**
   * Creates the <code>BehaviourPrefsPanel</code> used in this console prefs.
   * This method returns a plain <code>BehaviourPrefsPanel</code>, but allows
   * subclasses to return a subclass of it.
   */
  
  protected BehaviourPrefsPanel createBehaviourPrefsPanel(ConsoleManager cm){
    return new BehaviourPrefsPanel(cm);
  }

  
  
  public void applyChanges() throws BadChangesException{
    textPrefsPanel.applyChanges();
    behaviourPrefsPanel.applyChanges();
  }

}
