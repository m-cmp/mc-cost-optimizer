import { create } from "zustand";

export const useProjectStore = create((set) => ({
  workspaceId: null,
  workspaceName: null,
  projectId: null,
  projectUUID: null,
  projectName: null,
  userToken: null,

  setWorkspace: (id, name) => {
    console.log("🔄 [Store Update] setWorkspace 호출:", { id, name });
    set({ workspaceId: id, workspaceName: name });
  },
  setProject: (id, uuid, name) => {
    console.log("🔄 [Store Update] setProject 호출:", { id, uuid, name });
    set({ projectId: id, projectUUID: uuid, projectName: name });
  },
  setUserToken: (token) => {
    console.log("🔄 [Store Update] setUserToken 호출:", token);
    set({ userToken: token });
  },
}));
