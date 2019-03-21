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

package io.pivotal.refarch.cqrs.trader.app.command.company;

import io.pivotal.refarch.cqrs.trader.coreapi.company.AddOrderBookToCompanyCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyCreatedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyId;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CreateCompanyCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.company.OrderBookAddedToCompanyEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Aggregate
public class Company {

    @AggregateIdentifier
    private CompanyId companyId;

    @SuppressWarnings("UnusedDeclaration")
    public Company() {
        // Required by Axon Framework
    }

    @CommandHandler
    public Company(CreateCompanyCommand cmd) {
        long companyValue = cmd.getCompanyValue();
        if (companyValue < 0) {
            throw new IllegalArgumentException("Company value should be a positive number");
        }

        long amountOfShares = cmd.getAmountOfShares();
        if (amountOfShares < 0) {
            throw new IllegalArgumentException("Amount of shares should be a positive number");
        }

        apply(new CompanyCreatedEvent(cmd.getCompanyId(), cmd.getCompanyName(), companyValue, amountOfShares));
    }

    @CommandHandler
    public void handle(AddOrderBookToCompanyCommand cmd) {
        apply(new OrderBookAddedToCompanyEvent(companyId, cmd.getOrderBookId()));
    }

    @EventSourcingHandler
    public void on(CompanyCreatedEvent event) {
        companyId = event.getCompanyId();
    }
}
