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
      } else {
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
