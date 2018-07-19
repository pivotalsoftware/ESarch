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

package io.pivotal.refarch.cqrs.trader.command.order.matchers;

import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.CreateSellOrderCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioId;
import org.axonframework.commandhandling.CommandMessage;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

// TODO should be replaced by an inline lambda matching function
public class CreateSellOrderCommandMatcher extends BaseMatcher<CreateSellOrderCommand> {

    private OrderBookId orderBookdId;
    private PortfolioId portfolioId;
    private long tradeCount;
    private int itemPrice;

    private CreateSellOrderCommandMatcher(PortfolioId portfolioId, OrderBookId orderbookId, long tradeCount,
                                          int itemPrice) {
        this.portfolioId = portfolioId;
        this.orderBookdId = orderbookId;
        this.tradeCount = tradeCount;
        this.itemPrice = itemPrice;
    }

    public static Matcher newInstance(PortfolioId portfolioId, OrderBookId orderBookId, int tradeCount, int itemPrice) {
        return new CreateSellOrderCommandMatcher(portfolioId, orderBookId, tradeCount, itemPrice);
    }

    @Override
    public final boolean matches(Object o) {
        if (!(o instanceof CommandMessage)) {
            return false;
        }
        CommandMessage<CreateSellOrderCommand> message = (CommandMessage<CreateSellOrderCommand>) o;

        return doMatches(message.getPayload());
    }

    private boolean doMatches(CreateSellOrderCommand command) {
        return command.getOrderBookId().equals(orderBookdId)
                && command.getPortfolioId().equals(portfolioId)
                && tradeCount == command.getTradeCount()
                && itemPrice == command.getItemPrice();
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("CreateSellOrderCommand with tradeCount [")
                   .appendValue(tradeCount)
                   .appendText("], itemPrice [")
                   .appendValue(itemPrice)
                   .appendText("] for OrderBook with identifier [")
                   .appendValue(orderBookdId)
                   .appendText("] and for Portfolio with identifier [")
                   .appendValue(portfolioId)
                   .appendText("]");
    }
}
