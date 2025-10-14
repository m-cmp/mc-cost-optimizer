import {
  IconTriangleFilled,
  IconTriangleInvertedFilled,
} from "@tabler/icons-react";

/**
 * @component ChangeIndicator
 *
 * @description
 * 전월 대비 증감률과 증감금액을 표시하는 공용 컴포넌트.
 *
 * @prop {number} changePercent
 *   증감률 (%)
 *
 * @prop {number} changeAmount
 *   증감 금액
 *
 * @prop {string} [unit="USD"]
 *   표시할 단위 (기본: USD)
 */
export default function ChangeIndicator({
  changePercent,
  changeAmount,
  unit = "USD",
}) {
  const isIncrease = changeAmount > 0;
  const isDecrease = changeAmount < 0;

  const color = isIncrease ? "red" : isDecrease ? "#0080FF" : "gray";

  const Icon = isIncrease
    ? IconTriangleFilled
    : isDecrease
    ? IconTriangleInvertedFilled
    : null;

  return (
    <div style={{ display: "flex", alignItems: "center", gap: "4px", color }}>
      {Icon && <Icon size={16} stroke={2} color={color} />}
      <span style={{ fontSize: "1rem", fontWeight: "500" }}>
        {(changePercent ?? 0).toFixed(1)}% ({(changeAmount ?? 0).toFixed(1)}{" "}
        {unit})
      </span>
    </div>
  );
}
