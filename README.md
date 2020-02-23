# ML Metadata

> **NOTES:**
>
> This code has been tested only with Java 8 and 11, Scala 2.12.10 and Python 3.7 (the default setting). Any other versions of Java will not work. Other versions of Scala 2.11 and 2.12 may work.

* [Boris Lublinsky - Lightbend](mailto:boris.lublinsky@lightbend.com): See [Lightbend Platform](https://lightbend.com/lightbend-platform)
* [Dean Wampler - Anyscale](mailto:dean@anyscale.io): See [Anyscale](https://anyscale.io) and [Ray](https://ray.io)

[Strata Data Conference San Jose, Tuesday, March 16, 2020](https://conferences.oreilly.com/strata-data-ai/stai-ca/schedule/2020-03-16)

©Copyright 2020, Lightbend, Inc. Apache 2.0 License. Please use as you see fit, at your own risk, but attribution is requested.

See the companion presentation for the tutorial in the `presentation` folder

The tutorial contains 3 exercises:
* Serving Model as Data (tensorflow graph) leveraging [Cloudflow](https://cloudflow.io/).
* Using [MLflow](https://mlflow.org/) to capture and view model training metadata.
* Creating a Model registry using [Atlas](https://atlas.apache.org/#/).

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

## Overview of the Examples

We'll work with three examples:

1. Model Serving with Cloudflow
2. Model Training with MLflow
3. Data/Model Governance with Apache Atlas

The first and third examples use the `sbt` build to compile and run the examples. Here's a "crash course" on `sbt`, using an interactive session, where `$` is the shell prompt (`bash`, Windows CMD, or whatever) and `sbt:ML Learning tutorial>` is the interactive prompt for `sbt`:

```
$ sbt
... stuff is output
sbt:ML Learning tutorial> projects
...
[info] 	   atlasclient
[info] 	 * ml-metadata-tutorial-deanw-git
[info] 	   tensorflowAkka
sbt:ML Learning tutorial> projects
```

We are currently using the top-level project for the tutorial. The `atlasclient` is a program for interacting with an Apache Atlas server and `tensorflowAkka` uses [Cloudflow's Akka API](https://cloudflow.io) to demonstrate serving models in a microservice-like context, as we'll explain.

To work with one of the project, for example `tensorflowAkka` (the first one we'll try), use the `project` command, as follows. Note that the prompt will change:

```
sbt:ML Learning tutorial> project tensorflowAkka
sbt:tensorflow-akka>
```

## Model Serving with Cloudflow

This example uses the Akka Streams API in [Cloudflow](https://cloudflow.io/). We won't explain a lot of details about how Cloudflow works, see the [Cloudflow documentation(https://cloudflow.io/docs/current/index.html)] for an introduction and detailed explanations.

Clouflow applications are designed to be tested locally and executed in a cluster for production. For this exercise, we will not install the serving example to a cluster, but run it locally, using `sbt`.

Start the `sbt` interpreter and use the following `sbt` commands:
````
sbt:ML Learning tutorial> project tensorflowAkka
sbt:tensorflow-akka> runLocal
````

This will print out the location of the log file. (Look for the `... --- Output --- ...` line.) On MacOS or Linux systems, use the following command to see the entries as they are written:
````
tail -f <log_location>
````

On Windows, use the command `more < <log_location>`, but it stops as soon as it has read the current last line, so you'll need to repeat the command several times as new output is written to the file.

> **Pro Tips:**
>
> 1. In some MacOS and Linux shells, you can "command-click", "control-click", or right click on the file path in the text output to open it in a console window for easy browsing.
> 2. Actually, in this case, you don't need to switch to the `tensorflowAkka` project before using `runLocal`, but we showed it this way to be clear which nested project we're actually using.

Terminate the example by pressing the Enter key in the `sbt` window.


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

### Viewing metadata

By default, wherever you run your program, the tracking API writes data into files in a local ./mlruns directory. You can then run MLflow’s Tracking UI to view them:
````
mlflow ui
````
View results at
````
http://localhost:5000
````

## Data/Model Governance with Apache Atlas

### Setup
For Atlas you can either install Apache Atlas locally or use a prebuilt Docker image `lightbend/atlas:0.0.1`, which is easier, if you have Docker installed.

If you want to install locally, try using [this bash file](Atlas/localinstall/install.sh), which downloads, builds, and runs Atlas. The following are required for this script to work:

* Java and `JAVA_HOME` must be defined.
* Maven
* `wget` or `curl` (although if both are missing, the script tells you what to do instead)

The Docker image is build using [this Dockerfile](Atlas/docker/Dockerfile). In case you want to install it to a Kubernetes cluster, you can use the following [Helm chart](Atlas/chart).

Run the image using the following command
````
docker run -p 21000:21000 --rm -it lightbend/atlas:0.0.1
````

> **NOTE:** Be patient, it takes a while for an image to be ready.

### Building and Running the Atlas Example

An example of using Atlas is located in [/atlasclient](/atlasclient). You can build it either by loading this directory as a Scala project in your IDE or using this SBT command:
````
sbt clean compile
````

Once the project is built:
* Use [`ConnectivityTester`](atlasclient/src/main/scala/com/lightbend/atlas/utils/ConnectivityTester.scala) to check if you can connect to the cluster correctly and get a sense of how definitions look in Atlas.
* Use [`ModelCreator`](atlasclient/src/main/scala/com/lightbend/atlas/model/ModelCreator.scala) to create the required types and populate a simple model information.

### Viewing results

Point your browser to the following URL to see the Atlas UI:
````
http://localhost:21000
````

Use the credentials `admin/admin`.
