import Card from "@/components/common/card/Card";
import { cspColorMap } from "@/utils/styles/colors";
import { baseInfoStyles as styles } from "@/utils/styles/cardStyles";

function ProviderItem({ name, amount }) {
  return (
    <div style={styles.providerItem}>
      <div style={styles.providerLabel}>
        <span
          style={styles.providerDot(cspColorMap[name] || cspColorMap.OTHERS)}
        />
        {name}
      </div>
      <div style={styles.providerValue}>
        {`${
          amount !== null && amount !== undefined ? amount.toFixed(2) : "0.00"
        } USD`}
      </div>
    </div>
  );
}

export default function BaseInfoCard({ totalAmount, providers = [] }) {
  return (
    <Card title="Base Info" titleSize={2}>
      <div style={{ maxWidth: "550px", margin: "0 auto" }}>
        <div style={{ marginBottom: "12px" }}>
          <div style={styles.totalLabel}>
            <span style={styles.totalDot}></span>Total Amount
          </div>
          <div style={styles.totalValue}>
            {totalAmount !== null && totalAmount !== undefined
              ? totalAmount.toFixed(2)
              : "0.00"}{" "}
            USD
          </div>
        </div>

        <hr style={{ margin: "12px 0", borderColor: "#374151" }} />

        <div style={styles.providerWrapper}>
          {providers.map((p) => (
            <ProviderItem key={p.csp} name={p.csp} amount={p.cost} />
          ))}
        </div>
      </div>
    </Card>
  );
}
