/**
 * Jin - a chess client for internet chess servers.
 * More information is available at http://www.hightemplar.com/jin/.
 * Copyright (C) 2002 Alexander Maryanovsky.
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

package free.jin.sound;

import free.util.audio.*;
import free.jin.plugin.Plugin;
import free.jin.event.ChatListener;
import free.jin.event.ChatEvent;
import free.jin.event.ConnectionListener;
import free.jin.event.ConnectionEvent;
import free.jin.JinConnection;
import jregex.*;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.InputStream;
import java.io.IOException;
import javax.swing.JMenu;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * The plugin responsible for producing sound on all the relevant events.
 */

public class SoundManager extends Plugin implements ChatListener, ConnectionListener{


  /**
   * Maps chat message patterns to filenames containing the sound data.
   */

  protected final Hashtable patToFilenames = new Hashtable();




  /**
   * Maps sound filenames to AudioClips loaded from those filenames.
   */

  protected final Hashtable filenamesToAudioClips = new Hashtable();



  /**
   * Maps event names such as ("OnConnect") to AudioClips.
   */

  protected final Hashtable eventsToAudioClips = new Hashtable();




  /**
   * True when the plugin is "on", i.e. sounds are on.
   */

  protected boolean isOn;




  /**
   * Initializes the state of the plugin from user properties, loads the sounds
   * and registers all the listeners.
   */

  public void start(){
    init();
    loadSounds();
    registerListeners();
  }




  /**
   * Undoes what the <code>start()</code> method does.
   */

  public void stop(){
    saveState();
    unregisterListeners();
    unloadSounds();
  }





  /**
   * Initializes the state of the plugin from the state specified in the user
   * properties.  This method is called from the start method of the plugin.
   */

  protected void init(){
    isOn = getProperty("on", "true").toLowerCase().equals("true");
  }





  /**
   * Saves the current state of the plugin into the user properties. This method
   * is called from the start method of the plugin.
   */

  protected void saveState(){
    setProperty("on", isOn ? "true" : "false", true);
  }




  /**
   * Creates and returns the JMenu for this plugin.
   */

  public JMenu createPluginMenu(){
    JMenu myMenu = new JMenu(getName());
    
    JCheckBoxMenuItem onMenu = new JCheckBoxMenuItem("Sound on", isOn);
    onMenu.addChangeListener(new ChangeListener(){
      
      public void stateChanged(ChangeEvent evt){
        isOn = ((JCheckBoxMenuItem)(evt.getSource())).getState();
      }
      
    });

    myMenu.add(onMenu);

    return myMenu;
  }





  /**
   * Loads all the sounds and maps them to chat patterns.
   */

  protected void loadSounds(){
    int numPatterns = Integer.parseInt(getProperty("num-sound-patterns"));

    for (int i=0;i<numPatterns;i++){
      try{
        String filename = getProperty("sound-file-"+i);
        String pattern = getProperty("chat-pattern-"+i);
        Pattern regex = new Pattern(pattern);
        
        if (!filenamesToAudioClips.containsKey(filename)){
          InputStream in = getClass().getResourceAsStream(filename);
          if (in==null)
            continue;
          filenamesToAudioClips.put(filename, new AudioClip(in));
          in.close();
        }

        patToFilenames.put(regex, filename);
      } catch (IOException e){
          e.printStackTrace();
        }
        catch (PatternSyntaxException e){
          e.printStackTrace();
        }
    }

    loadEventAudioClip("OnConnect");
    loadEventAudioClip("OnLogin");
    loadEventAudioClip("OnDisconnect");

    loadEventAudioClip("Move");
    loadEventAudioClip("Capture");
    loadEventAudioClip("Castling");
    loadEventAudioClip("IllegalMove");
    loadEventAudioClip("GameEnd");
    loadEventAudioClip("GameStart");
  }




  /**
   * Tries to load an AudioClip for the given event and map the event name to the
   * AudioClip in the <code>eventsToAudioClips</code> hashtable. Silently fails if
   * unsuccessful.
   */

  protected final void loadEventAudioClip(String eventName){
    try{
      String resourceName = getProperty(eventName);
      if (resourceName==null)
        return;
      InputStream in = getClass().getResourceAsStream(resourceName);
      if (in==null)
        return;
      eventsToAudioClips.put(eventName, new AudioClip(in));
      in.close();
    } catch (IOException e){
        e.printStackTrace();
      }
  }



  
  /**
   * Registers all the necessary listeners.
   */

  protected void registerListeners(){
    JinConnection conn = getConnection();
    conn.addChatListener(this);
    conn.addConnectionListener(this);
  }





  /**
   * Unregisters all the listeners registered by <code>registerListeners()</code>.
   */

  protected void unregisterListeners(){
    JinConnection conn = getConnection();
    conn.removeChatListener(this);
    conn.removeConnectionListener(this);
  }




  /**
   * Unloads all the sounds.
   */

  protected void unloadSounds(){
    patToFilenames.clear();
    filenamesToAudioClips.clear();
  }




  /**
   * Listens to ChatEvents and makes appropriate sounds.
   */

  public void chatMessageArrived(ChatEvent evt){
    if (!isOn())
      return;

    String type = evt.getType();
    Object forum = evt.getForum();
    String sender = evt.getSender();
    String chatMessageType = type+"."+(forum == null ? "" : forum.toString())+"."+sender;

    Enumeration enum = patToFilenames.keys();
    while (enum.hasMoreElements()){
      Pattern regex = (Pattern)enum.nextElement();
      Matcher matcher = regex.matcher(chatMessageType);
      if (matcher.find()){
        String filename = (String)patToFilenames.get(regex);
        AudioClip clip = (AudioClip)filenamesToAudioClips.get(filename);
        clip.play();
      }
    } 
  }





  /**
   * Plays the event associated with the given event.
   * Currently recognized event names include:
   * <UL>
   *   <LI> OnConnect - A connection was established.
   *   <LI> OnLogin - Login procedure succeeded.
   *   <LI> OnDisconnect - Disconnected.
   *   <LI> Move - A move is made.
   *   <LI> Capture - A capture move is made.
   *   <LI> Castling - A castling move is made.
   *   <LI> IllegalMove - An illegal move was attempted.
   *   <LI> GameEnd - A game ended.
   * </UL>
   * Returns true if the given event is recognized, false otherwise. Note that
   * for various reasons (like the user disabling sounds), the sound may not
   * be played.
   */

  public boolean playEventSound(String eventName){
    AudioClip clip = (AudioClip)eventsToAudioClips.get(eventName);
    if (clip!=null){
      if (isOn())
        clip.play();
      return true;
    }

    return false;
  }




  /**
   * Plays the sound mapped to the "OnConnect" event.
   */

  public void connectionEstablished(ConnectionEvent evt){
    playEventSound("OnConnect");
  }




  /**
   * Plays the sound mapped to the "OnLogin" event.
   */

  public void connectionLoggedIn(ConnectionEvent evt){
    playEventSound("OnLogin");
  }




  /**
   * Plays the sound mapped to the "OnDisconnect" event.
   */

  public void connectionLost(ConnectionEvent evt){
    playEventSound("OnDisconnect");
  }




  /**
   * Returns <code>true</code> if the sound is currently on. Returns
   * <code>false</code> otherwise.
   */

  public boolean isOn(){
    return isOn;
  }


}
