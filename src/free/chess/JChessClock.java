/**
 * The chess framework library.
 * More information is available at http://www.jinchess.com/.
 * Copyright (C) 2007 Alexander Maryanovsky.
 * All rights reserved.
 *
 * The chess framework library is free software; you can redistribute
 * it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * The chess framework library is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the chess framework library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package free.chess;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import free.util.GraphicsUtilities;
import free.util.TextUtilities;


/**
 * A Component for displaying a simple, text based chess clock.
 */

public class JChessClock extends AbstractChessClock{
  
  
  
  /**
   * The font we use by default.
   */
  
  private static final Font DEFAULT_FONT;
  static{
    Font font = null;
    try{
      InputStream in = JChessClock.class.getResourceAsStream("clockfont.ttf");
      if (in != null){
        font = Font.createFont(Font.TRUETYPE_FONT, new BufferedInputStream(in));
        in.close();
      }
    } catch (FontFormatException e){}
      catch (IOException e){}
    DEFAULT_FONT = font;
  }
  
  
  
  /**
   * The active background color.
   */
  
  private Color activeBG = new Color(30, 100, 230);
  
  
  
  /**
   * The active foreground color.
   */
  
  private Color activeFG = Color.white;
  
  
  
  /**
   * The inactive background color. Defaults to the parent's background.
   */
  
  private Color inactiveBG = null;
  
  
  
  /**
   * The inactive foreground color.
   */
  
  private Color inactiveFG = Color.black;
  
  
  
  /**
   * The size of the JChessClock the last time it was painted.
   */
  
  private final Dimension lastSize = new Dimension();
  
  
  
  /**
   * The Font we used the last time this JChessClock was painted.
   */
  
  private Font lastFont = null;
  
  
  
  /**
   * The FontMetrics of the font we used the last time this JChessClock was painted.
   */
  
  private FontMetrics lastFontMetrics = null;
  
  
  
  /**
   * Creates a new JChessClock with the given initial amount of time 
   * (in milliseconds) on it.
   */
  
  public JChessClock(int time){
    super(time);
    
    if (DEFAULT_FONT == null)
      setFont(new Font("Monospaced", Font.BOLD, 50));
    else
      setFont(DEFAULT_FONT.deriveFont(50f));
  }
  
  
  
  /**
   * Sets the background color of this JChessClock when it's active.
   */
  
  public void setActiveBackground(Color color){
    activeBG = color;
  }
  
  
  
  /**
   * Returns the background color of this JChessClock when it's active.
   */
  
  public Color getActiveBackground(){
    return activeBG;
  }
  
  
  
  /**
   * Sets the foreground color of this JChessClock when it's active.
   */
  
  public void setActiveForeground(Color color){
    activeFG = color;
  }
  
  
  
  /**
   * Returns the foreground color of this JChessClock when it's active.
   */
  
  public Color getActiveForeground(){
    return activeFG;
  }
  
  
  
  /**
   * Sets the background color of this JChessClock when it's inactive.
   */
  
  public void setInactiveBackground(Color color){
    inactiveBG = color;
  }
  
  
  
  /**
   * Returns the background color of this JChessClock when it's inactive.
   */
  
  public Color getInactiveBackground(){
    return inactiveBG;
  }
  
  
  
  /**
   * Sets the foreground color of this JChessClock when it's inactive.
   */
  
  public void setInactiveForeground(Color color){
    inactiveFG = color;
  }
  
  
  
  /**
   * Returns the foreground color of this JChessClock when it's inactive.
   */
  
  public Color getInactiveForeground(){
    return inactiveFG;
  }
  
  
  
  /**
   * Sets whether the owner of this JChessClock is also the owner of the current
   * turn.
   */
  
  public void setActive(boolean isActive){
    super.setActive(isActive);
    repaint();
  }
  
  
  
  /**
   * Returns the string that should be drawn for the given time, in milliseconds.
   */
  
