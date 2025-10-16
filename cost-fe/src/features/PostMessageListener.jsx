import { useEffect } from "react";
import { useProjectStore } from "../stores/useProjectStore";
import { useAlertStore } from "../stores/useAlertStore";
import { logger } from "@/utils/logger";

export default function PostMessageListener() {
  const setWorkspace = useProjectStore((s) => s.setWorkspace);
  const setProject = useProjectStore((s) => s.setProject);
  const setUserToken = useProjectStore((s) => s.setUserToken);
  const addAlert = useAlertStore((s) => s.addAlert);

  useEffect(() => {
    let messageReceived = false;

    const setDefaultValues = (reason) => {
      console.warn(`⚠️ [PostMessage] ${reason}`);
      logger.warn(reason);

      addAlert({
        variant: "warning",
        title: "No Message Received",
        message:
          "Project data was not received from PostMessage. Applying default values.",
        duration: 5000,
      });

      setWorkspace("ws01", "testWs");
      setProject("ns01", "mock-uuid", "default-project");
      setUserToken("Null");
    };

    // Check for initial message stored globally
    if (window.__INITIAL_POST_MESSAGE__) {
      const data = window.__INITIAL_POST_MESSAGE__;
      console.log("[PostMessage] Global message detected, setting Store", {
        workspaceId: data.workspaceInfo.name,
        workspaceName: data.workspaceInfo.name,
        projectId: data.projectInfo.ns_id,
        projectUUID: data.projectInfo.id,
        projectName: data.projectInfo.name,
      });
      messageReceived = true;

      setWorkspace(data.workspaceInfo.name, data.workspaceInfo.name);
      setProject(
        data.projectInfo.ns_id,
        data.projectInfo.id,
        data.projectInfo.name
      );
      setUserToken(data.accessToken);

      window.__INITIAL_POST_MESSAGE__ = null;
    }

    function handleMessage(event) {
      if (event.data && event.data.accessToken) {
        console.log("[PostMessage] New message received, updating Store", {
          workspaceId: event.data.workspaceInfo.name,
          workspaceName: event.data.workspaceInfo.name,
          projectId: event.data.projectInfo.ns_id,
          projectUUID: event.data.projectInfo.id,
          projectName: event.data.projectInfo.name,
        });
        messageReceived = true;

        setWorkspace(
          event.data.workspaceInfo.name,
          event.data.workspaceInfo.name
        );
        setProject(
          event.data.projectInfo.ns_id,
          event.data.projectInfo.id,
          event.data.projectInfo.name
        );
        setUserToken(event.data.accessToken);
      }
    }

    window.addEventListener("message", handleMessage);

    // Set fallback data if message is not received within timeout
    const fallbackTimeout = setTimeout(() => {
      if (!messageReceived) {
        setDefaultValues("No message received, applying default values");
      }
    }, 3000);

    return () => {
      window.removeEventListener("message", handleMessage);
      clearTimeout(fallbackTimeout);
    };
  }, [setWorkspace, setProject, setUserToken, addAlert]);

  return null;
}
