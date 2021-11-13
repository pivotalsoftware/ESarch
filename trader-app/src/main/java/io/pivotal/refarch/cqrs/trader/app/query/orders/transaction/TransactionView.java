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

package io.pivotal.refarch.cqrs.trader.app.query.orders.transaction;

import io.pivotal.refarch.cqrs.trader.coreapi.orders.TransactionType;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionState;
import org.springframework.data.annotation.Id;

import javax.persistence.Entity;

@Entity
public class TransactionView {

    @Id
    @javax.persistence.Id
    private String identifier;
    private String orderBookId;
    private String portfolioId;

    private String companyName;
    private long amountOfItems;
    private long amountOfExecutedItems;
    private long pricePerItem;
    private TransactionState state;
    private TransactionType type;

    public long getAmountOfExecutedItems() {
        return amountOfExecutedItems;
    }

    public void setAmountOfExecutedItems(long amountOfExecutedItems) {
        this.amountOfExecutedItems = amountOfExecutedItems;
    }

    public long getAmountOfItems() {
        return amountOfItems;
    }

    public void setAmountOfItems(long amountOfItems) {
        this.amountOfItems = amountOfItems;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getOrderBookId() {
        return orderBookId;
    }

    public void setOrderBookId(String orderBookId) {
        this.orderBookId = orderBookId;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public long getPricePerItem() {
        return pricePerItem;
    }

    public void setPricePerItem(long pricePerItem) {
        this.pricePerItem = pricePerItem;
    }

    public TransactionState getState() {
        return state;
    }

    public void setState(TransactionState state) {
        this.state = state;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }
}
