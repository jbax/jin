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

package free.jin;

import free.jin.event.SeekListener;
import free.jin.Seek;


/**
 * An extension of the JinConnection interface which adds support for seeking
 * functionality.
 */

public interface SeekJinConnection extends JinConnection{


  /**
   * Adds the given SeekListener to the list of listeners receiving 
   * notifications when seeks are added and removed. Note that the
   * implementation must deliver not only new Seeks to the newly registered 
   * listener but also all of the current seeks as well.
   */

  void addSeekListener(SeekListener listener);



  /**
   * Removes the given SeekListener from the list of listeners receiving
   * notifications when seeks are added and removed.
   */

  void removeSeekListener(SeekListener listener);



  /**
   * Accepts the given seek. Note that the given seek must be an instance generated
   * by this SeekJinConnection and it must be in the current sought list.
   */

  void acceptSeek(Seek seek);

}
