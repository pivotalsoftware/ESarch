# Axon Trader

This project demonstrates the use of CQRS and Event Sourcing with [Pivotal Application Service][11] (a.k.a PCF or PAS). It consists of two [Spring Boot][9] microservices built using the open source [Axon Framework][10] and a user interface built using [Node.js][16].

> __Note:__ If you just want to experience Axon Trader without having to build and host the code yourself, go ahead! Simply navigate your browser to [https://esrefarch-demo-trader-ui.cfapps.io/][5] but be sure to circle back if you're curious about how CQRS and Event Sorcing works in practice. 

## Before You Begin.

This demo will *only* run in a compatible [Pivotal Application Service (PCF)][11] environment. This environment must also contain a marketplace with the correct "brokered" services. Examples of compatible environments would include [Pivotal Web Services (PWS)][6], an enterprise PCF foundation with the correct services, or locally on your PC using [PCF-Dev][7] (started with the correct marketplace services installed). 

> We recommend Pivotal Web Services (PWS) for this tutorial as it meets these requirements. If you don't have a PWS account, [you can sign up for free here][6].

## Prerequisites

In order to follow along with this tutorial...

- You must have the [cf-cli][8] installed (test by calling `cf version`).
- You must have access to a [Pivotal Application Service][11] instance.
- Your PCF must have the correct marketplace services (which are MySQL, Rabbit, Spring Cloud Registry and Spring Cloud Config).
- You must have a `bash` terminal in order to run the provided scripts.

# Building and Running the Axon Trader

First clone this repository using `git` and `cd` to it's directory in your terminal window. There you'll notice there is a folder for the `trader-app` microservice, a folder for the `trading-engine` microservice and a folder for the `trader-ui` (amongst others).

> You don't have to explore the code in order to run the Axon Trader application, but you might like to take a look later.

For instructions on how to build the Axon Trader Reference application for yourself and host them in your space on [Pivotal Web Services][6], read on.


### 1. Build the Microservice JARs

To obtain Spring Boot JAR's for the `trader-app` and `trading-engine` microservices of the Axon Trader Reference Architecture run the following script provided in the project folder...

```bash
./package.sh
```

> Note: The user interface is written in Node.js, so formally building it is not required in this step.

### 2. Pave the Pivotal Application Service Environment

The Axon Trader Reference Architecture needs certain Pivotal Cloud Foundry Marketplace Services to be available in the "space" where you will to run the applications. The services required are [ClearDB][1], [CloudAMQP][2], [Spring Cloud Config][3] and [Spring Cloud Registry][4].

> Note: In our scripts we have chosen the __free__ service plans for each of these services. These free plans do have usage limitations, so make yourself aware of these.

To pave your PWS space with these marketplace services, simply run the `pave.sh` script in the root of the project as follows... 

```bash
# you need to provide this script with your PCF username, password, org and space...
./pave.sh USERNAME PASSWORD ORG SPACE
```

Following this step, a quick call to `cf services` should list all four of these application services you provisioned. These services will now be available for use by the applications (which is good, because the microservices applications we're about to deploy depend upon them).

> Note: If this is your first time using PCF, notice how easy it is to create services via the marketplace. It's designed to be a low code, low friction experience focussed on developers.

### 3. Push Your JARs to the Pivotal Application Service

Now the environment is ready we can `cf push` our back-end microservice applications and get them running in the cloud. Again, a script has been provided to make this simple... 

```bash
# you need to provide this script with your PCF username, password, org and space...
$ ./push-microservices.sh USERNAME PASSWORD ORG SPACE

# When finished, you should see a list of your currently deployed apps and their URL's...

name                            requested state   instances  urls
esrefarch-demo-trader-app       started           1/1        esrefarch-demo-trader-app.cfapps.io
esrefarch-demo-trader-ui        started           1/1        esrefarch-demo-trader-ui.cfapps.io
```

> If you're new to PCF, notice also how easy it is to run your apps in the cloud. In a couple of minutes you'll have running services that will keep running no matter what.

Once the script has finished, you should notice output that contains the application URL's. Take note of the URL for the `esrefarch-demo-trader-app`, you'll need it for the next task.

### 4. Push the Axon Trader UI to Pivotal Application Service

Next we push the Node.js user interface to Pivotal Application Service, providing the URL of the `esrefarch-demo-trader-app` we discovered at the end of the last task. This will be used to link our UI to our backend. 

```bash
# you need to provide this script with your PCF username, password, org, space and trader-app-url ()...
$ ./push-ui.sh USERNAME PASSWORD ORG SPACE TRADER-APP-URL
```

### 5. Test the Axon Trader Application


By now, you should have a replica of the Axon Trader application running in your own PWS pr PAS space. This means we are ready to test it, as soon as we have the URL of the `trader-ui` application. You can discover the URL's for all your PWS application by calling `cf apps` from your logged in command line...

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

If the User interface is up, you can borrow the "run-all-tests" script to test the Axon Trader application and check that everything is working as expected. 

> Notice how we are setting environment variables for the `trader-app`, `trader-ui` and `trader-engine` before calling the `run-tests.sh` script...

```bash
cd ci
uiURL=https://esrefarch-demo-trader-ui.cfapps.io \
appURL=https://esrefarch-demo-trader-app.cfapps.io \
engineURL=https://esrefarch-demo-trading-engine.cfapps.io \
./run-tests.sh 
```

You should receive output similar to this when you run it...

````bash
The health check status is reporting that https://esrefarch-demo-trading-engine.cfapps.io is UP
TRADING-ENGINE SMOKE TEST FINISHED - ZERO ERRORS ;D

The API check status is reporting that there are 20 commands available.
TRADER-APP SMOKE TEST FINISHED - ZERO ERRORS ;D

The randomly generated Company Name for the e2e test is: 715DFBFB-37B6-44A5-8F59-77C31622E58E
The Company Payload for the e2e test is: {"companyName": "715DFBFB-37B6-44A5-8F59-77C31622E58E", "companyValue": "1337", "amountOfShares": "42"}
The Company UUID returned for the e2e test is: 193b11cb-0611-4a14-8e83-3f29226b5eeb
INTEGRATION TESTS FINISHED - NO ERRORS ;D
````

### 6. [Optional] Continuously Delivering the Axon Trader Application to PAS

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

# How Does It Actually Work?

## Architecture Overview

TODO: Need to describe the CQRS, Event Sourcing etc.


[1]: https://docs.run.pivotal.io/marketplace/services/cleardb.html
[2]: https://docs.run.pivotal.io/marketplace/services/cloudamqp.html
[3]: https://docs.pivotal.io/spring-cloud-services/2-0/common/config-server/index.html
[4]: https://docs.pivotal.io/spring-cloud-services/2-0/common/service-registry/index.html
[5]: https://esrefarch-demo-trader-ui.cfapps.io/
[6]: https://run.pivotal.io/
[7]: https://pivotal.io/pcf-dev
[8]: https://docs.cloudfoundry.org/cf-cli/install-go-cli.html
[9]: http://projects.spring.io/spring-boot/
[10]: http://www.axonframework.org/
[11]: https://pivotal.io/platform/pivotal-application-service
[12]: /images/AxonTrader-UI-001.png
[13]: https://concourse-ci.org/
[14]: https://wings.pivotal.io/teams/pcf-solutions-emea/pipelines/bw-esrefarch-demo
[15]: https://concourse-ci.org/fly.html
[16]: https://nodejs.org/en/
