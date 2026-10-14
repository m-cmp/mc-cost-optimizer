import { gcpSetupClient } from "../Client";

export const getGcpSetupStatus = () =>
  gcpSetupClient.get("/status");

export const autoProvisionGcp = () =>
  gcpSetupClient.post("/auto-provision");

export const setupGcpDataset = (action, datasetName) =>
  gcpSetupClient.post("/dataset", { action, datasetName });

export const confirmGcpBillingExport = () =>
  gcpSetupClient.post("/billing-confirmed");