  protected String createTimeString(int time){
    boolean isNegative = time<0;
    
    time = Math.abs(time);
    int hours = time/(1000*60*60);
    time -= hours*1000*60*60;
    int minutes = time/(1000*60);
    time -= minutes*1000*60;
    int seconds = time/1000;
    time -= seconds*1000;
    int tenths = time/100;
    time -= tenths*100;
    
    String signString = isNegative ? "-" : "";
    
    switch (getActualDisplayMode()){
      case HOUR_MINUTE_DISPLAY_MODE:
        String sepString = (Math.abs(tenths) > 4) || !isRunning() ? ":" : " "; 
        return signString + String.valueOf(hours) + 
        sepString + TextUtilities.padStart(String.valueOf(minutes), '0', 2);  
      case MINUTE_SECOND_DISPLAY_MODE:
        return signString + TextUtilities.padStart(String.valueOf(60*hours+minutes), '0', 2) +
        ":" + TextUtilities.padStart(String.valueOf(seconds), '0', 2);
      case SECOND_TENTHS_DISPLAY_MODE:
        return signString + TextUtilities.padStart(String.valueOf(60*hours+minutes), '0', 2) +
        ":" + TextUtilities.padStart(String.valueOf(seconds), '0', 2) + "." +
        String.valueOf(tenths);
      default:
        throw new IllegalStateException("Bad display mode value: " + getDisplayMode()); 
    }
  }
  
  
  
  /**
   * Determines and returns the largest font of the same kind as the given Font
   * with which the given text can be drawn so that it still fits into the given
   * width and height.
   */
  
  protected Font findLargestFittingFont(String text, int width, int height, Graphics g, Font font){
    int maxSize = GraphicsUtilities.getMaxFittingFontSize(g, font, text, new Dimension(width, 6*height/7));
    return font.deriveFont((float)maxSize);
  }
  
  
  
  /**
   * Overrides JComponent.paintComponent(Graphics) to paint this JChessClock.
   */
  
  public void paintComponent(Graphics g){
    Graphics2D g2d = (Graphics2D)g;
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    
    String text = createTimeString(getTime());
    int width = getWidth();
    int height = getHeight();
    if ((width != lastSize.width) || (height != lastSize.height)){
      lastSize.width = getWidth();
      lastSize.height = getHeight();
      
      lastFont = findLargestFittingFont(text, lastSize.width, lastSize.height, g, getFont());
      lastFontMetrics = g.getFontMetrics(lastFont);
    }
    
    Color bgColor = isActive() ? getActiveBackground() : getInactiveBackground();
    if (bgColor != null){
      g.setColor(bgColor);
      g.fillRect(0, 0, width, height);
    }
    
    Color fgColor = isActive() ? getActiveForeground() : getInactiveForeground();
    g.setColor(fgColor);
    g.setFont(lastFont);
    int x = 0;
    int ascent = lastFontMetrics.getAscent();
    int y = ascent + (height - ascent) / 2;
    
    g.drawString(text, x, y);
  }
  
  
  
  /**
   * Returns the preferred width of this <code>JChessClock</code> when displayed
   * at the specified height.
   */
  
  public int getPreferredWidth(int height){
    String text = createTimeString(getTime());
    Font font = getFont();
    Font prefFont = font.deriveFont((float)height);
    FontMetrics fm = GraphicsUtilities.getFontMetrics(prefFont);
    return fm.stringWidth(text);
  }
  
  
  
  
  /**
   * Returns the preferred size of this JChessClock.
   */
  
  public Dimension getPreferredSize(){
    String text = createTimeString(getTime());
    Font font = getFont();
    Font prefFont = font.deriveFont(50f);
    FontMetrics fm = GraphicsUtilities.getFontMetrics(prefFont);
    int fontWidth = fm.stringWidth(text);
    int fontHeight = fm.getAscent() + fm.getDescent();
    return new Dimension(fontWidth, fontHeight);
  }
  
  
  
  
  /**
   * Returns the minimum size of this JChessClock.
   */
  
  public Dimension getMinimumSize(){
    String text = createTimeString(getTime());
    Font font = getFont();
    Font prefFont = font.deriveFont(12f);
    FontMetrics fm = GraphicsUtilities.getFontMetrics(prefFont);
    int fontWidth = fm.stringWidth(text);
    int fontHeight = fm.getAscent() + fm.getDescent();
    return new Dimension(fontWidth, fontHeight);
  }
  
  
  
}
