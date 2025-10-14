import { cspColorMap as cspColorMapFromConstants } from "../../constants/cspConstants";

export const chartColors = {
  default: [
    "#3B82F6", // Blue (총 금액 등 기본 강조)
    "#F59E0B", // Yellow
    "#10B981", // Green
    "#8B5CF6", // Purple
    "#EF4444", // Red
    "#6B7280", // Gray
  ],
  neutral: {
    text: "#111827", // 진한 텍스트
    subText: "#6b7280", // 보조 텍스트
    border: "#e5e7eb",
  },
};

/**
 * CSP별 색상 맵
 * @deprecated cspConstants.js에서 중앙 관리됨. cspConstants.cspColorMap 사용 권장
 */
export const cspColorMap = cspColorMapFromConstants;
