import { chartColors } from "./colors";

export const serviceItemStyle = {
  border: "1px solid #e5e7eb",
  borderRadius: "8px",
  padding: "10px",
  display: "flex",
  alignItems: "center",
  gap: "10px",
};

export const serviceIconBoxStyle = {
  backgroundColor: "#2ecc71",
  padding: "6px",
  borderRadius: "8px",
  display: "flex",
  alignItems: "center",
  justifyContent: "center",
};

export const baseInfoStyles = {
  totalLabel: {
    fontSize: "13px",
    fontWeight: "500",
    color: chartColors.neutral.subText,
    display: "flex",
    alignItems: "center",
    gap: "4px",
  },
  totalDot: {
    width: "10px",
    height: "10px",
    borderRadius: "50%",
    backgroundColor: chartColors.default[4], // 기본 Blue 사용
  },
  totalValue: {
    fontSize: "28px",
    fontWeight: "700",
    color: chartColors.neutral.text,
  },
  providerWrapper: {
    display: "grid",
    gridTemplateColumns: "repeat(2, 1fr)", // 항상 2개
    marginTop: "25px",
    gap: "30px 32px", // 위아래 16px, 좌우 32px 간격
    alignItems: "flex-start",
  },
  providerItem: {
    display: "flex",
    flexDirection: "column",
    gap: "4px",
  },
  providerLabel: {
    fontSize: "15px",
    fontWeight: "600",
    color: "#6b7280",
    display: "flex",
    alignItems: "center",
    gap: "6px",
  },
  providerValue: {
    fontSize: "18px",
    fontWeight: "400",
    color: "#111827",
  },
  providerDot: (provider) => ({
    width: "10px",
    height: "10px",
    borderRadius: "50%",
    backgroundColor: provider,
    display: "inline-block",
  }),
};
