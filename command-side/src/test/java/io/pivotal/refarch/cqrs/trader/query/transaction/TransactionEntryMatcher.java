/*
 * Copyright (c) 2010-2012. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.pivotal.refarch.cqrs.trader.query.transaction;

import org.mockito.ArgumentMatcher;

public class TransactionEntryMatcher implements ArgumentMatcher<TransactionView> {

    private final TransactionState state;
    private final String companyName;
    private final int amountOfItems;
    private final int amountOfItemsExecuted;
    private final long pricePerItem;

    public TransactionEntryMatcher(int amountOfItems,
                                   int amountOfItemsExecuted,
                                   String companyName,
                                   long pricePerItem,
                                   TransactionState state) {
        this.amountOfItems = amountOfItems;
        this.amountOfItemsExecuted = amountOfItemsExecuted;
        this.companyName = companyName;
        this.pricePerItem = pricePerItem;
        this.state = state;
    }

    @Override
    public boolean matches(TransactionView transactionView) {
        return amountOfItems == transactionView.getAmountOfItems()
                && amountOfItemsExecuted == transactionView.getAmountOfExecutedItems()
                && companyName.equals(transactionView.getCompanyName())
                && pricePerItem == transactionView.getPricePerItem()
                && state == transactionView.getState();
    }
}
