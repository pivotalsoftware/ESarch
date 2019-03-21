/*
 * Copyright (c) 2010-2012. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.pivotal.refarch.cqrs.trader.app.query.portfolio;

import org.springframework.data.annotation.Id;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
public class PortfolioView {

    @Id
    @javax.persistence.Id
    private String identifier;
    private String userId;
    private long amountOfMoney;
    private long reservedAmountOfMoney;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "PORTFOLIO_ITEM_POSSESSION",
            joinColumns = @JoinColumn(name = "PORTFOLIO_ID"),
            inverseJoinColumns = @JoinColumn(name = "ITEM_ID")
    )
    private Map<String, ItemEntry> itemsInPossession = new HashMap<>();
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "PORTFOLIO_ITEM_RESERVED",
            joinColumns = @JoinColumn(name = "PORTFOLIO_ID"),
            inverseJoinColumns = @JoinColumn(name = "ITEM_ID")
    )
    private Map<String, ItemEntry> itemsReserved = new HashMap<>();

    /*-------------------------------------------------------------------------------------------*/
    /* utility functions                                                                         */
    /*-------------------------------------------------------------------------------------------*/
    public long obtainAmountOfAvailableItemsFor(String orderBookId) {
        long possession = obtainAmountOfItemsInPossessionFor(orderBookId);
        long reserved = obtainAmountOfReservedItemsFor(orderBookId);
        return possession - reserved;
    }

    public long obtainAmountOfReservedItemsFor(String orderBookId) {
        ItemEntry item = findReservedItemById(orderBookId);
        if (null == item) {
            return 0;
        }
        return item.getAmount();
    }

    public long obtainAmountOfItemsInPossessionFor(String orderBookId) {
        ItemEntry item = findItemInPossession(orderBookId);
        if (null == item) {
            return 0;
        }
        return item.getAmount();
    }

    public long obtainMoneyToSpend() {
        return amountOfMoney - reservedAmountOfMoney;
    }

    public ItemEntry findReservedItemById(String orderBookId) {
        return itemsReserved.get(orderBookId);
    }

    public ItemEntry findItemInPossession(String orderBookId) {
        return itemsInPossession.get(orderBookId);
    }

    public void addReservedItem(ItemEntry itemEntry) {
        handleAdd(itemsReserved, itemEntry);
    }

    public void addItemInPossession(ItemEntry itemEntry) {
        handleAdd(itemsInPossession, itemEntry);
    }

    public void removeReservedItem(String itemId, long amount) {
        handleRemoveItem(itemsReserved, itemId, amount);
    }

    public void removeItemsInPossession(String itemID, long amount) {
        handleRemoveItem(itemsInPossession, itemID, amount);
    }

    /*-------------------------------------------------------------------------------------------*/
    /* Getters and setters                                                                       */
    /*-------------------------------------------------------------------------------------------*/
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getAmountOfMoney() {
        return amountOfMoney;
    }

    public void setAmountOfMoney(long amountOfMoney) {
        this.amountOfMoney = amountOfMoney;
    }

    public long getReservedAmountOfMoney() {
        return reservedAmountOfMoney;
    }

    public void setReservedAmountOfMoney(long reservedAmountOfMoney) {
        this.reservedAmountOfMoney = reservedAmountOfMoney;
    }

    public Map<String, ItemEntry> getItemsInPossession() {
        return itemsInPossession;
    }

    public void setItemsInPossession(Map<String, ItemEntry> itemsInPossession) {
        this.itemsInPossession = itemsInPossession;
    }

    public Map<String, ItemEntry> getItemsReserved() {
        return itemsReserved;
    }

    public void setItemsReserved(Map<String, ItemEntry> itemsReserved) {
        this.itemsReserved = itemsReserved;
    }

    /*-------------------------------------------------------------------------------------------*/
    /* Private helper methods                                                                    */
    /*-------------------------------------------------------------------------------------------*/
    private void handleAdd(Map<String, ItemEntry> items, ItemEntry itemEntry) {
        if (items.containsKey(itemEntry.getIdentifier())) {
            ItemEntry foundEntry = items.get(itemEntry.getIdentifier());
            foundEntry.setAmount(foundEntry.getAmount() + itemEntry.getAmount());
        } else {
            items.put(itemEntry.getIdentifier(), itemEntry);
        }
    }

    private void handleRemoveItem(Map<String, ItemEntry> items, String itemId, long amount) {
        if (items.containsKey(itemId)) {
            ItemEntry foundEntry = items.get(itemId);
            foundEntry.setAmount(foundEntry.getAmount() - amount);
            if (foundEntry.getAmount() <= 0) {
                items.remove(foundEntry.getIdentifier());
            }
        }
    }

    @Override
    public String toString() {
        return "PortfolioView{" +
                "amountOfMoney=" + amountOfMoney +
                ", identifier='" + identifier + '\'' +
                ", userId='" + userId + '\'' +
                ", reservedAmountOfMoney=" + reservedAmountOfMoney +
                ", itemsInPossession=" + itemsInPossession +
                ", itemsReserved=" + itemsReserved +
                '}';
    }
}
