import { gcpSetupClient } from "../Client";

export const startGcpSetup = () => {
  return gcpSetupClient.post("");
};

export const getGcpSetupStatus = () => {
  return gcpSetupClient.get("/status");
};
