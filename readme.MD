## Implementing an example of image repository

### Design assumptions

* Mainly IO bounded
* Using vertx
* Some of the workers are cpu bounded so it may require to 

```
+---------------------------------------------------------------------------------------------------------------------------+
|                                                                                                                           |
|     Prog Image Service                                                +---------------------------------------------+     |
|                                                                       | Http Server                                 |     |
|                                                                       |                                             |     |
|                                                                       |    ++                                       |     |
|                                           +----------------+          |    +------+ /health                         |     |
|          +---------------+-----------+----+ Image Actions  +----------+    |                                        |     |
|          |               |           |    +-------+--------+          |    |                                        |     |
|          |               |           |            |                   |    +------+ /image (GET images and metadata)|     |
|    +-----+-------+  +----+------+ +--+-----+ +----+-----+             |    |           +                            |     |
|    | png to jpeg |  |crop worker| |filter 1| | filter 2 |             |    |           +----+GET /:imageName.format |     |
|    | worker      |  |           | |worker  | | worker   | ...         |    +           |                            |     |
|    +-------------+  +-----------+ +--------+ +----------+             |                +----+(POS posts a new image)|     |
|                                                                       |                +                            |     |
|  +-------------------------------------------------+------+           |                                             |     |
|              Image workers                         |                  +---------------------+-----------------------+     |
|                                            +-------+------------+                           |                             |
|                                    +-------+ Image Repository   | +-------------------------+                             |
|                                    |       +--------------------+                                                         |
+---------------------------------------------------------------------------------------------------------------------------+
                                     |
                                     +------+ File System


```
 

### Usage

* Upload an image

``` 
curl -H "Content-Type: image/png" -X POST -d @/Users/oleg/dev/hub/prog-image-service/src/test/resources/simple.file http://localhost:2000/image
```

* list image metadata 

``` http://localhost:2000/image```

* get image
 
```http://localhost:2000/image/image.png ```


* run locally 

```
mvn clean install
java -jar ./target/prog-image-service-1.0-SNAPSHOT-fat.jar 

```

### TODO
* add logging support
* improve async code handling by adding RxJava or experimenting with Kotlin coroutines
* I think is not good idea to do conversion from one format to another when the image is requested, maybe when is posted? as an async action
* make the application more configurable so it can only support certain workers
* better error handling
* implement high level client api
* store additional image metadata

