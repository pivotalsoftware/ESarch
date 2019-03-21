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

package io.pivotal.refarch.cqrs.trader.app.query.users;

import io.pivotal.refarch.cqrs.trader.coreapi.users.FindAllUsersQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.users.UserByIdQuery;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.domain.Sort.Order.asc;

@Service
@ProcessingGroup("userQueryModel")
public class UserEventHandler {

    private final UserViewRepository userRepository;

    public UserEventHandler(UserViewRepository userRepository) {
        this.userRepository = userRepository;
    }

    @EventHandler
    public void on(UserCreatedEvent event) {
        UserView userView = new UserView();

        userView.setIdentifier(event.getUserId().getIdentifier());
        userView.setName(event.getName());
        userView.setUsername(event.getUsername());

        userRepository.save(userView);
    }

    @QueryHandler
    public UserView find(UserByIdQuery query) {
        return userRepository.findByIdentifier(query.getUserId().getIdentifier());
    }

    @QueryHandler
    public List<UserView> findAll(FindAllUsersQuery query) {
        return userRepository.findAll(PageRequest.of(query.getPageOffset(), query.getPageSize(), Sort.by(asc("name")))).getContent();
    }

}
