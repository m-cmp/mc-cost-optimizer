<template>
    <!-- Page Header -->
    <div class="page-header d-print-none">
        <div class="container-xl">
            <div class="row g-2 align-items-center">
                <div class="col">
                    <!-- Page pre-title -->
                    <div class="page-pretitle">Overview</div>
                    <h2 class="page-title">Dashboard</h2>
                </div>
                <div class="col-auto ms-auto d-print-none">
                    <div class="btn-list">
                        <span class="d-none d-sm-inline">
                            <a href="#" class="btn">New view</a>
                        </span>
                        <a href="#" class="btn btn-primary d-none d-sm-inline-block" data-bs-toggle="modal" data-bs-target="#modal-report">
                            <!-- Download SVG icon from http://tabler-icons.io/i/plus -->
                            <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                                <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                                <path d="M12 5l0 14"></path>
                                <path d="M5 12l14 0"></path>
                            </svg>
                            Create new report
                        </a>
                        <a href="#" class="btn btn-primary d-sm-none btn-icon" data-bs-toggle="modal" data-bs-target="#modal-report" aria-label="Create new report">
                            <!-- Download SVG icon from http://tabler-icons.io/i/plus -->
                            <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                                <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                                <path d="M12 5l0 14"></path>
                                <path d="M5 12l14 0"></path>
                            </svg>
                            <path d="M12 5l0 14"></path>
                            <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                            <path d="M5 12l14 0"></path>
                        </a>
                    </div>
                </div>
                <div class="selectBoxList">
                    <!-- selectBox 1 -->
                    <div class="box">
                        <div :class="['selectBox2', { 'active': showWorkspaceOptions }]" @click="toggleOptions('workspace')">
                            <button class="label">{{ selectedOptions.workspace }}</button>
                            <ul v-show="showWorkspaceOptions" class="optionList">
                                <li class="optionItem" v-for="option in workspaceOptions" :key="option" @click.stop="selectOption('workspace', option)">{{ option }}</li>
                            </ul>
                        </div>
                    </div>
                    <!-- selectBox 2 -->
                    <div class="box">
                        <div :class="['selectBox2', { 'active': showProjectOptions }]" @click="toggleOptions('project')">
                            <button class="label">{{ selectedOptions.project }}</button>
                            <ul v-show="showProjectOptions" class="optionList">
                                <li class="optionItem" v-for="option in projectOptions" :key="option" @click.stop="selectOption('project', option)">{{ option }}</li>
                            </ul>
                        </div>
                    </div>
                    <!-- selectBox 3 -->
                    <div class="box">
                        <div :class="['selectBox2', { 'active': showCSPOptions }]" @click="toggleOptions('csp')">
                            <button class="label">{{ selectedOptions.csp }}</button>
                            <ul v-show="showCSPOptions" class="optionList">
                                <li class="optionItem" v-for="option in cspOptions" :key="option" @click.stop="selectOption('csp', option)">{{ option }}</li>
                            </ul>
                        </div>
                        <span class="icoArrow">
                            <img src="https://freepikpsd.com/media/2019/10/down-arrow-icon-png-7-Transparent-Images.png" alt="">
                        </span>
                    </div>
                </div>
            </div>
        </div>
    </div>
    </template>
    
    <script>
    export default {
        name: 'DashboardSelectbox',
        data() {
            return {
                showWorkspaceOptions: false,
                showProjectOptions: false,
                showCSPOptions: false,
                workspaceOptions: ['AWS-KR-1', 'AWS-KR-2', 'AZURE-KR-1', 'NCP-FKR-1'],
                projectOptions: ['CostOpti', 'Monitoring', 'Authorize', 'Provision'],
                cspOptions: ['AWS', 'GCP', 'AZURE', 'NCP'],
                selectedOptions: {
                    workspace: 'WorkSpace',
                    project: 'Project',
                    csp: 'CSP'
                }
            };
        },
        methods: {
            toggleOptions(type) {
                if (type === 'workspace') {
                    this.showWorkspaceOptions = !this.showWorkspaceOptions;
                } else {
                    this.showWorkspaceOptions = false;
                }
                if (type === 'project') {
                    this.showProjectOptions = !this.showProjectOptions;
                } else {
                    this.showProjectOptions = false;
                }
                if (type === 'csp') {
                    this.showCSPOptions = !this.showCSPOptions;
                } else {
                    this.showCSPOptions = false;
                }
            },
            resetOptions() {
                this.showWorkspaceOptions = false;
                this.showProjectOptions = false;
                this.showCSPOptions = false;
            },
            selectOption(type, option) {
                this.selectedOptions[type] = option;
                this.resetOptions();
            }
        }
    }
    </script>
    
    <style>
    .selectBoxList {
        display: flex;
        grid-gap: 30px;
        overflow: visible;
    }
    
    .selectBox2 * {
        box-sizing: border-box;
    }
    
    .selectBox2 {
        position: relative;
        z-index: 1;
        width: 350px;
        height: 35px;
        border-radius: 4px;
        border: 1px solid rgb(34, 99, 183);
        cursor: pointer;
    }
    
    .selectBox2:after {
        content: '▼';
        position: absolute;
        top: 50%;
        transform: translateY(-50%);
        right: 5px;
        transition: transform 0.6s ease;
    }
    
    .selectBox2.active:after {
        transform: translateY(-50%) rotate(180deg);
    }
    
    .selectBox2 .label {
        display: flex;
        align-items: center;
        width: inherit;
        height: inherit;
        border: 0 none;
        outline: 0 none;
        padding-left: 15px;
        background: transparent;
        cursor: pointer;
    }
    
    .selectBox2 .optionList {
        position: absolute;
        top: 35px;
        left: 0;
        width: 100%;
        background: rgb(34, 99, 183);
        color: #fff;
        list-style-type: none;
        padding: 0;
        border-radius: 6px;
        overflow: hidden;
        max-height: 0;
        transition: max-height 0.3s ease-in-out, opacity 0.3s ease-in-out;
        z-index: 10;
        opacity: 0;
        pointer-events: none;
    }
    
    .selectBox2.active .optionList {
        max-height: 200px;
        overflow-y: auto;
        opacity: 1;
        pointer-events: auto;
    }
    
    .selectBox2 .optionList::-webkit-scrollbar {
        width: 6px;
    }
    
    .selectBox2 .optionList::-webkit-scrollbar-track {
        background: transparent;
    }
    
    .selectBox2 .optionList::-webkit-scrollbar-thumb {
        background: #303030;
        border-radius: 45px;
    }
    
    .selectBox2 .optionList::-webkit-scrollbar-thumb:hover {
        background: #303030;
    }
    
    .selectBox2 .optionItem {
        border-bottom: 1px dashed rgb(66, 114, 177);
        padding: 10px 15px;
        transition: background 0.1s;
    }
    
    .selectBox2 .optionItem:hover {
        background: rgb(66, 114, 177);
    }
    
    .selectBox2 .optionItem:last-child {
        border-bottom: 0 none;
    }
    </style>
    