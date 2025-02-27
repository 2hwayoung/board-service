# example-springboot-restapi

### 📌 Running MySQL with Docker Compose & Enabling Query Logs

Start the container:

```bash
docker-compose up -d
```

Since logs are mapped to your local machine in `./mysql/conf`, you can monitor them directly:

```bash
tail -f ./mysql_logs/general.log
```

Or search for specific keywords:

```bash
grep "COMMIT" ./mysql_logs/general.log
grep "ROLLBACK" ./mysql_logs/general.log
```

2
test
