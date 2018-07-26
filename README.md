# CQRS and Event Sourcing on Cloud Foundry

This project demonstrates the the use of CQRS and Event Sourcing with Cloud Foundry. It contains two [Spring Boot](http://projects.spring.io/spring-boot/) microservices, both built using components provided by the [Axon Framework](http://www.axonframework.org/). You can read the [blog post that accompanies this project over on Wordpress](https://benwilcock.wordpress.com/2017/07/11/cqrs-and-event-sourcing-microservices-on-cloudfoundry/).

> **This CQRS application is designed to run exclusively on Cloud Foundry.**

## Architecture Overview

![Architecture Overview - CQRS & Event Sourcing on CloudFoundry](/images/CQRS+EventSourcing-on-CloudFoundry.jpg)

## Before You Begin.

This demo will *only* run in a Cloud Foundry environment such as [Pivotal Web Services (PWS)](https://run.pivotal.io/) or [PCF-Dev](https://pivotal.io/pcf-dev). If you don't have a PWS account, [you can sign up for free](https://run.pivotal.io). If you would prefer to use PCF-Dev on your local machine rather than the cloud, read on to the end to find out what else you'll need to do.

First things first, clone this repository and go to it's directory in your terminal window. You'll notice there is a folder for the `command-side` project and a folder for the `query-side` project. You don't have to explore these folders to run the demo, but you might like to later after you're done here.

## Comission Your Cloud Foundry Space.

This demo needs certain CloudFoundry marketplace services to be available in the space where you want to `cf push` the applications. The services required are MySQL, RabbitMQ, Spring Cloud Config and Spring Cloud Registry. 

Following this step, a quick call to `cf services` should list all four of these application services you provisioned (rabbit, mysql, registry and config). These services will now be available for use by the applications in the targeted space (which is good, because the microservice applications depend on them).

## Build & Deploy The Microservices.

Now that's done we're ready to compile the code and push the resulting Java JAR to CloudFoundry. From the project root folder, use Gradle Wrapper to build the code and the cf-cli to push the code to CloudFoundry as follows...

````bash
$ ./mvnw verify
$ cf push
````

This will compile and push two seperate application JAR files using a CloudFoundry `manifest.yml` that has also been provided. Once deployed to Cloud Foundry, the **command-side** application deals with the *commands* in the domain and emits *events*, and the **query-side** application listens for events and builds a read-only *view* (sometimes called a *projection*) based on the events it receives.

Once the applications are pushed, you can list the apps to find out their URL's. Make a note of them, you'll need these URL's when integration testing the application in the next section.

````bash
$ cf apps

name                               requested state   instances   memory   disk   urls
pcf-axon-cqrs-demo-trading-engine    started           1/1         1G       1G     pcf-axon-cqrs-demo-trading-engine.cfapps.io
pcf-axon-cqrs-demo-trader-app        started           1/1         1G       1G     pcf-axon-cqrs-demo-trader-app.cfapps.io
````

## E2E Testing The Microservices.

To make this easy, we can simply use curl command to test that the *command-side* and *query-side* applications are collaborating together as intended. A bash script has been provided to make this straightforward. This script assumes Mac OS X, but with a small mod you can use it on Linux too.

````bash
$ ./ci/run-tests.sh
````

# How Does It Actually Work?
