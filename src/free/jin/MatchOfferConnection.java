/**
 * Jin - a chess client for internet chess servers.
 * More information is available at http://www.jinchess.com/.
 * Copyright (C) 2007 Alexander Maryanovsky.
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

import java.util.Collection;

import free.jin.event.MatchOfferListenerManager;



/**
 * An extension of the <code>Connection</code> interface which adds support for
 * issuing and receiving notifications of match offers. 
 */

public interface MatchOfferConnection extends Connection{
  
  
  
  /**
   * Returns the <code>MatchOfferListenerManager</code> via which you can
   * ask to be notified when new match offers are made and withdrawn. 
   */
  
  MatchOfferListenerManager getMatchOfferListenerManager();
  
  
  
  /**
   * Returns the set of pending match offers. Note that the pending match offers
   * are only guaranteed to be retrieved if there are registered
   * <code>MatchOfferListener</code>s.
   */
  
  Collection getMatchOffers();
  
  
  
  /**
   * Issues the specified match offer.
   */
  
  void issue(UserMatchOffer offer);
  
  
  
  /**
   * Withdraws the specified match offer.
   */
  
  void withdraw(MatchOffer offer);
  
  
  
}