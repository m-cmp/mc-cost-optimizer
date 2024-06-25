// src/stores/selectedOptions.js
import { defineStore } from 'pinia';

export const useSelectedOptionsStore = defineStore('selectedOptions', {
    state: () => ({
        selectedOptions: {
            workspace: {
                'workspaceNM': 'WorkSpace'
            },
            project: [], // 여러 개의 프로젝트를 선택할 수 있도록 배열로 변경
            csp: [] // 여러 개의 CSP를 선택할 수 있도록 배열로 변경
        }
    }),
    actions: {
        setSelectedWorkspace(workspace) {
            // workspaceCD 값만 저장
            this.selectedOptions.workspace = workspace.workspaceCD;
        },
        toggleProject(project) {
            // projectCD 값만 저장
            const projectCDs = project.map(p => p.projectCD);
            this.selectedOptions.project = projectCDs;
        },
        toggleCSP(csp) {
            this.selectedOptions.csp = csp;
        }
    }
});
