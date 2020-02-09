# ML Metadata

> **NOTES:**
>
> This code has been tested only with Java 8, Scala 2.12.10 and Python 3.7 (the default setting). Any other versions of Java will not work. Other versions of Scala 2.11 and 2.12 may work.

[Boris Lublinsky](mailto:boris.lublinsky@lightbend.com) and [Dean Wampler](mailto:dean.wampler@lightbend.com), [Lightbend](https://lightbend.com/lightbend-platform)

* [Strata Data Conference San Jose, Tuesday, March 16, 2020](https://conferences.oreilly.com/strata-data-ai/stai-ca/schedule/2020-03-16)

©Copyright 2020, Lightbend, Inc. Apache 2.0 License. Please use as you see fit, at your own risk, but attribution is requested.


See the companion presentation for the tutorial in the `presentation` folder

The tutorial contains 3 exersises:
* Serving Model as Data (tensorflow graph) leveraging [cloudflow](https://cloudflow.io/)
* Usage of the [MLflow](https://mlflow.org/) for capturing and viewing of model training metadata
* Creating a Model registry leveraging [Atlas](https://atlas.apache.org/#/)

## Model serving

We recommend reading cloudflow documentation and main concepts [here](https://cloudflow.io/)
For this exercise we will not install serving to the cluster, but run it local, using the following command
````
 sbt runLocal
````
this will print out the location of the log file. Run
````
tail -f <log location>
````
to see execution results

## MLflow

### Setup
Following [MLflow quick start](https://www.mlflow.org/docs/latest/quickstart.html), run
````
pip install mlflow
````
Additionally, the project contains a [docker file](/MLFlow/docker/Dockerfile) and required supporting 
script to build MLflow docker image, and a [Helm chart](/MLFlow/chart), installing an image in a 
kubernetes cluster, leveraging Minio for storing tracking results.

For this tutorial use local install.

### Running example
We provide both [notebook](/MLFlow/example/MLFlow.ipynb) and [Python](/MLFlow/example/MLFlow.py) versions of the
code implementing training and storing execution metadata.

### Viewing metadata
By default, wherever you run your program, the tracking API writes data into files into a local ./mlruns directory. You can then run MLflow’s Tracking UI:
````
mlflow ui
````
View results at 
````
http://localhost:5000
````

## Atlas

### Setup
For Atlas you can either use local setup, leveraging [bash file](Atlas/localinstall/install.sh)
or use a prebuild docker image 
````
lightbend/atlas:0.0.1
````
The image is build using the following [docker file](Atlas/docker/Dockerfile). In case you want to install
it to the kubernetes cluster, you can use the following [Helm chart](Atlas/chart).

### Building and Running example
An example code is located [here](/atlasclient). You can build it either directly
in Intellij or using SBT command:
````
sbt clean compile
````
Once the project is build:
* Use [ConnectivityTester](atlasclient/src/main/scala/com/lightbend/atlas/utils/ConnectivityTester.scala) to check
if you connect to cluster correctly and get the feel of how definitions look like in Atlas
* Use [ModelCreator](atlasclient/src/main/scala/com/lightbend/atlas/model/ModelCreator.scala) to create required types
and populate a simple model information

### Viewing results

Point your browser to
````
http://localhost:21000
````
and look at the Atlas UI 