# BungeeScale
BungeeScale is a plugin to manage and scale servers in a BungeeCord network. This solution is only suitable for networks whose servers are to run on a single machine.

BungeeScale can be easily configured to launch a fixed number of instances per image when BungeeCord boots and to start a new instance as soon as another one terminates, but you need to contact it's API in order to make it actually scale, e.g. to launch a new instance as soon as another one starts a game.

## Usage

### Installation
Download the latest version of BungeeScale and move it into your BungeeCord's `plugins` directory. After running the server once, a new directory named `servers` should be created in BungeeCord's root directory. Make sure to never change anything inside this directory while BungeeCord is running.

Next, move an executable JAR file (e.g. `spigot-X.X.X.jar`) to `servers/images/driver.jar` with which all of your instances are going to be launched. Have a look into `servers/network.yml` if you want to modify the command which is used to launch an instances. Now, you are ready to create your first image.

### Creating an image
1. Create a new directory for your image inside the `servers/images` directory.
1. Start and stop BungeeCord once. You'll discover a file named `image.yml` inside your new directory.
1. Move all the files (such as configuration files, plugins, worlds, ...) for your new image into your new directory.
1. Open up the `image.yml` file and change a few options. all values are preceded by comments explaining their meanings. Make sure to change the `name` setting and to set `enabled` to `true` if you want your image to be actually loaded.

That's it; Your image should be loaded the next time you run BungeeCord. You'll be able to discover the launched instances using BungeeCord's `/glist` command.

## Development
You're welcome to help in development by opening a pull request, but please make sure to create an issue beforehand, so that your proposed changes can be discussed *before* you start working.

### Building
Since [Maven](https://maven.apache.org) is used for compiling and packaging, making a build is as easy as running `mvn clean package`. The final JAR file will be called `target/bungeescale-VERSION-jar-with-dependencies.jar` and is ready for use.

### Testing
As soon as you run `mvn package`, the packaged JAR file will be copied from the output directory to `test-server/plugins/`, so you can set up a testing environment in this directory.

There is no unit testing configured yet.

### API
If you wish to use BungeeScale's API in your plugin and if you are using maven for development, you'll need to add BungeeScale to your dependencies:

```
<dependency>
  <groupId>de.skeletoneye</groupId>
  <artifactId>bungeescale</artifactId>
  <version>0.2.0</version>
  <scope>provided</scope>
</dependency>
```

There is no maven repository containing BungeeScale publicly available yet, so you need to install it to your local repository manually by running `mvn install`.
