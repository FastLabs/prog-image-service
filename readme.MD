## Implementing an example of image repository

### Design assumptions

```
+----------------------------------------------------+
|   Http Server                                      |
|                                                    |
|      ++                                            |
|      +------+ /health                              |
|      |                                             |
|      |                                             |
|      +------+ /image (GET images and metadata)     |
|      |           +                                 |
|      |           +----+GET /:imageName.format      |
|      +           |                                 |
|                  +----+(POS posts a new image)     |
|                  +                                 |
|                                                    |
+----------------------------------------------------+

```
 

## Example of usage

### Upload an image

``` 
curl -H "Content-Type: image/png" -X POST -d @/Users/oleg/dev/hub/prog-image-service/src/test/resources/simple.file http://localhost:2000/image
```

