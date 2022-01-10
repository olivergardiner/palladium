White Cottage Orbit Recipes
===========================

This project is used for building OSGI bundles that can be incorporated into Palladium
that are not available from elsewhere, notably Eclipse Orbit. This project uses the
build system of Eclipse Orbit copied form the Orbit git repo.

The only dependency not now available within Eclipse Orbit is Freemarker - this is
present in Eclipse Orbit but only an early version that does not have the required functionality.

This repository builds with Java 8 and Java 11

How to build all the bundles 
----------------------------

1. Clone this repository and go into the repository root folder.
2. `mvn clean install`
3. `mvn clean install -P aggregation`
4. `mvn clean package -P repository`

The repository will be made available as archive in `releng/repository/target`.

Note, you **must** build the recipes first and *install* the result into your local
Maven repository. Otherwise the p2 build won't find any bundles.

The Palladium tpd file will need to be modified to point to the correct location for 
the repository - this can't be made relative and so is dependent on your local setup
