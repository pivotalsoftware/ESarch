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

package io.pivotal.refarch.cqrs.trader.query.portfolio;

import org.mockito.ArgumentMatcher;

public class PortfolioEntryMatcher implements ArgumentMatcher<PortfolioView> {

    private final int itemsInPossession;
    private final String itemIdentifier;
    private final int amountOfItemInPossession;
    private final int itemsInReservation;
    private final int amountOfItemInReservation;

    public PortfolioEntryMatcher(String itemIdentifier,
                                 int itemsInPossession,
                                 int amountOfItemInPossession,
                                 int itemsInReservation,
                                 int amountOfItemInReservation) {
        this.itemsInPossession = itemsInPossession;
        this.itemIdentifier = itemIdentifier;
        this.amountOfItemInPossession = amountOfItemInPossession;
        this.itemsInReservation = itemsInReservation;
        this.amountOfItemInReservation = amountOfItemInReservation;
    }

    @Override
    public boolean matches(PortfolioView portfolioView) {

        return portfolioView.getItemsInPossession().size() == itemsInPossession
                && amountOfItemInPossession == portfolioView.findItemInPossession(itemIdentifier).getAmount()
                && portfolioView.getItemsReserved().size() == itemsInReservation
                && !(itemsInReservation != 0 && (amountOfItemInReservation != portfolioView
                .findReservedItemByIdentifier(itemIdentifier).getAmount()));
    }
}
