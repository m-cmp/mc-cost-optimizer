// src/stores/selectedOptions.js
import { defineStore } from 'pinia';
import ps from '@/utils/common.js'
export const useSelectedOptionsStore = defineStore('selectedOptions', {
    state: () => ({
        selectedOptions: {
            selectedWorkspace: {
                'workspaceNM': 'WorkSpace'
            },
            selectedProjects: [], // 여러 개의 프로젝트를 선택할 수 있도록 배열로 변경
            selectedCsps: [], // 여러 개의 CSP를 선택할 수 있도록 배열로 변경
            today: ps.date.toFormatString('','yyyymmdd')
        }
    }),
    actions: {
        setSelectedWorkspace(workspace) {
            // workspaceCD 값만 저장
            this.selectedOptions.selectedWorkspace = workspace.workspaceCD;
        },
        toggleProject(project) {
            // projectCD 값만 저장
            const projectCDs = project.map(p => p.projectCD);
            this.selectedOptions.selectedProjects = projectCDs;
        },
        toggleCSP(csp) {
            this.selectedOptions.selectedCsps = csp;
        }
    }
});
