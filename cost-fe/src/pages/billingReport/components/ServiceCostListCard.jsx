import Card from "@/components/common/card/Card";
import { Icons } from "@/icons/Icons";
import {
  serviceItemStyle,
  serviceIconBoxStyle,
} from "@/utils/styles/cardStyles";

export default function ServiceCostListCard({ services }) {
  const iconMap = {
    "Virtual Machine": Icons.virtualMachine,
    Storage: Icons.storage,
    Database: Icons.database,
    LB: Icons.lb,
    Others: Icons.others,
  };

  return (
    <Card title="Accumulated cost by Service" titleSize={2}>
      <div style={{ display: "flex", flexDirection: "column", gap: "10px" }}>
        {services.map((service, idx) => {
          const Icon =
            iconMap[service.familyProductCode] || Icons.virtualMachine;
          return (
            <div key={idx} style={serviceItemStyle}>
              <div style={serviceIconBoxStyle}>
                <Icon size={25} color="#fff" />
              </div>
              <div>
                <div style={{ fontWeight: 500 }}>
                  {service.familyProductCode}
                </div>
                <div style={{ fontSize: "13px", color: "#6b7280" }}>
                  {service.totalCost.toFixed(3)} USD
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </Card>
  );
}
