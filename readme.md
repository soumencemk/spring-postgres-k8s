# Spring boot Postgres on kubernetes

## 1. Running the application on standalone Docker

### a. Create a new docker network

```bash
docker network create test_soumen
```

### b. Install postgresql on that network

```bash
docker run -d --net test_soumen \
--name pgdb -e POSTGRES_USER=postgres \
-e POSTGRES_PASSWORD=postgres postgres
```

### c. Build the docker image

```
docker build . -t <dockername>
```

### d. Starting that new image

```
docker run -d --net test_soumen -e POSTGRES_URL=r2dbc:postgresql://postgres:postgres@pgdb:5432/postgres -p 8080:8080 <image_name>
```

