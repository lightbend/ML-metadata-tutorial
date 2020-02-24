# ML Metadata

> **NOTE:** This code has been built and tested only with the following tools:
>
> 1. Java 8 and 11 (but see note in the Apache Atlas section below)
> 2. Scala 2.12.10 (this is handled internally by the build process; no need to install anything)
> 3. Python 3.7 (although newer versions may work)
> 4. Docker (recommended for the Apache Atlas example)

Any other versions of Java will not work. Other versions of Scala 2.11 and 2.12 may work. To build Atlas as described below, you will _also_ need Python 2 available to run the Atlas administration scripts, which are not compatible with Python 3. However, we provide a Docker image to recommend its use instead).

* [Boris Lublinsky - Lightbend](mailto:boris.lublinsky@lightbend.com): See [Lightbend Platform](https://lightbend.com/lightbend-platform)
* [Dean Wampler - Anyscale](mailto:dean@anyscale.io): See [Anyscale](https://anyscale.io) and [Ray](https://ray.io)

[Strata Data Conference San Jose, Tuesday, March 16, 2020](https://conferences.oreilly.com/strata-data-ai/stai-ca/schedule/2020-03-16)

©Copyright 2020, Lightbend, Inc. Apache 2.0 License. Please use as you see fit, at your own risk, but attribution is requested.

See the companion presentation for the tutorial in the `presentation` folder

The tutorial contains 3 exercises:
* Serving models as data (TensorFlow graph) leveraging [Cloudflow](https://cloudflow.io/).
* Using [MLflow](https://mlflow.org/) to capture and view model training metadata.
* Creating a model registry using [Apache Atlas](https://atlas.apache.org/#/).

## Install SBT

Several of the examples use components built with [Scala](https://scala-lang.org/). To build and run these examples, [install the build tool `sbt`](https://www.scala-sbt.org/download.html). The rest of the dependencies will be downloaded automatically.

## Install Python, Pip, and Libraries

The MLflow example uses [Python](https://www.python.org/) and [Pip](https://pip.pypa.io/en/stable/) to install the components. Python 3 is required.

> Python 3 is **not** the default version installed on Mac OS. You have two options:
1. Install Python 3 using the [Homebrew](https://brew.sh/) package manager, `brew install python`. In this case, when you see the `pip` command below, use `pip3` instead.
2. [Install Anaconda](https://www.anaconda.com/distribution/#download-section) and create a environment for this tutorial.

While it can be a little more work to set up, we recommend using [Anaconda](https://www.anaconda.com/distribution/#download-section) for MacOS, Windows, and Linux, especially if you plan to do a lot of work with Python-based tools. By providing isolated environments, it helps avoid conflicting dependency problems, provide isolation when you need to switch between multiple versions of packages (e.g., for testing), and other benefits.

Once Python 3 and `pip` (or `pip3`) are installed, run the following command to install MLflow and dependencies:

```
pip install -r MLflow/requirements.txt --upgrade
```

## Overview of the SBT Project

This tutorial is an `sbt` project that's used for two of the three examples:

1. Model serving with cloudflow
3. A model registry with Apache Atlas

Both use the `sbt` build to compile and run the supplied application code. So, here is a "crash course" on interactive sessions with `sbt`. Here `$` is the shell prompt for `bash`, Windows CMD, or whatever (don't type it) and `sbt:ML Learning tutorial>` is the interactive prompt for `sbt`:

```
$ sbt
... initialization messages ...
sbt:ML Learning tutorial> projects
...
[info] 	   atlasclient
[info] 	 * ml-metadata-tutorial
[info] 	   tensorflowakka
sbt:ML Learning tutorial>
```

The `*` indicates we are currently using the top-level project for the tutorial. The `atlasclient` is a program for interacting with an Apache Atlas server and `TensorFlowAkka` uses [Cloudflow's Akka API](https://cloudflow.io) to demonstrate serving models in a microservice-like context, as we explain in the presentation slides.

To work with one of the projects, for example `tensorflowakka` (the first one we'll try), use the `project` command, as follows. Note that the prompt will change:

```
sbt:ML Learning tutorial> project tensorflowakka
sbt:TensorFlow-akka>
```

When we tell you to use some variation of a `run` command, it will automatically download all required libraries and build the code first. You could do the compilation step separately, if you like: `compile`. Similarly, you can compile the code _and_ compile and run the tests using `test`.

> **Pro Tip:** If you are editing code and you want `sbt` to continually compile it every time you save a file, put a `~` in front: `~compile` or `~test`.

## Model Serving with Cloudflow

This example uses the Akka Streams API in [Cloudflow](https://cloudflow.io/). We won't explain a lot of details about how Cloudflow works, see the [Cloudflow documentation(https://cloudflow.io/docs/current/index.html)] for an introduction and detailed explanations.

Clouflow applications are designed to be tested locally and executed in a cluster for production. For this exercise, we will not install the serving example to a cluster, but run it locally, using `sbt`.

Start the `sbt` interpreter and use the following `sbt` commands from the project root directory:

````
sbt:ML Learning tutorial> project tensorflowakka
sbt:tensorflow-akka> runLocal
````

This will print out the location of the log file. (Look for the `... --- Output --- ...` line.) On MacOS or Linux systems, use the following command to see the entries as they are written:

````
tail -f <log_location>
````

On Windows, use the command `more < <log_location>`, but it stops as soon as it has read the current last line, so you'll need to repeat the command several times as new output is written to the file.

Terminate the example by pressing the Enter key in the `sbt` window.

> **Pro Tips:**
>
> 1. In some MacOS and Linux shells, you can "command-click", "control-click", or right click on the file path in the text output to open it in a console window for easy browsing.
> 2. Actually, you don't need to switch to `project tensorflowakka` before invoking `runLocal`. You could just invoke `runLocal` in the top-level `ml-metadata-tutorial` project. However, we switched to `tensorflowakka` first so it's clear which one we're actually using.


## Model Training with MLflow

We'll train some models using [scikit-learn](https://scikit-learn.org/stable/) and track those training runs in MLflow. We'll use the MLflow GUI to examine the data.

### Setup

Follow the instructions above to install MLflow and other dependencies for this example using `pip`. For reference, the [MLflow quick start](https://www.mlflow.org/docs/latest/quickstart.html) provides additional information.

Additionally, this tutorial project contains a [Dockerfile](/MLflow/docker/Dockerfile) and supporting scripts to build an MLflow docker image. There is a [Helm chart](/MLflow/chart) for installing the image in a Kubernetes cluster, leveraging Minio for storing tracking results.

For this tutorial, we will use local installation.

### Running the Example

We provide both [notebook](/MLflow/example/MLflow.ipynb) and [Python](/MLflow/example/MLflow.py) versions of the code implementing training and storing execution metadata.

To run the notebook version, install Jupyter using `pip` (or `pip3`):

```
pip install jupyter
```

Then change to the `MLflow` directory and run:

```
jupyter notebook
```

Click on the `MLflow.ipynb` link to open the notebook.

The same code is in the Python script, `MLflow.py` in the `MLflow/example` directory. You can run it with the command:

```
python MLflow.py
```

> **Note:** If you get an exception about a "key error" for `metrics.rmse` on line `df_runs.sort_values(["metrics.rmse"], ascending = True, inplace = True)
`. It may be that the experiment number actually used was 1 instead of 0. Change `df_runs = mlflow.search_runs(experiment_ids="0")` to use 1 instead of 0 and try again.

### Viewing Metadata

By default, wherever you run your program, the tracking API writes data into files in a local ./mlruns directory. You can then run MLflow’s Tracking UI to view them:

````
mlflow ui
````

View results at

````
http://localhost:5000
````

## A Model Registry with Apache Atlas

### Setup

For Atlas you can either build and run Apache Atlas locally or use a prebuilt Docker image `lightbend/atlas:0.0.1`. The latter is _much_ is easier, as we'll see, but it requires you to have Docker installed on your laptop.

If you want to install Atlas locally, try the following at home! It will take too long to do this during the tutorial and it will consume what battery reserve you have left.

[This bash file](Atlas/localinstall/install.sh) downloads, builds, runs Atlas to confirm it's working, then shuts it down.

The following tools are required for this script to work:

* Java 8 (`JAVA_HOME` must be defined) - Newer versions of Java will not work, because of annotations that were removed from the JDK, which Atlas uses.
* Maven - the `mvn` command.
* `wget` or `curl` - However, if both are missing, the script tells you what to do as a workaround.
* Python 2 - While you need Python 3 for the rest of the tutorial, the admin scripts for Atlas are old and require Python 2. See _troubleshooting_ below for more details.

It's best to change to the `localinstall` directory and then run `./install.sh`.

> **Toubleshooting:**
>
> 1. Verify you are using Java 8; annotations used by the Atlas code were apparently deprecated and removed in _either_ later versions of the JDK _or_ some library dependency that is JDK version-specific.
> 2. If the Maven build fails with an error that an `slf4j` dependency can't be resolved, look closely at the error message and see if it complains that accessing the repo `http://repo1.maven.org/maven2` requires HTTPS. If so, edit line 793 in the downloaded `pom.xml` file for Atlas, change `http` to `https`.
> 3. If Atlas builds, but you get an error running the `atlas_start.py` script, it's probably because the Python 3 installation you're using for the rest of this tutorial is not compatible with the script, which expects Python 2 :(

If you encounter the last issue, you may also have Python 2 on your laptop. For MacOS, the built-in version of Python, `/usr/bin/python`, is version 2.7.X. If you are on Windows or Linux, your machine may also have Python 2 installed somewhere.

So, MacOS users can run the following commands:

```
cd apache-atlas-sources-2.0.0
cd distro/target/apache-atlas-2.0.0-server/apache-atlas-2.0.0/
/usr/bin/python bin/atlas_start.py
```

> **NOTE:** _If you get this far_, be patient, as it takes a while for Atlas to start up. You'll see dots printed while it's initializing. It's ready when you see _Apache Atlas Server started!!!_

As you can see, it's not easy to reliably build and run Atlas. Hence, we strongly recommend using the Docker image for the tutorial. For reference, the Docker image was built using [this Dockerfile](Atlas/docker/Dockerfile). In case you want to install it to a Kubernetes cluster, you can use [this Helm chart](Atlas/chart).

To run the Docker image, use the following command in a separate command window:

````
docker run -p 21000:21000 --rm -it lightbend/atlas:0.0.1
````

> **NOTE:** Be patient, as it takes a while for the container to finish start up. You'll see dots printed while it's initializing. It's ready when you see _Apache Atlas Server started!!!_

### Building and Running the Atlas Client Example

An example of using Atlas is located in [./AtlasClient](AtlasClient). You can build it either by loading this directory as a Scala project in your IDE or using this SBT commands from the project root directory:

````
sbt:ML Learning tutorial> project atlasclient
sbt clean compile
````

Once the project is built, there are two applications you can run:
* [`ConnectivityTester`](AtlasClient/src/main/scala/com/lightbend/atlas/utils/ConnectivityTester.scala), to check if you can connect to the cluster correctly and get a sense of how definitions look in Atlas.
* [`ModelCreator`](AtlasClient/src/main/scala/com/lightbend/atlas/model/ModelCreator.scala), to create the required types and populate a simple model information.

To run either of them, you have two options.

1. In your IDE, navigate to `com.lightbend.atlas.utils.ConnectivityTester` or `com.lightbend.atlas.model.ModelCreator`, then right click and use the _Run_ command.
2. Use one of several `run` commands in `sbt`.

For `sbt`, the easiest way is to invoke the `run` command and then enter the number at the prompt:

```
sbt:atlasclient> run
...
Multiple main classes detected, select one to run:

 [1] com.lightbend.atlas.model.ModelCreator
 [2] com.lightbend.atlas.utils.ConnectivityTester
```

You can also avoid the prompt by invoking each one directly:

```
sbt:atlasclient> runMain com.lightbend.atlas.model.ModelCreator
sbt:atlasclient> runMain com.lightbend.atlas.utils.ConnectivityTester
```

### Viewing results

Point your browser to the following URL to see the Atlas UI:
````
http://localhost:21000
````

Use the credentials `admin/admin`.
