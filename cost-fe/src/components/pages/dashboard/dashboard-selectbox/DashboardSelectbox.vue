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
                        <button class="label">{{ selectedOptions.workspace.workspaceNM }}</button>
                        <ul v-show="showWorkspaceOptions" class="optionList">
                            <li class="optionItem" v-for="option in workspaceOptions" :key="option" @click.stop="selectOption('workspace', option)">{{ option.workspaceNM }}</li>
                        </ul>
                    </div>
                </div>
                <!-- selectBox 2 -->
                <div class="box">
                    <div :class="['selectBox2', { 'active': showProjectOptions }]" @click="toggleOptions('project')">
                        <button class="label">{{ selectedOptions.project.length > 0 ? selectedOptions.project.map(p => p.projectNM).join(', ') : 'Projects' }}</button>
                        <ul v-show="showProjectOptions" class="optionList">
                            <li class="optionItem" v-for="option in projectOptions" :key="option.projectCD" :class="{ 'selected': isSelected(option) }" @click.stop="selectOption('project', option)">
                                {{ option.projectNM }}
                            </li>
                        </ul>
                    </div>
                </div>
                <!-- selectBox 3 -->
                <div :class="['selectBox2', { 'active': showCSPOptions }]" @click="toggleOptions('csp')">
                    <button class="label">{{ selectedOptions.csp.length > 0 ? selectedOptions.csp.join(', ') : 'CSP 선택' }}</button>
                    <ul v-show="showCSPOptions" class="optionList">
                        <li class="optionItem" v-for="option in cspOptions" :key="option" :class="{ 'selected': isSelected('csp', option) }" @click.stop="selectOption('csp', option)">
                            {{ option }}
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>
</template>

<script>
import axios from 'axios';
import {
    useSelectedOptionsStore
} from '@/stores/selectedOptions';

export default {
    name: 'DashboardSelectbox',
    data() {
        return {
            showWorkspaceOptions: false,
            showProjectOptions: false,
            showCSPOptions: false,
            workspaceOptions: [],
            projectOptions: [],
            cspOptions: ['AWS', 'GCP', 'AZURE', 'NCP'],
            selectedOptions: {
                workspace: {
                    'workspaceNM': 'WorkSpace'
                },
                project: [], // 여러 개의 프로젝트를 선택할 수 있도록 배열로 변경
                csp: [] // 여러 개의 CSP를 선택할 수 있도록 배열로 변경
            }
        };
    },
    setup() {
        const store = useSelectedOptionsStore();
        return {
            store
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
            if (type === 'workspace') {
                this.fetchProjects(option);
                this.store.setSelectedWorkspace(option);
            }
            if (type === 'project') {
                // console.log('선택한 프로젝트 : ', this.selectedOptions);
                const index = this.selectedOptions.project.findIndex(proj => proj.projectCD === option.projectCD);
                if (index > -1) {
                    // 이미 선택된 경우 해제
                    this.selectedOptions.project.splice(index, 1);
                } else {
                    // 선택되지 않은 경우 추가
                    this.selectedOptions.project.push(option);
                }
                this.store.toggleProject(this.selectedOptions.project);
            } else if (type === 'csp') {
                const index = this.selectedOptions.csp.indexOf(option);
                if (index > -1) {
                    // 이미 선택된 경우 해제
                    this.selectedOptions.csp.splice(index, 1);
                } else {
                    // 선택되지 않은 경우 추가
                    this.selectedOptions.csp.push(option);
                }
                this.store.toggleCSP(this.selectedOptions.csp);
            } else {
                this.selectedOptions[type] = option;
            }
            this.resetOptions();
        },
        isSelected(type, option) {
            if (type === 'project') {
                return this.selectedOptions.project.some(proj => proj.projectCD === option.projectCD);
            } else if (type === 'csp') {
                return this.selectedOptions.csp.includes(option);
            }
        },
        fetchWorkspaces() {
            axios.get('http://localhost:9090/api/v2/getWorkspaces', {
                    params: {
                        account: 'mcmpcostopti'
                    }
                })
                .then(response => {
                    if (response.data.status === 'OK') {
                        this.workspaceOptions = response.data.Data;
                    } else {
                        console.error('api 호출 실패: ', response.data);
                    }
                })
                .catch(error => {
                    console.error(error);
                });
        },
        fetchProjects(options) {
            axios.get('http://localhost:9090/api/v2/getProjects', {
                    params: {
                        workspaceCD: options.workspaceCD
                    }
                })
                .then(response => {
                    if (response.data.status === "OK") {
                        this.projectOptions = response.data.Data;
                    } else {
                        console.error('api 호출 실패: ', response.data);
                    }
                })
                .catch(error => {
                    console.error(error);
                });
        }
    },
    mounted() {
        this.fetchWorkspaces();
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
