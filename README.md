# Task Runner

## Introduction
 This code allows the execution and monitoring of a group of dependent or independent tasks.

 It was primarily written to support running Apache Hive HQL and Python spark code against a YARN managed Hadoop cluster, but the code supports plugin tasks that could execute any
 kind of activity.

 Task templating is also supported to allow the configuration of Task contents against different execution environments (unit test/production for example).

## Features

* Suports the running of the following task types out of the box: -

    - JDBC - SQL code that can be run against a standard JDBC connection. File extension `.sql`.
    - Hive HQL -  [Apache Hive HQL](https://cwiki.apache.org/confluence/display/Hive/LanguageManual) run using the Hive CLI. File extension `.hql`.
    - Apache Spark Python - [Apache Spark Python](https://spark.apache.org/docs/latest/sql-programming-guide.html) code run using `spark-submit`. File extension `.py`.
    - Apache Spark Jar - Execute a Jar using `spark-submit`. File extension `.jar`.
    - Dummy Task - Does nothing! - File Extensions `.dmy` and `.txt`.
* Templating - Tasks can be templated to support running in different environments.
* Plugin architecture - New tasks types can be added to the framework using dependency injection (See [Adding a task type](#adding-a-task-type))

## Example - Running a Single Task
To run a single task (for example, some SQL against a JDBC connection), place the SQL file in a directory together with
an `application.conf` file that contains the JDBC config necessary to make a JDBC connection.

File: `create_foo.sql`
```
create table foo (
    bar string
)
```

File: `application.conf`
```
jdbc {
  driver = "org.apache.derby.jdbc.EmbeddedDriver"
  url = "jdbc:derby:memory:testDB;create=true"
}
```

Invoke TaskRunner telling it where to find the task and configuration: -

```bash
java -jar TaskRunner.jar -conf <DIR> -tasks <DIR>
```

## Example - Running Multiple Tasks
Multiple independent tasks can be run (potentially in parallel) by simply adding files to the task directory.

File: `create_foo.sql`
```
create table foo (
    bar string
)
```

File: `create_bar.sql`
```
create table bar (
    foo string
)
```


File: `application.conf`
```
jdbc {
  driver = "org.apache.derby.jdbc.EmbeddedDriver"
  url = "jdbc:derby:memory:testDB;create=true"
}
# Specify number of threads
jobrunner {
    threads = 2
}
```

TaskRunner will execute `create_foo.sql` and `create_bar.sql` in parallel.


## Example - Introducing Task Dependencies 
Let's say we wanted to make `create_bar.sql` dependent on the successful execution of `create_foo.sql`. Simply add the following *task specific* configuration file, 
which **must** be named the same as its task but with a `.conf` extension: -

File: `create_bar.conf`
```
depends-on {
    id = ["create_foo.sql"]
}
```

Now `create_bar.sql` will only run upon the successful execution of `create_foo.sql`. 
Note that `depends-on.id` is a list so multiple dependencies can be added for a single task if required.

## Example - Task Templates
Let's say we wanted to run the same SQL against different environments, and the database schema/table changes between them.
This can be done by introducing template fields in the task and corresponding task config (`.conf`) file: -


File: `create_bar.sql`
```
create table [=schema].[=tablename] (
    foo string
)
```

In the above example `[=schema]` and `[=tablename]` are both template fields, the code expects to find the values for these fields in the conf file: -

File: `create_bar.conf`
```
depends-on {
    id = ["create_foo.sql"]
}
template {
  schema = "testschema"
  dummy = "mock_bar"
}
```

The code will throw an exception if it cannot find a field value for a template field.
The `-render` option can be used to render the template to stdout to check your fields: -

`java -jar TaskRunner.jar -tasks <dir> -conf <dir> -render create_bar.sql`

output: -
```
create table testschema.mock_bar (
    foo string
)
```

## Configuration
The [typesafe config](https://github.com/lightbend/config) library is used as a configuration engine. With application level and individual task configuration.

### Application Config
Application wide config should be placed in a file called `application.conf` in the directory pointed to by the `-conf` command line option. The following values are
set by default and can be overridden by application values: -
```

#
# Default (empty) JDBC config - application.conf should always override this if JDBC connections
# are required...
#
jdbc {
  username = ""
  password = ""
  driver = ""
  url = ""
}
#
# Default (empty) Kerberos config - application.conf should always override this if connecting to
# a kerberos managed resource.
#
kerberos {
  username = ""
  password = ""
  principal = ""
}
#
# spark-python task: Default arguments for the spark submit command.
#
spark-python {
  # environment is a list of environment variables to check.
  environment = ["SPARK_HOME"]
  master = "local[*]"
  queue = "default"
  num-executors = 2
  timeoutms = 600000
  driver-java-options = null
}

#
# spark-jar task: Default arguments for the spark submit command.
#
spark-jar {
  # environment is a list of environment variables to check.
  environment = ${spark-python.environment}
  master = ${spark-python.master}
  queue = ${spark-python.queue}
  num-executors = ${spark-python.num-executors}
  timeoutms = ${spark-python.timeoutms}
  driver-java-options = null
}
#
# Default timeout (in milli-seconds) for hive task executor
#
hive {
  # environment is a list of environment variables to check.
  environment = ["HIVE_HOME"]
  timeoutms = 600000
}


jobrunner {
  # plugintasks - These are the list of task types that the Task Runner framework
  #               will handle. Each task is configured by three items: -
  #                   1. The name of the task (String).
  #                   2. The name of the (Guice) plugin module that injects the
  #                      Task into the framework.
  #                   3. The list of file extensions supported by this plugin.
  #
  plugintasks {
    task = [
      {
        name = "dummy"
        plugin-module = "net.martinprobson.jobrunner.dummytask.DummyTaskModule"
        file-extensions = [".txt", ".dmy"]
      }
      {
        name = "jdbc"
        plugin-module = "net.martinprobson.jobrunner.jdbctask.JDBCTaskModule"
        file-extensions = [".sql"]
      }
      {
        name = "hive"
        plugin-module = "net.martinprobson.jobrunner.hivetask.HiveTaskModule"
        file-extensions = [".hql"]
      }
      {
        name = "spark-python"
        plugin-module = "net.martinprobson.jobrunner.sparkpythontask.SparkPythonTaskModule"
        file-extensions = [".py"]
      }
      {
        name = "spark-jar"
        plugin-module = "net.martinprobson.jobrunner.sparkjartask.SparkJarTaskModule"
        file-extensions = [".jar"]
      }
    ]
  }

  #
  # Default number of threads to use when running tasks.
  # You should override this in application.conf according to
  # requirements.
  #
  threads = 2

}
```

### Task Config
Individual task configuration should be stored in files with the same name as the task but with the `.conf` extension. This is the place for
per task template fields and dependency configuration.

## Command Line Options

- `-tasks` - Name of the directory containing tasks to be run.
- `-conf`  - Name of the directory containing task and application config.
- `-help` - Command line help.
- `-render <taskid>` - (optional) - Render the given task id to stdout.

## Build Instructions

Maven is used as the build tool with the following goals: -

```bash
mvn clean compile test package install
```

## Adding a Task Type
Google [guice](https://github.com/google/guice) is used as the DI framework, to add a custom task, do the following: -

1. Create a new class that extends `BaseTask`.
2. Create custom task execution class that implements the `TaskExecutor` interface.
3. Create a Google guice module to wire together the new task with the Task Factory.
4. Tell the framework about the new task (and the file extensions it is capable of dealing with) by adding the following lines to `reference.conf` (this example is from DummyTask): 
```
  plugintasks {
    task = [
      {
        name = "dummy"
        plugin-module = "net.martinprobson.jobrunner.dummytask.DummyTaskModule"
        file-extensions = [".txt", ".dmy"]
      }
```

The file extension(s) that the built in tasks processes by default can also be chanaged here.

See package `net.martinprobson.jobrunner.dummytask`  for an example of the three classes required.

## Caveats

- This code is intended to be a lightwight framework to allow easy unit and integration testing of Apache Hadoop/Spark workflows. You should probably look into
a full featured execuition engine if you need more scheduling and error recovery options. I recommend [Apache airflow](https://airflow.apache.org/) for this.

## To Do

- Add additional test coverage.

## Acknowledgements

Thanks to [Nadeem Mohammad](https://github.com/dexecutor/dexecutor-core) for the Dexecutor code that this framework builds on.

*Martin Robson* 2018
