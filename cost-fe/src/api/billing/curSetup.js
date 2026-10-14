import { curSetupClient } from "../Client";

export const startCurSetup = () => {
  return curSetupClient.post("/setup");
};

export const getCurSetupStatus = () => {
  return curSetupClient.get("/status");
};
