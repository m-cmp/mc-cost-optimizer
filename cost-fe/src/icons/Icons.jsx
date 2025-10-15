import {
  IconHome,
  IconBusinessplan,
  IconUser,
  IconAlarm,
  IconServer,
  IconDatabase,
  IconBucket,
  IconTopologyStar3,
  IconFileTypeCsv,
  IconFileTypeXls,
  IconCheck,
  IconInfoCircle,
  IconAlertTriangle,
  IconAlertCircle,
  IconDotsCircleHorizontal,
  IconDeviceIpadDollar,
} from "@tabler/icons-react";

const defaultProps = {
  size: 20,
  stroke: 1.5,
};

export const Icons = {
  home: (props) => <IconHome {...defaultProps} {...props} />,
  businessplan: (props) => <IconBusinessplan {...defaultProps} {...props} />,
  user: (props) => <IconUser {...defaultProps} {...props} />,
  alarm: (props) => <IconAlarm {...defaultProps} {...props} />,
  budget: (props) => <IconDeviceIpadDollar {...defaultProps} {...props} />,

  // Service icons
  virtualMachine: (props) => <IconServer {...defaultProps} {...props} />,
  storage: (props) => <IconBucket {...defaultProps} {...props} />,
  database: (props) => <IconDatabase {...defaultProps} {...props} />,
  lb: (props) => <IconTopologyStar3 {...defaultProps} {...props} />,
  others: (props) => <IconDotsCircleHorizontal {...defaultProps} {...props} />,

  // Export
  exportCsv: (props) => <IconFileTypeCsv {...defaultProps} {...props} />,
  exportXls: (props) => <IconFileTypeXls {...defaultProps} {...props} />,

  // Alert status icons
  success: (props) => <IconCheck {...defaultProps} {...props} />,
  info: (props) => <IconInfoCircle {...defaultProps} {...props} />,
  warning: (props) => <IconAlertTriangle {...defaultProps} {...props} />,
  danger: (props) => <IconAlertCircle {...defaultProps} {...props} />,
};
