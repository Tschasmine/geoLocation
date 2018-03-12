# GeoLocation
This is the source code of GeoLocation, an abas Essentials App build on the abas Essentials SDK.

## General setup
Add a gradle.properties file to your $GRADLE_USER_HOME.

```
#If you use a proxy add it here
systemProp.http.proxyHost=<webproxy>
systemProp.http.proxyPort=<port>
systemProp.https.proxyHost=<webproxy>
systemProp.https.proxyPort=<port>

nexusSnapshotURL=https://registry.abas.sh/repository/abas.snapshots
nexusReleaseURL=https://registry.abas.sh/repository/abas.releases
nexusUser=<extranet username>
nexusPassword=<extranet password>
PARTNER_USER=<extranet username>
PARTNER_PASSWORD=<extranet password>
```

Use your favorite IDE to import the project.

## Installation
To install the project make sure you are running the docker-compose.yml file or else change the gradle.properties file accordingly to use another erp client (you will still need a nexus server, but it can of course also be installed in your erp installation or elsewhere as long as it is configured in the gradle.properties file).

To use the project's docker-compose.yml file, in the project's root directory run:
```shell
docker login --username <extranet user> --password <extranet password> partner.registry.abas.sh
docker-compose up
```

Once it's up, initialize the gradle.properties with the appropriate values by running:
```shell
./initGradleProperties.sh
```

Now you can install the project as follows:
```shell
./gradlew fullInstall
```
## Development
If you want to make changes to the project before installing you still need to run the docker-compose.yml file or at least have a Nexus Server set up to work with.

Then run
```shell
./gradlew publishHomeDirJars
```

You also need to run
```shell
./gradlew publishClientDirJars
```
to upload the $MANDANTDIR/java/lib dependencies to the Nexus Server and set eclipse up to work with the uploaded dependencies.

After that the code should compile both with gradle and in intelliJ and you are set up to work on the code or resource files as you want.
