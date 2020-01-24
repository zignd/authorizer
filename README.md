# authorizer

A command-line application for the authorization of transactions. It receives the operations as JSON from its STDIN and returns the results to its STDOUT.

## Requirements

The application can be executed locally using Docker or Leiningen; here are the requirements for both.

### Docker

You only need Docker installed. Detailed information regarding the installation for specific platforms can be found [here](https://docs.docker.com/install/).

### Leiningen

You need to install OpenJDK 8 (or a later version), and then Leiningen. Detailed information can be found [here](https://leiningen.org/).

## Running

### Using Docker

You can run the application on your machine without the need to install anything other than Docker. Therefore, in case you have it installed locally, you will be able to follow the steps below:

1. Let's build the image based on the Dockerfile, which can be found in the root directory of the application.

```
$ docker build -t authorizer .
```

2. Once the image is built, let's run a Docker container and pipe the testing operations to its STDIN.

```
$ cat testing-operations/account-already-initialized | docker run --rm -i authorizer
```

To try other operations, check the `testing-operations` directory, and change the path provided to the `cat` command in the previous example.

### Using Leiningen

To run the application using Leiningen, you have to execute the following command, so that it pipes the content of one of the testing operations files to the STDIN:

    $ cat testing-operations/account-already-initialized | lein run

To try other operations, check the `testing-operations` directory, and change the path provided to the `cat` command in the previous example.

## Testing

You can run the tests using Leinigen:

    $ lein test
