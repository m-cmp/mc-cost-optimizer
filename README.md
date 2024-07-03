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

- clone this repo
- update submodule (git submodule init; git submodule update)
- install mysql version 8
- run ddl/dml script
- create Mysql user(name : mcmpcostopti / init pw : 0000)
- build and run Backend first, Frontend later

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
