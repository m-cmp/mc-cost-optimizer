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
  brew install mysql
  brew services start mysql
  CREATE USER 'mcmpcostopti'@'%' IDENTIFIED BY '0000'; 
  GRANT ALL PRIVILEGES ON *.* TO 'mcmpcostopti'@'%' WITH GRANT OPTION; 
  FLUSH PRIVILEGES;
```

### Step three
```
  ## 3. DDL/DML execution
  ### sql file path : /BackEnd/src/main/resource/sql
  ### running step DDL > DML
  mysql -u mcmpcostopti -p < mcmp_cost_optimize_ddl.sql
  mysql -u mcmpcostopti -p < mcmp_cost_optimize_dml.sql
```

### Step four
```  
  4. backend run
  ### Maven install and jar run
  brew install maven
  mvn clean install
  java -jar target/BackEnd.jar
```

### Step five
```  
  5. Frontend run
  ## node : v20.13.1
  ## npm : v10.5.2
  npm i
  npm run serve
```

### Frontend
- node version : v20.13.1
- npm version : v10.5.2
- run command : npm run serve

### Backend
- java version : 17

### DataBase
- mysql version : 8
- run script /BackEnd/src/main/resources/sql/mcmp_cost_optimize_ddl.sql
- run script /BackEnd/src/main/resources/sql/mcmp_cost_optimize_dml.sql

### swagger UI
- After starting the backend, search the URL
- url : localhost:9090/swagger-ui(default back-end port 9090)

## How to Contribute

- Issues/Discussions/Ideas: Utilize issue of mc-cost-optimizer
- Code improvement: Directly contribute at the [https://github.com/m-cmp/mc-cost-optimizer](https://github.com/m-cmp/mc-cost-optimizer) repository

## License
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fm-cmp%2Fmc-cost-optimizer.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2Fm-cmp%2Fmc-cost-optimizer?ref=badge_large)
