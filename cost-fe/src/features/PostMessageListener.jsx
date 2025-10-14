import { useEffect } from "react";
import { useProjectStore } from "../stores/useProjectStore";
import { logger } from "@/utils/logger";

export default function PostMessageListener() {
  const setWorkspace = useProjectStore((s) => s.setWorkspace);
  const setProject = useProjectStore((s) => s.setProject);
  const setUserToken = useProjectStore((s) => s.setUserToken);

  useEffect(() => {
    function handleMessage(event) {
      logger.debug("message received:", event);

      if (event.data && event.data.accessToken) {
        console.log("=== [PostMessage] 외부에서 받은 데이터 ===");
        console.log("accessToken:", event.data.accessToken);
        console.log("workspaceInfo:", event.data.workspaceInfo);
        console.log("projectInfo:", event.data.projectInfo);
        console.log("====================================");

        setWorkspace(
          event.data.workspaceInfo.id,
          event.data.workspaceInfo.name
        );
        setProject(
          event.data.projectInfo.ns_id,
          event.data.projectInfo.id,
          event.data.projectInfo.name
        );
        setUserToken(event.data.accessToken);

        console.log("=== [Store] 저장된 데이터 ===");
        console.log("workspaceId:", event.data.workspaceInfo.id);
        console.log("workspaceName:", event.data.workspaceInfo.name);
        console.log("projectId (ns_id):", event.data.projectInfo.ns_id);
        console.log("projectUUID:", event.data.projectInfo.id);
        console.log("projectName:", event.data.projectInfo.name);
        console.log("====================================");
      } else {
        console.warn("⚠️ [PostMessage] 프로젝트 코드가 없어서 임시 값 적용");
        logger.warn("프로젝트 코드가 없어서 임시 값 적용");
        setWorkspace("ws01", "testWs");
        setProject("ns01", "mock-uuid", "undefined");
        setUserToken("Null");
      }
    }

    window.addEventListener("message", handleMessage);

    if (import.meta.env.MODE === "development") {
      setTimeout(() => {
        window.postMessage(
          {
            accessToken: "dummy-token",
            workspaceInfo: { id: "ws01", name: "Test Workspace" },
            projectInfo: {
              ns_id: "ns01",
              id: "mock-uuid",
              name: "Test Project",
            },
          },
          "*"
        );
      }, 1000);
    }

    return () => {
      window.removeEventListener("message", handleMessage);
    };
  }, [setWorkspace, setProject, setUserToken]);

  return null;
}
