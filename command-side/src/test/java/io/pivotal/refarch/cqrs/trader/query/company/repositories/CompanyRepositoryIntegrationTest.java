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

package io.pivotal.refarch.cqrs.trader.query.company.repositories;

import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyId;
import io.pivotal.refarch.cqrs.trader.query.company.CompanyView;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Jettro Coenradie
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Ignore
//@ContextConfiguration(classes = {PersistenceInfrastructureConfig.class, HsqlDbConfiguration.class})
@ActiveProfiles("hsqldb")
public class CompanyRepositoryIntegrationTest {

    @Autowired
    private CompanyViewRepository companyRepository;

    @Test
    public void storeCompanyInRepository() {
        CompanyView companyView = new CompanyView();
        companyView.setIdentifier(new CompanyId().toString());
        companyView.setValue(100000);
        companyView.setAmountOfShares(1000);
        companyView.setTradeStarted(true);

        companyRepository.save(companyView);
    }
}
