# mc-cost-optimizer
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fm-cmp%2Fmc-cost-optimizer.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Fm-cmp%2Fmc-cost-optimizer?ref=badge_shield)


This repository provides a Multi-Cloud Cost Management and Optimizing Framework.

A sub-system of [M-CMP platform](https://github.com/m-cmp/docs/tree/main) to deploy and manage Multi-Cloud Infrastructures.

([Cloud-Barista/CB-Tumblebug](https://github.com/cloud-barista/cb-tumblebug) mirroring for M-CMP)

## Overview

- Multi-cloud Cost Management and Optimizing Framework (Codename: mc-cost-optimizer): Supports cost management and cost optimization processes for various heterogeneous clouds
- Development and Contribution strategy: Delivers cost optimization features for multicloud environments and promptly adapts to new service launches.
- Development Space: M-CMP Cost Optimizer ([https://github.com/m-cmp/mc-cost-optimizer](https://github.com/m-cmp/mc-cost-optimizer))

## How to Use

### Step one
```
  ## 1. clone this repo
  mkdir -p {your source repo}
  git clone https://github.com/m-cmp/mc-cost-optimizer.git
```

### Step two
```
  ## 2. DB install
  ### Example of DB installation method based on Mac
  ### In case of DB installation, installation is possible depending on the installation environment. 
  ### MySQL version 8 must be installed
  
  ### create user and privileges settings
  CREATE USER 'mcmpcostopti'@'%' IDENTIFIED BY '0000'; 
  GRANT ALL PRIVILEGES ON *.* TO 'mcmpcostopti'@'%' WITH GRANT OPTION; 
  FLUSH PRIVILEGES;
```

### Step three
```
  ## 3. DDL/DML execution
  ### sql file path : /mc-cost-optimizer/mysql
  ### running script
  mysql -u mcmpcostopti -p < init_cost_db_ddl.sql
  mysql -u mcmpcostopti -p < init_mail_db.sql
  mysql -u mcmpcostopti -p < init_slack_db.sql
```

### Step four
```  
  4. docker-compose run
  ### Modify '.env' to suit your environment.
  vi .env 
  ### build and run docker
  docker compose up -d
```

### Frontend(cost-fe)
- node version : v20.13.1
- npm version : v10.5.2
- run command : npm run serve
#### dev mode
- run command : npm run dev(front-end independent build)


### BackEnd
- java version : 17
- spring boot version : 2.7.15

### batch process(costCollector/costProcessor/costSelector)
- java version : 17
- spring boot version : 3.2.8

### AlarmService
- java version : 17
- spring boot version : 3.2.4

### DataBase
- mysql version : 8
- run script 
```
- /mc-cost-optimizer/mysql/init_cost_db_ddl.sql
- /mc-cost-optimizer/mysql/init_mail_db.sql
- /mc-cost-optimizer/mysql/init_slack_db.sql
```


### Video Guide to init project
#### [YouTube link](https://www.youtube.com/watch?v=Pei7MWMD6UA)


### swagger UI
#### [mc-cost-optimizer Swagger api](https://cloud-barista.github.io/api/?url=https://raw.githubusercontent.com/m-cmp/mc-cost-optimizer/main/swagger.yaml)

## How to Contribute

- Issues/Discussions/Ideas: Utilize issue of mc-cost-optimizer
- Code improvement: Directly contribute at the [https://github.com/m-cmp/mc-cost-optimizer](https://github.com/m-cmp/mc-cost-optimizer) repository

## License
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fm-cmp%2Fmc-cost-optimizer.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2Fm-cmp%2Fmc-cost-optimizer?ref=badge_large)
