package contracts.trader.query

import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpStatus

Contract.make {
  request {
    method 'GET'
    url "/query/order-book/by-company/f82c40ec-a785-11e8-98d0-529269fb1459"
    headers {
      contentType applicationJson()
    }
  }
  response {
    status HttpStatus.OK.value()
    body("""
      [ {
        "identifier" : "f82c40ec-a785-11e8-98d0-529269fb1459",
        "companyIdentifier" : "f82c40ec-a785-11e8-98d0-529269fb1459",
        "companyName" : "Pivotal",
        "sellOrders" : [ {
          "jpaId" : 2,
          "identifier" : "f82c4ace-a785-11e8-98d0-529269fb1459",
          "tradeCount" : 5,
          "itemPrice" : 75,
          "userId" : "98684ad8-987e-4d16-8ad8-b620f4320f4c",
          "itemsRemaining" : 20,
          "type" : "Sell"
        } ],
        "buyOrders" : [ {
          "jpaId" : 1,
          "identifier" : "f82c4984-a785-11e8-98d0-529269fb1459",
          "tradeCount" : 5,
          "itemPrice" : 50,
          "userId" : "98684ad8-987e-4d16-8ad8-b620f4320f4c",
          "itemsRemaining" : 10,
          "type" : "Buy"
        } ]
      } ]
    """)
    headers {
      contentType applicationJson()
    }
    async()
  }
}
