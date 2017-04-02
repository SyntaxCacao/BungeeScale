# BungeeScale
BungeeScale is a plugin to manage and scale servers in a BungeeCord network. Servers can be configured once and multiple instances can be started through ingame commands or an API. This solution is only suitable for networks whose servers are to run on a single machine.

## Usage

## Development

### Building
Since [Maven](https://maven.apache.org) is used for compiling and packaging, making a build is as easy as running `mvn clean package`. The final JAR file will be called `target/bungeescale-VERSION-jar-with-dependencies.jar` and is ready for use.

### Testing
As soon as you execute `mvn package`, the final package will be copied from the output directory to `test-server/plugins/`, so you can set up a testing environment in this directory.

There is no unit testing configured yet.

### API
If you wish to use BungeeScale's API in your plugin and if you are using maven for development, you'll need to add BungeeScale to your dependencies:

```
<dependency>
  <groupId>de.skeletoneye</groupId>
  <artifactId>bungeescale</artifactId>
  <version>1.0.0</version>
  <scope>provided</scope>
</dependency>
```

There is no maven repository containing BungeeScale publicly available yet, so you need to install it to your local repository manually by running `mvn install`.
