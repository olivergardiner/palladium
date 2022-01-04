Palladium
---------

Palladium is an Eclipse plugin that complements the Papyrus UML platform by adding additional plugins specific to the management of logical data models expressed in UML. These extensions range from simple shortcuts to streamline the process of data modelling through to processing the models to generate additional artefacts such as schemas and documentation.

Overall, the environment for using the Papyrus/Palladium tooling for the PLDM can be seen as being built from the following layers:

* Palladium
* Papyrus Compare
* Papyrus
* Eclipse UML2 (a dependency of Papyrus)
* Eclipse EMF (a dependency of UML2)
* Eclipse Platform
The base Papyrus platform (the bottom 4 layers) can either be the Papyrus RCP or any other Eclipse package with Papyrus installed.

For the development of Palladium, the following plugins are recommended:
* Sonarlint (Eclipse Marketplace)
* Checkstyle (Eclipse Marketplace)
* Window Builder (Eclipse Marketplace)
* Freemarker IDE (Eclipse Marketplace)
* Target Platform Definition DSL and Generator (https://github.com/eclipse-cbi/targetplatform-dsl)

Palladium is built using Maven and Tycho - for a typical build, simply go to the project folder for uk.org.whitecottage.palladium and execute `mvn clean verify -P rcp`

Several profiles are supported:

* `rcp` - builds the RCP application using the latest released version of Papyrus
* `rcp-nghtly` - builds the RCP application using the latest development version of Papyrus
* `site` - builds the Palladium plugin repository

## Palladium project structure
Palladium itself has a number of components that are managed as individual Eclipse projects:

* uk.org.whitecottage.palladium  - this is the main plugin project for the code that adds new functionality
* uk.org.whitecottage.palladium.feature - this is simply a wrapper project that creates an Eclipse Feature (a related collection of plugins) for Palladium so that an update site can be created for distribution
* org.eclipse.papyrus.uml.tools.patch - this is a wrapper project that creates an Eclipse Feature Patch that allows us to replace specific plugins or features in Papyrus to alter existing behaviour (NB: Not built using Tycho because of a Tycho issue)
* org.eclipse.papyrus.uml.tools – this is a clone of the Papyrus plugin and is unchanged but needed to provide a complete replacement for the org.eclipse.papyrus.uml.tools feature
* org.eclipse.papyrus.uml.tools.utils – this is a modified copy of the corresponding Papyrus plugin to alter the display of Association Cardinality

Palladium also uses a number of external libraries that are not available by default in Eclipse and so must also be included. All of these are available as Maven artefacts but, as Eclipse uses OSGI for its dependency management, there are additional wrapper projects that create OSGI bundles from Maven artefacts. The current list of external dependencies is:

* Apache POI - for writing OOXML documents
* Freemarker - a templating system for generating HTML files
* Jackson - for creating JSON (and hence Swagger) documents
* Apache XML Schema - for creating XML Schema documents

## Common tasks

### Updating the Palladium version
Strictly we could consider an independent versioning scheme for both the Palladium plugin and the associated RCP but, in practice, it is simpler to keep them in step as it is always helpful to ensure that the workspace is updated when there is a new version of the plugin (which requires the RCP version to be updated).

The following files need to be updated in the `uk.org.whitecottage.palladium` project:
* `releng/uk.org.whitecottage.palladium.product/palladium.product` (use the default editor to set the version in the `Overview` tab
* `plugins/uk.org.whitecottage.palladium/plugin.xml` (use the default editor to set the version in the `Overview` tab)
* `plugins/uk.org.whitecottage.palladium/pom.xml` (set the <version> tag for the maven artefact - not the parent POM)

NB: The version numbers in `plugin.xml` files should have the suffix `.qualifier` and in the POM files they should have the suffix `-SNAPSHOT`

### Tracking the Eclipse release train
Eclipse currently follows a simultaneous release cycle that is quarterly. To ensure that the Palladium RCP references the most up to date version of Eclipse & Papyrus, the following steps need to be taken:
* Update the "build" repositories in `releng/pom.xml`
* Update the "update" repositories in `releng/rcp/uk.org.whitecottage.palladium.rcp.product/palladium.product`

Ensure that the repositories are updated in all relevant profiles (`rcp-nightly` shouldn't need to change) - in the profile-dependent sections the only repo that should need an update is `papyrus` and in the common repo definitions it should be just `eclipse-releases` and `eclipse`.

In line with Papyrus practice, it is recommended that each quarterly update should increment the minor version number.



