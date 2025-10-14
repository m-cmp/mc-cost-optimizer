import { create } from "zustand";

export const useProjectStore = create((set) => ({
  workspaceId: null,
  workspaceName: null,
  projectId: null,
  projectUUID: null,
  projectName: null,
  userToken: null,

  setWorkspace: (id, name) => set({ workspaceId: id, workspaceName: name }),
  setProject: (id, uuid, name) =>
    set({ projectId: id, projectUUID: uuid, projectName: name }),
  setUserToken: (token) => set({ userToken: token }),
}));
