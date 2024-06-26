/*
 * Copyright (c) 1990-2012 kopiLeft Development SARL, Bizerte, Tunisia
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */
package org.ebics.client.api

import java.io.Serializable
import java.net.URL

/**
 * Details about EBICS communication with a given bank.
 *
 * @author Hachani
 */
interface EbicsBank : Serializable {
    /**
     * Returns the URL needed for communication to the bank.
     * @return the URL needed for communication to the bank.
     */
    val bankURL: URL

    /**
     * Returns the bank id.
     * @return the bank id.
     */
    val hostId: String

    /**
     * Returns the bank's name.
     * @return the bank's name.
     */
    val name: String

    /**
     * The name of the HTTP configuration which is used for this bank
     */
    val httpClientConfigurationName: String
}