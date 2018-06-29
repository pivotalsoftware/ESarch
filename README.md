# CQRS and Event Sourcing on Cloud Foundry

This project demonstrates the the use of CQRS and Event Sourcing with Cloud Foundry. It contains two [Spring Boot](http://projects.spring.io/spring-boot/) microservices, both built using components provided by the [Axon Framework](http://www.axonframework.org/). You can read the [blog post that accompanies this project over on Wordpress](https://benwilcock.wordpress.com/2017/07/11/cqrs-and-event-sourcing-microservices-on-cloudfoundry/).

There are two Spring Boot applications here because the [*command-and-query-responsibility-segragation*](https://martinfowler.com/bliki/CQRS.html) pattern has been implemented quite literally in the code. We have physically separated the *command-side* microservice from the *query-side* microservice. They could even be in separate Git repo's if we wanted to, but we have kept them together for ease of understanding and maintenance. 

This demo project was inspired by a webinar given by [Josh Long](https://twitter.com/starbuxman) of Pivotal and [Allard Buijze](https://twitter.com/allardbz) of Trifork. [You can view the whole thing on Youtube](https://youtu.be/Jp-rW-XOYzA).

> **This CQRS application is designed to run exclusively on Cloud Foundry.**

> **This CQRS application contains only RESTful services and API's - no UI.**

## Architecture Overview

The following diagram illustrates how these microservices are architected. Notice how because we're using Event Driven Architecture, it's easy (and expected) that you would ultimately have lots of different views, projections, reports and legacy adaptors in order to help you maintain the flexibility of your microservice architecture without it turning back into a monolith.

![Architecture Overview - CQRS & Event Sourcing on CloudFoundry](/images/CQRS+EventSourcing-on-CloudFoundry.png)

## Before You Begin.

This demo will *only* run in a Cloud Foundry environment such as [Pivotal Web Services (PWS)](https://run.pivotal.io/) or [PCF-Dev](https://pivotal.io/pcf-dev). If you don't have a PWS account, [you can sign up for free](https://run.pivotal.io). If you would prefer to use PCF-Dev on your local machine rather than the cloud, read on to the end to find out what else you'll need to do.

First things first, clone this repository and go to it's directory in your terminal window. You'll notice there is a folder for the `command-side` project and a folder for the `query-side` project. You don't have to explore these folders to run the demo, but you might like to later after you're done here.

## Comission Your Cloud Foundry Space.

This demo needs certain CloudFoundry marketplace services to be available in the space where you want to `cf push` the applications. The services required are MySQL, RabbitMQ, Spring Cloud Config and Spring Cloud Registry. 

To set up these marketplace services, login to CloudFoundry with your local CloudFoundry CLI client. [Download the the 'cf-cli' and install it if you havn't used it before](https://github.com/cloudfoundry/cli).

````bash
$ cf login -a api.run.pivotal.io
````

When logging in, you'll need your account username and password. Be sure to choose an 'Org' and 'Space' with ample quota.

Once you're logged in, you can setup the supporting services required for the applications. Use the commands below to instantly self-provision backing services for *MySQL*, *RabbitMQ*, *Spring Cloud Registry* and *Spring Cloud Config*. Note that the Config server will need to know where to get it's configuration files. A setup file has been provided in the root of this project that contains [the Github location of the configuration required.](https://github.com/benwilcock/app-config)

````bash
$ cf create-service cleardb spark mysql
$ cf create-service cloudamqp lemur rabbit
$ cf create-service p-service-registry standard registry
$ cf create-service p-config-server standard config -c config-server-setup.json
````

Following this step, a quick call to `cf services` should list all four of these application services you provisioned (rabbit, mysql, registry and config). These services will now be available for use by the applications in the targeted space (which is good, because the microservice applications depend on them).

## Build & Deploy The Microservices.

Now that's done we're ready to compile the code and push the resulting Java JAR to CloudFoundry. From the project root folder, use Gradle Wrapper to build the code and the cf-cli to push the code to CloudFoundry as follows...

````bash
$ ./gradlew build
$ cf push
````

This will compile and push two seperate application JAR files using a CloudFoundry `manifest.yml` that has also been provided. Once deployed to Cloud Foundry, the **command-side** application deals with the *commands* in the domain and emits *events*, and the **query-side** application listens for events and builds a read-only *view* (sometimes called a *projection*) based on the events it receives.

Once the applications are pushed, you can list the apps to find out their URL's. Make a note of them, you'll need these URL's when integration testing the application in the next section.

````bash
$ cf apps

name                               requested state   instances   memory   disk   urls
pcf-axon-cqrs-demo-command-side    started           1/1         1G       1G     pcf-axon-cqrs-demo-command-side.cfapps.io
pcf-axon-cqrs-demo-query-side      started           1/1         1G       1G     pcf-axon-cqrs-demo-query-side.cfapps.io
````

## Integration Testing The Microservices.

To make this easy, we can simply use curl command to test that the *command-side* and *query-side* applications are collaborating together as intended. A bash script has been provided to make this straightforward. This script assumes Mac OS X, but with a small mod you can use it on Linux too.

````bash
$ ./addProductToCatalog.sh
````

This script creates a unique product UUID and POST's a **New Product Command** (a JSON string) to the *command-side* application's `/add`. The script then queries the *query-side* application's `/products` endpoint to check if the new Product we added is in the list. 

> Notes: This is not guaranteed to happen instantly (often termed "eventual consistency"), so you may need to be patient between your POST and GET requests. One second should normally do it. There can be many different 'views' in a CQRS application, hence why the `/add` and `/products` endpoints are on separate services.

# How Does It Actually Work?

Following our first `curl` command (to the command side)...

1. The command is received by the command-side and then processed by Axon using a new *AggregateRoot* (the term *AggregateRoot* comes from Domain Driven Design, for which CQRS and Event Sourcing are an excellent match!). 

2. The AggregateRoot performs the command and then emits an *Event*.

3. The Event is stored in the *Event Store* (which uses MySQL) anf then gets published to 'out of process' listeners (using RabbitMQ).

4. The query-side *Listener* receives the event and stores a new Product in it's Product view table (also in MySQL, but in a completely seperate Table, Schema and even DB if desired).

Following our second `curl` command (to the query-side)...

1. The Product view would have been queried for a list of products to display.

2. The list of Procuct are sent back to the caller in JSON format.

> Note: Spring Data REST Repositories are being used for the query-side implmentation, so you'll find very little boilerplate code in that part of the project.


# Running locally using PCF-Dev

If you have a development machine with the necessary hardware requirements, you can also run this demo locally using PCF-Dev on your PC. If you don't yet have PCF-Dev, [download it and set it up using these instructions](https://pivotal.io/pcf-dev). 

If you decide to use PCF-Dev, here are some differences to take into account:-

1. You'll need to start PCF-Dev with Spring Cloud Services available (`cf dev start -s all`).

2. You'll need to login using the PCF-dev endpoint and skipping SSL validation (`cf login -a https://api.local.pcfdev.io --skip-ssl-validation`).

3. You'll need to modify the script that creates the services so that you use the correct service names and plans for PCF-Dev (as these have different names from those in the PWS marketplace).

4. The URL's of the testing endpoints will be different.

Other than these points, the instructions are broadly the same.

# Bonus Material

### Useful CQRS related stuff.

**To Read...**

For a well written primer on CQRS, Event Sourcing and Domain Driven Design, check out Vaughn Vernon's excellent book - [Domain Driven Design Distilled](https://www.amazon.co.uk/Domain-Driven-Design-Distilled-Vaughn-Vernon/dp/0134434420/ref=sr_1_7?s=books&ie=UTF8&qid=1499356762&sr=1-7&keywords=domain+driven+design). Alternatively, you can download Microsoft's excellent FREE eBook - [A CQRS Journey - Exploring CQRS and Event Sourcing.](https://www.microsoft.com/en-us/download/details.aspx?id=34774)

**To Watch...**

You can find out what Greg Young thinks of the Axon Framework, Kafka and the overall state of Event Sourcing in the JVM by [watching his presentation from QCon.](https://www.infoq.com/presentations/event-sourcing-jvm) You can also watch the talk that inspired this project - ['Bootiful CQRS' on Youtube.](https://youtu.be/Jp-rW-XOYzA)

### Microservices Continuous Delivery Pipeline

This project includes a simple continuous delivery pipeline that uses [Concourse](http://concourse.ci/). The Concourse pipeline can be found in the `ci` folder. To use it, you'll need a [Concourse server](http://concourse.ci/), and the associated `fly` cli tool. When pushing the pipeline you'll also need a second file (`private.yml`) with your own private configuration parameters as follows...

````yml
cf-cmd-app-url: <your command-side app URL>
cf-qry-app-url: <your query-side app URL>
cf-endpoint: <your cf-api endpoint>
cf-user: <your cf username>
cf-password: <your cf password>
cf-org: <your cf org name>
cf-space: <your cf space name>
skip-ssl: <whther you should login with ssl (true|false)>
````

### Code Credits

[Ben Wilcock](https://benwilcock.wordpress.com) - code.

[Aurora Mirea](https://github.com/auramirea) - code.

[Josh Long](https://twitter.com/starbuxman) - webinar.

[Allard Buijze](https://twitter.com/allardbz) - webinar.
