# nem2-camel

:warning: nem2-camel is in experimental stage.

Starts a server that listens the catapult-rest calls and acts as a proxy.
When it detects a transaction announce, it waits for the confirmation via WebSockets and return the message to the HTTP call.

In summary, it turns the asynchronous API to announce transactions into synchronous.

It allows you use the [TransactionHttp.announceSync(_)](https://nemtech.github.io/nem2-sdk-typescript-javascript/classes/_infrastructure_transactionhttp_.transactionhttp.html#announcesync) method from [nem2-sdks](https://nemtech.github.io/sdk/overview.html#).

:warning: It does not forward the WebSockets.

## Download

- [v0.1](https://github.com/nemtech/nem2-camel/releases/tag/v0.1)

## Dependencies

[SBT][sbt]: Scala Build Tool v1.0

## Run

```bash
$> sbt run
```

It starts the [Apache Camel][apache-camel] server.

## Build

```bash
$> sbt assembly
```

It generates a `nem2-camel.jar` file under `./target/scala-2.12` folder.

## Usage

`java -jar nem2-camel.jar` to run it with default options

* -help. prints help message

`java -jar nem2-camel.jar --help`

* -url. sets a custom catapult node

`java -jar nem2-camel.jar --url http://localhost:3000`

* -accountsFile. uses a custom set of accounts and their channel to subscribe. Example:

`java -jar nem2-camel.jar --accountsFile ./accounts.csv`

### Accounts File Format

A CSV file with the address to subscribe followed by the channels to subscribe to

`SDRDGFTDLLCB67D4HPGIMIHPNSRYRJRT7DOBGWZY,status,unconfirmedAdded`

[sbt]: http://www.scala-sbt.org/
[apache-camel]: https://camel.apache.org