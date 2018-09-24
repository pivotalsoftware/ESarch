# Axon Trader

This project demonstrates the use of Command and Query Responsibility Segregation [CQRS][17] and [Event Sourcing][17] with [Pivotal Application Service][11] (a.k.a Pivotal Cloud Foundry or PAS). It consists of two [Spring Boot][9] microservices built using the open source [Axon Framework][10] and a user interface built using [Node.js][16] and Angular.

> __Note:__ If you just want to experience Axon Trader without having to build and host the code yourself, go ahead! Simply navigate your browser to [https://esrefarch-demo-trader-ui.cfapps.io/][5] but be sure to circle back if you're curious about how CQRS and Event Sorcing works in practice. 

## Before You Begin.

This demo will *only* run in a compatible [Pivotal Application Service][11] environment. This environment must also have a marketplace containing the required "brokered" services. Examples of compatible PAS environments would include [Pivotal Web Services (PWS)][6], any enterprise PCF foundation with the correct services, or locally on your PC using [PCF-Dev][7] (started with the required marketplace services running). 

> In this tutorial we will use Pivotal Web Services (PWS) as it meets all of the requirements set out above. If you don't have a PWS account yet, [you can sign up for one with some free credit here][6].

## Prerequisites

In order to follow along with this tutorial...

- You must have the [cf-cli][8] installed (ypu can test this test by calling `cf version` in your terminal).
- You must have access to a [Pivotal Application Service][11] instance.
- You must have marketplace services for MySQL, Rabbit, Spring Cloud Registry and Spring Cloud Config in PAS.
- You must have the `bash` terminal in order to run the provided scripts. 

# Building and Running the Axon Trader

Clone this repository using `git` in the usual way and `cd` to it's directory in your terminal window. There you'll notice there is a folder for the `trader-app` microservice, a folder for the `trading-engine` microservice and a folder for the `trader-ui`  user interface (amongst others).

> You don't have to explore the code in these folders in order to run the Axon Trader application, but you might like to take a look later.

To build and run the Axon Trader Reference application in your space on [Pivotal Web Services][6], read on. If you're using your enterprise instance of Pivotal Application Service the instructions are the same (as long as you have the required services).

### 1. Paving your Pivotal Application Service Environment

The Axon Trader Reference Architecture is built using Java (more specifically Spring Boot) and Node.js (bootstrapping an Angular single page application). To run, the Axon Trader applications need certain PAS arketplace Services to be available in the "space" where you will run the applications. These services are [ClearDB][1] (MySQL), [CloudAMQP][2] (RabbitMQ), [Spring Cloud Config][3] and [Spring Cloud Registry][4].

To pave your PWS space with these marketplace services, simply run the `pave.sh` script provided. I needs some details from you about your PWS space in order to run as illustrated below... 

```bash
# you need to provide this script with your PCF username, password, org and space...
./pave.sh <PWS-USERNAME> <PWS-PASSWORD> <PWS-ORG> <PWS-SPACE>
```

> Note: In the `pave.sh` script we have chosen only the __free__ PWS service plans for each of the marketplace services. These free plans won't cost you a penny to run but do have usage limitations (like a 7 day lifetime in the case of the Spring Cloud Services), so make yourself aware of these and make other choices as you feel appropriate.

Following the completion of the `pave.sh` script, a quick call to `cf services` in your terminal window should provide you with a list of all of the marketplace services you just created. These services will now be available for use by the Axon Trader applications (which is good, because the Axon Trader applications depend on them to run).

> Note: If this was your first time using the Pivotal Application Service, notice how easy it is to create services via the marketplace. It's designed to be a low code, low friction experience focussed on developers.

### 2. Building and Pushing the Axon Trader applications Pivotal Web Services

Now the PWS space is "paved" we can build and "push" our microservice applications and their user interface toit. Again, a script called `deploy-backend.sh` has been provided to save you a little typing... 

```bash
# you need to provide the deploy script with your PCF username, password, org and space...
$ ./deploy-backend.sh <PWS-USERNAME> <PWS-PASSWORD> <PWS-ORG> <PWS-SPACE>
```

> Notice how easy it is to run your apps in the cloud. In a couple of minutes you'll have running services with HTTP URL's accessible from anywhere that will keep running no matter what.

When the deploymet script has finished, you should see a list of your currently deployed apps and their URL's...

name                            requested state   instances  urls
esrefarch-demo-trader-app       started           1/1        esrefarch-demo-trader-app.cfapps.io
esrefarch-demo-trader-ui        started           1/1        esrefarch-demo-trader-ui.cfapps.io
```

Once the `deploy-backend.sh` script has finished it's work, you should notice output that contains the application URL's. Take note of the URL for the `esrefarch-demo-trader-app`, you'll need it for the next task.

### 3. Configuring the Axon Trader UI to Pivotal Application Service

Finally, the Node.js user interface for the Axon Trader UI needs to know where to find the newly pushed Axon Trader App backend. 

When we "pushed" the Axon Trader App to Pivital Web Services a few moments ago, it was assigned a unique and random URL. We now need to tell the Axon Trader UI this unique URL so that it can communicate with the backend.

TODO: Articulate how this is done.

```bash
cf app esrefarch-demo-trader-app
```

Copy the `URL` returned.

Open `blah.txt` in your favourite text editor for editing. Paste in the new setting for the `TODO` attribute and save the file. Now this configuration is set, we need to build and push the Axon Trader UI to PWS.

```bash
./deploy-frontend.sh <PWS-USERNAME> <PWS-PASSWORD> <PWS-ORG> <PWS-SPACE>
```

In a few minutes, the UI should have now have started and you will be in a position to test the application as desribed in the next section.

### 4. Test the Axon Trader Application

You should have a replica of the Axon Trader application running in your own PWS space. This means you are ready to test it. To begin, we need the Axon Trader UI URL. You can discover all the URL's for all your PWS application by calling `cf apps` from your command line...

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

If the User Interface is you can use the Axon Trader UI application and check that everything is working as expected.

You should be able to impersonate users, view the dashboard and the order books and place trades.

### 5. [Optional] Continuously Delivering the Axon Trader Application to PAS

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

### [Optional] Cleaning Up

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

You should see that each is empty. If not, use the PWS UI at [run.pivotal.io][18] to clear things manually or use the cf-cli `cf help -a" to find and execute the commands you need.

# How Does the Axon Trader Application Actually Work?

[TODO] Intro

## Architecture Overview

TODO: Need to describe the Axon CQRS, Event Sourcing etc.


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
[17]: cqrs-event-sourcing-article
[18]: https://run.pivotal.io