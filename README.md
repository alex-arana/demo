GoogleContainerTools distroless / Issue #1350
====

Overview
---
This repository contains the source for building the demo project used to demonstrate the issue above. As per the 
[issue description](https://github.com/GoogleContainerTools/distroless/issues/1350):

> Following up on Issue #183 regarding implementation of Docker HEALTHCHECK within a distroless image, the accepted solution no longer appears to work.
> 
> As per one of the closing comments back in March, adding wget to a distroless image could be used to implement Docker HEALTHCHECK like so:
> `COPY --from=busybox:1.36.0-musl /bin/wget /usr/bin/wget`
> 
> However, this does not appear to work with the following distroless container images I have tested:
> 
> * gcr.io/distroless/base:latest
> * gcr.io/distroless/base:nonroot
> * gcr.io/distroless/java17-debian11:nonroot


Prerequisites
-------------
In order to build the program, the following is required

* Docker Engine
* A working Internet connection (to download all library dependencies required to build the program).


Build and Run
------------

### To build the output Docker image locally use:

```
    $ ./gradlew buildDockerImage composeUp
```

You can also skip `buildDockerImage` step and run `composeUp` which simply pulls the public image directly from [DockerHub](https://hub.docker.com/repository/docker/lexluthor421/demo).


### To build and push the output Docker image using `buildx` driver:

```
    $ ./gradlew buildDockerxImage composeUp
```

You can also invoke `docker-compose` directly from the project directory using: `docker-compose up -d` to start a container
in the background (detached mode). You will then need to check the status of the container until it becomes `unhealthy` (usually
a couple of minutes): `docker ps -a`.


Stopping
---------

To stop a local container using docker-compose:

```
    $ ./gradlew composeDown
```


Links
-----

- [Docker Reference](https://docs.docker.com/engine/reference/builder/)
- [GoogleContainerTools/distroless](https://github.com/GoogleContainerTools/distroless)


