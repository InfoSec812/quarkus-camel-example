# camel-quarkus-example Project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Prerequisites
* OpenJDK >= 11
* [Twitter API Credentials](https://developer.twitter.com/en/portal/projects-and-apps) in a file `${HOME}/.twitter.properties`
  * Fromatting of the file should be: 
  ```
  twitter.access.token=000000000-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
  twitter.access.secret=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
  twitter.consumer.key=xxxxxxxxxxxxxxxxxxxxxxxxx
  twitter.consumer.secret=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
  ```

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

## Add Twitter and logging components by uncommenting them in `pom.xml`

```xml
<!--    <dependency>-->
<!--      <groupId>org.apache.camel.quarkus</groupId>-->
<!--      <artifactId>camel-quarkus-twitter</artifactId>-->
<!--    </dependency>-->
<!--    <dependency>-->
<!--      <groupId>org.apache.camel.quarkus</groupId>-->
<!--      <artifactId>camel-quarkus-log</artifactId>-->
<!--    </dependency>-->
```

## Use twitter component in Camel context for `TwitterIngestRoute`

```java
		fromF("twitter-search:%s?greedy=true&type=direct&delay=%s", searchTerms, twitterPollDelay)
				.to("log:rawTweet");
```

## Add twitter credentials to `src/main/resources/application.properties`

```
camel.component.twitter-search.access-token=xxxxxxxx-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
camel.component.twitter-search.access-token-secret=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
camel.component.twitter-search.consumer-key=xxxxxxxxxxxxxxxxxxxxxxxxx
camel.component.twitter-search.consumer-secret=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

## Note that since we are already running in Dev mode that as we changed these files the application automatically reloaded our changes. After this most recent change with the Twitter credentials we should start seeing tweets coming through.



> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/camel-example-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Related Guides

- Camel Core ([guide](https://camel.apache.org/camel-quarkus/latest/reference/extensions/core.html)): Camel core functionality and basic Camel languages: Constant, ExchangeProperty, Header, Ref, Simple and Tokenize
