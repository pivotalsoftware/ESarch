# Axon Trader

This project demonstrates the use of [Command and Query Responsibility Segregation (CQRS)][17] and [Event Sourcing][17] with [Pivotal Application Service][11] (a.k.a Pivotal Cloud Foundry or PAS). The code consists of two [Spring Boot][9] microservices built using the open source [Axon Framework][10] and a user interface constructed using [Node.js][16] and [React.js][22].

> __Note:__ If you just want to experience Axon Trader without having to build and host the code for yourself, simply navigate your browser to [https://axontrader.cfapps.io/][5].

## Before You Begin

This demo will *only* intended to run in a compatible [Pivotal Application Service][11] environment. This environment must also have a marketplace containing the required "brokered" services. Examples of compatible PAS environments would include [Pivotal Web Services (PWS)][6], any enterprise PCF foundation with the correct services, or locally on your PC using [PCF-Dev][7] (if started with the required marketplace services running).

> In this tutorial we will use Pivotal Web Services (PWS) as our PAS as it meets all of the requirements set out above. If you don't have a PWS account, [you can sign up and receive free credits here][6].

## Prerequisites

In order to follow along with this tutorial...

- You must have the [cf-cli][8] installed (you can test this test by calling `cf version` in your terminal).
- You must have access to a [Pivotal Application Service][11] instance.
- You must have marketplace services for MySQL, RabbitMQ, Spring Cloud Services Registry and Spring Cloud Services Config Server in PAS.
- You must have the `bash` terminal in order to run the provided scripts.

## Building and Running the Axon Trader on PWS

To begin, clone this repository and `cd` to it's directory in your terminal window. There you'll notice there is a folder for the `trader-app` api, a folder for the `trading-engine` microservice and a folder for the `trader-ui`  user interface (amongst others).

> You don't have to explore the code in these folders in order to run the Axon Trader application, but you might want to take a look if you're curious how it's written.

To build and run Axon Trader application in your [Pivotal Web Services][6] space, read on. If you're using an enterprise instance of Pivotal Application Service the instructions are the same (as long as you have the required marketplace services available).

## 1. Paving your Pivotal Application Service Environment

The Axon Trader Reference Architecture is built using Java, Spring Boot and Node.js (bootstrapping React.js single page application). To run, the Axon Trader applications requires certain PAS Marketplace Services to be available in the "space" where you will host and run the applications. These marketplace services are [ClearDB][1] (MySQL), [CloudAMQP][2] (RabbitMQ), [Spring Cloud Services Config Server][3] and [Spring Cloud Services Service Registry][4].

To pave your PWS space with these marketplace services, simply run the `pave.sh` script provided. The pave script isn't rocket science, there's no magic, it just saves you typing `cf create-service` multiple times. Obviously it needs some details from you about your PWS space in order to run, as illustrated below...

```bash
# you need to provide this script with your PCF username, password, org and space...
./pave.sh <PWS-USERNAME> <PWS-PASSWORD> <PWS-ORG> <PWS-SPACE>
```

> Note: In the `pave.sh` script we have chosen only the __free__ PWS service plans for each of the marketplace services. These free plans won't cost you a penny to run but they do have usage limitations (like a 7 day lifetime in the case of the Spring Cloud Services), so make yourself aware of these and make other choices as you feel appropriate.

Following the completion of the `pave.sh` script, a quick call to `cf services` in your terminal window should provide you with a list of all of the marketplace services you just created. These services will now be available for use by the Axon Trader applications (which is good, because the Axon Trader applications depend on them to run).

> Note: If this was your first time using the Pivotal Application Service, notice how easy it is to create services via the marketplace. Pivotal Application Service is designed to offer a low code, low friction developer experience.

### The Axon Trader Configuration

The configuration of the backend microservices in the Axon Trader application is handled by [Spring Cloud Services Config][3]. In the `pave.sh` script, when commissioning Spring Cloud Services Config, a file called `config-server-setup.json` is used to tell the service where it can find the configuration for the microservices. If you wish to change this location, edit the file. The config repository can be cloned from [here][21].

## 2. Building and Pushing the Axon Trader applications Pivotal Web Services

Now the PWS space is "paved" we can build and "push" our microservice applications and their user interface to it. A script called `deploy-backend.sh` has been provided to do this to save you a little more repetitive typing...

```bash
# you need to provide the deploy script with your PCF username, password, org and space...
$ ./deploy-backend.sh <PWS-USERNAME> <PWS-PASSWORD> <PWS-ORG> <PWS-SPACE>
```

> If you open the script you'll notice how easy it is to use `cf push` to run your applications in the cloud. In a couple of minutes you can have microservices with externally accessible URLs and which will keep running no matter what.

When the script has finished, you should see a list of your currently deployed apps and their URLs...

```bash
name                            requested state   instances  urls
esrefarch-demo-trader-app       started           1/1        esrefarch-demo-trader-app.cfapps.io
esrefarch-demo-trader-ui        started           1/1        esrefarch-demo-trader-ui.cfapps.io
```

> Note: Take note of the URL for the `esrefarch-demo-trader-app`, you'll need it for the next task.

### Initialising the Axon Trader Database

Finally, we need to create some dummy data for the application (companies, users, and cash). To help with this, we have provided an actuator endpoint that accepts a POST request but doesn't require any content. You can use `curl` to trigger this as shown below.

```bash
curl -sL -X POST ${your-trader-app-url}/actuator/data-initializer
```

If this returns true, you should now have users, companies and orderbooks ready to be used once the UI is in place, which we'll do in the next section.

## 3. Configuring the Axon Trader UI to Pivotal Application Service

When we "pushed" the Axon Trader App to Pivotal Web Services a few moments ago, it was assigned a unique and random route URL. We now need to tell the Axon Trader UI about this unique URL so that it can communicate with the backend.

Edit the `HostURLsMapping` constant in the file `trader-app-ui/src/utils/config.js` and bind your axon trader UI and APP urls to each other as follows...

````javascript
export const HostURLsMapping = {
    "localhost": "http://localhost:8080",
    "your-trader-UI-hostname": "https://your-trader-APP-URL",
}
````

> So if your trader UI hostname was esrefarch-demo-trader-ui.cfapps.io and your trader app URL was https://esrefarch-demo-trader-app.cfapps.io then your configuration file would contain `"esrefarch-demo-trader-ui.cfapps.io": "https://esrefarch-demo-trader-app.cfapps.io"` as a key-value pair in the `config.js` JSON file.

Now this configuration is set, we can build and push the Axon Trader UI to PWS.

```bash
./deploy-frontend.sh <PWS-USERNAME> <PWS-PASSWORD> <PWS-ORG> <PWS-SPACE>
```

In a few minutes, the UI should have now have started and you will be in a position to test the application as described in the next section.

## 4. Test the Axon Trader Application

You should have a replica of the Axon Trader application running in your own PWS space. This means you are ready to test it. To begin, we need the Axon Trader UI URL. You can discover all the URLs for all your PWS application by calling `cf apps` from your command line...

```bash
cf apps

Getting apps in org esrefarch / space myspace as me@me.com...
OK

name                            requested state   instances  urls
esrefarch-demo-trader-app       started           1/1        esrefarch-demo-trader-app.cfapps.io
esrefarch-demo-trader-ui        started           1/1        esrefarch-demo-trader-ui.cfapps.io
esrefarch-demo-trading-engine   started           1/1        esrefarch-demo-trading-engine.cfapps.io
```

Copy the url for the `esrefarch-demo-trader-ui` application from the `urls` column and paste it into your browser window. You should see something like this...

![Axon Trader][12]

If the User Interface is there you can use the Axon Trader UI application and check that everything is working as expected. You should be able to impersonate users, view the dashboard and the order books and place trades. If you forgot to initialise the data you won't have any users, companies or orderbooks. Go back to section 2. and complete the initialisation exercise before you continue.

## Optional Tasks

The following tasks are all optional.

### [Optional] Continuously Delivering the Axon Trader Application to PAS

This section on continuously delivering Axon Trader is purely mentioned here for completeness. You can manually refresh the Axon Trader applications at any time simply by repeating the steps above.

For the continuous integration and deployment needs of the Axon Trader application, the team have chosen to use [Concourse-ci][13]. You can view our team's Axon Trader build monitor [here][14] at any time. _Remember: builds can be green as well as red. Past performance is no guarantee of future results._  

All of the configuration and scripts required to recreate our build pipeline on your own Concourse server is included in the `ci` folder under the project root. The only thing that's missing is the `private.yml` file which we use to specify some secrets and additional config required by the pipeline. 

Here is an example of `ci/private.yml` so you can recreate it...

````yaml
---
cf-trading-engine-url: http://your-trading-engine.cfapps.io
cf-trader-app-url: http://your-trader-app.cfapps.io
cf-trader-ui-url: http://your-trader-ui.cfapps.io
cf-endpoint: api.run.pivotal.io
cf-user: me@pivotal.io
cf-password: ************
cf-org: myorg
cf-space: prod
skip-ssl: false
webhook: https://hooks.slack.com/your-webhook-url
git-uri: git@github.com:pivotalsoftware/ESarch.git
git-private-key: |
  -----BEGIN RSA PRIVATE KEY-----
````

> Note: For the `git-uri` you could use your own fork if you don't want to track our repo.

To install and run the pipeline on Concourse, `login` using [fly][15] then run `set-pipeline` using the `pipeline.yml` and the `private.yml` as shown below. Finally `unpause-pipeline` so make it capable of being run. The commands for this are illustrated below...

````bash
fly -t wings login -c http://your-concourse-url
fly -t wings set-pipeline -p bw-esrefarch-demo -c ci/pipeline.yml -l ci/private.yml
fly -t wings unpause-pipeline -p bw-esrefarch-demo
````

### [Optional] Cleaning Up Your PWS

If you have finished trying the Axon Trader application and you would like to clear down your PWS space, a script has been provided that will stop the applications, remove the services and delete everything.

```bash
./teardown.sh  <PWS-USERNAME> <PWS-PASSWORD> <PWS-ORG> <PWS-SPACE>
```

Once completed, the `teardown.sh` script should leave you with an empty space and no services. To check this is the case you could call...

```bash
cf apps
cf services
cf network-policies
```

You should see that each is empty. If not, use the PWS dashboard UI at [run.pivotal.io][18] to clear things manually or use the cf-cli's `cf help -a" command to find and execute the commands you need.

### [Optional] Running Axon Trader Locally

You can run the Axon Trader locally on your desktop without Pivotal Cloud Foundry, but the instructions for this are beyond the scope of this tutorial, but here are a few hints to get you started:-

- You need RabbitMQ running in the background (we use Docker for this).
- The Axon Trader backend microservices will default to using an in memory database if none is provided.
- The local configuration is used and basic configuration is provided (very handy).
- You'll need Spring Cloud Registry running in the background, see the `discovery-server` folder for a quickstart app.
- You'll need [Node.js][16] and `npm install` to build the frontend and `npm start` to run it.
- There are some sample Postman calls for the backend in the file `ESArchSmokeTest.postman_collection.json`

## CQRS & Event Sourcing Architectural Overview

CQRS on itself is a very simple pattern. It only prescribes that the component of an application that processes commands should be separated from the component that processes queries. Although this separation is very simple in and of itself, it provides a number of very powerful features when combined with other patterns. Axon provides the building blocks that make it easier to implement the different patterns that can be used in combination with CQRS.

### Commands and the Command Bus

Commands are typically represented by simple and straightforward objects that contain all data necessary for a command handler to execute it. A command expresses its intent by its name. In Java terms, that means the class name is used to figure out what needs to be done, and the fields of the command provide the information required to do it.

The Command Bus receives commands and routes them to the Command Handlers. Each command handler responds to a specific type of command and executes logic based on the contents of the command. In some cases, however, you would also want to execute logic regardless of the actual type of command, such as validation, logging or authorization.

The command handler retrieves domain objects (Aggregates) from a repository and executes methods on them to change their state. These aggregates typically contain the actual business logic and are therefore responsible for guarding their own invariants. The state changes of aggregates result in the generation of Domain Events. Both the Domain Events and the Aggregates form the domain model.

Repositories are responsible for providing access to aggregates. Typically, these repositories are optimized for lookup of an aggregate by its unique identifier only. Some repositories will store the state of the aggregate itself (using Object Relational Mapping, for example), while others store the state changes that the aggregate has gone through in an Event Store. The repository is also responsible for persisting the changes made to aggregates in its backing storage.

### Events, Event Bus and Event Sourcing

Axon provides support for both the direct way of persisting aggregates (using object-relational-mapping, for example) and for event sourcing.

The event bus dispatches events to all interested event listeners. This can either be done synchronously or asynchronously. Asynchronous event dispatching allows the command execution to return and hand over control to the user, while the events are being dispatched and processed in the background. Not having to wait for event processing to complete makes an application more responsive. Synchronous event processing, on the other hand, is simpler and is a sensible default. By default, synchronous processing will process event listeners in the same transaction that also processed the command.

Event listeners receive events and handle them. Some handlers will update data sources used for querying while others send messages to external systems. As you might notice, the command handlers are completely unaware of the components that are interested in the changes they make. This means that it is very non-intrusive to extend the application with new functionality. All you need to do is add another event listener. The events loosely couple all components in your application together.

In some cases, event processing requires new commands to be sent to the application. An example of this is when an order is received. This could mean the customer's account should be debited with the amount of the purchase, and shipping must be told to prepare a shipment of the purchased goods. In many applications, logic will become more complicated than this: what if the customer didn't pay in time? Will you send the shipment right away, or await payment first? The saga is the CQRS concept responsible for managing these complex business transactions.

### Queries and the Query Bus

Since Axon 3.1 the framework provides components to handle queries. The Query Bus receives queries and routes them to the Query Handlers. A query handler is registered at the query bus with both the type of query it handles as well as the type of response it providers. Both the query and the result type are typically simple, read-only DTO objects. The contents of these DTOs are typically driven by the needs of the User Interface. In most cases, they map directly to a specific view in the UI (also referred to as table-per-view).

It is possible to register multiple query handlers for the same type of query and type of response. When dispatching queries, the client can indicate whether he wants a result from one or from all available query handlers.

For more information on Axon [visit the Axon documentation][19].
For more information on Axon Trader [visit the Axon Trader wiki][20].


[1]: https://docs.run.pivotal.io/marketplace/services/cleardb.html
[2]: https://docs.run.pivotal.io/marketplace/services/cloudamqp.html
[3]: https://docs.pivotal.io/spring-cloud-services/2-0/common/config-server/index.html
[4]: https://docs.pivotal.io/spring-cloud-services/2-0/common/service-registry/index.html
[5]: https://esrefarch-demo-trader-ui.cfapps.io/
[6]: https://run.pivotal.io/
[7]: https://pivotal.io/pcf-dev
[8]: https://docs.cloudfoundry.org/cf-cli/install-go-cli.html
[9]: http://projects.spring.io/spring-boot/
[10]: https://axoniq.io/
[11]: https://pivotal.io/platform/pivotal-application-service
[12]: /images/AxonTrader-UI-001.png
[13]: https://concourse-ci.org/
[14]: https://wings.pivotal.io/teams/pcf-solutions-emea/pipelines/bw-esrefarch-demo
[15]: https://concourse-ci.org/fly.html
[16]: https://nodejs.org/en/
[17]: https://www.slideshare.net/BenWilcock1/microservice-architecture-with-cqrs-and-event-sourcing
[18]: https://run.pivotal.io
[19]: https://docs.axonframework.org
[20]: https://github.com/pivotalsoftware/ESarch/wiki/Axon-Trader-Reference-Documentation
[21]: https://github.com/pivotalsoftware/ESarch-conf
[22]: https://reactjs.org/
