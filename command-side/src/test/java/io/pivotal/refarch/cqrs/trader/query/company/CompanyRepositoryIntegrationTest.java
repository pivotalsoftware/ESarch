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

package io.pivotal.refarch.cqrs.trader.query.company;

import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Jettro Coenradie
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CompanyRepositoryIntegrationTest {

    @Autowired
    private CompanyViewRepository companyRepository;

    @Test
    public void storeCompanyInRepository() {
        CompanyView companyView = new CompanyView();
        companyView.setIdentifier(new CompanyId().getIdentifier());
        companyView.setValue(100000);
        companyView.setAmountOfShares(1000);
        companyView.setTradeStarted(true);

        companyRepository.save(companyView);

        // TODO: Assertions
    }
}
