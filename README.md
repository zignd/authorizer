# authorizer

A command-line application capable for authorizing transactions coming from its STDIN and then returning the result through its STDOUT.

## Requirements

The application can be executed locally through Docker or Leiningen, here are the requirements for both.

### Docker

You only need a basic Docker installation. Detailed information regarding the installation for specific platforms can be found [here](https://docs.docker.com/install/).

### Leiningen

You will need to install OpenJDK 8 or a later version, and then Leiningen. Detailed information can be found [here](https://leiningen.org/).

## Running

### Through Docker

You can run the application on your machine without the need of installing anything other than Docker. Therefore, in case you have it installed locally, you will be able to follow the steps below:

1. Let's build the image based on the Dockerfile which can be found in the root directory of the application.

```
$ docker build -t authorizer .
```

2. Once the image is built, let's run the application through Docker and pipe to its STDIN the testing operations which can be found in the `testing-operations` directory.

```
$ cat testing-operations/account-already-initialized | docker run --rm -i authorizer
```

In order to try other operations, check the `testing-operations` directory, and change the path in the `cat` command in the example above.

### Through Leiningen

In order to run the application through Leiningen, considering you have the requirements described above, you simply have to execute the following command, it will pipe the content of one of the files containing testing operations (which can be found in the `testing-operations` directory) to the process:

    $ cat testing-operations/account-already-initialized | lein run

In order to try other operations, check the `testing-operations` directory, and change the path in the `cat` command in the example above.

## Testing

The tests are executed through Leiningen, therefore, you're required to have requirements mentioned above as if to run the application through Leiningen. Considering you have that, follow by executing the following command:

    $ lein test

## Code Architecture Diagram

TODO: Create something on draw.io once you finish the challenge.