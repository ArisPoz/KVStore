# KVStore (Key-Value DataStore)
A simple version of a distributed, fault-tolerant key-value datastore with a Trie implementation.

* Kotlin
* Maven
* Ktor (Netty)
* OKHttp3
* Websockets
* Coroutines

## Guidelines:
### KVServer
The key-value server starts at a specified IP address and port and waits for commands.

#### Installation and execution
`mvn clean install`

`java -jar target/KVServer.jar -a IP -p PORT`

#### CLI usage
Options | Value | Description
--- | --- | --- |
-a | IP | The specified IP address of the server
-p | PORT | The specified Port of the server
-h | - | Show this help message and exit.
-V | - | Print version information and exit.

### KVBroker
The broker connects to multiple servers and sends query commands as GET, QUERY, PUT and DELETE.

#### Installation and execution
`mvn clean install`

`java -jar target/KVBroker.jar -i Data.txt -s Servers.txt -k 2`

#### CLI usage
Options | Value | Description
--- | --- | --- |
-i | dataToIndex.txt | File containing available server IPs
-k | replicationFactor | Replication factor. The # of server having identical data
-s | serverFile.txt | File containing generated data
-h | - | Show this help message and exit.
-V | _ | Print version information and exit.


### DataCreation
Generates random key-value pairs, to feed them into the servers, so we can have data to test the application.

`mvn clean install`

`java -jar target/DataCreation.jar -k keyFile.txt -d 3 -m 2 -l 5 -n 2000`

#### CLI usage
Options | Value | Description | Default
--- | --- | --- | --- |
-d | Max Nesting Level |  Maximum nesting level, '0' for no nesting | Default: 3
-l | Max String Length |  Maximum length of a string value | Default: 4
-m | Max Keys Num |  Maximum number of keys inside each value. | Default: 5
-n | Num of Lines |  Number of lines that we would like to generate | Default: 1000
-k | Key File |  File containing a space-separated list of key names and their data types | -
-h | - | Show this help message and exit. | -
-V | - | Print version information and exit. | -
