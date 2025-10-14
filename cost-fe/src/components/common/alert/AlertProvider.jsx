import { useAlertStore } from "../../../stores/useAlertStore";
import Alert from "./Alert";

/**
 * @component AlertProvider
 * @description
 * 전역 알림(Toast) 출력 담당 컴포넌트.
 *
 * - Alert를 직접 각 컴포넌트에서 렌더링하지 않고,
 *   상태(`useAlertStore`)에 추가된 알림들을 모아서 Portal로 화면에 띄워줍니다.
 * - 따라서 어디서든 `useAlertStore().addAlert({...})` 만 호출하면
 *   AlertProvider가 자동으로 오른쪽 위에 알림을 표시합니다.
 * - Alert는 Portal로 body 밑 `#global-toast-container`에 고정되므로
 *   버튼이나 레이아웃에 영향을 주지 않습니다.
 *
 * @example
 * // 버튼/모달 컴포넌트에서
 * const { addAlert } = useAlertStore();
 * addAlert({ variant: "success", title: "완료", message: "작업이 성공했습니다." });
 *
 * // 상위 페이지에서
 * <AlertProvider />
 */
export default function AlertProvider() {
  const { alerts, removeAlert } = useAlertStore();

  return (
    <>
      {alerts.map((a) => (
        <Alert
          key={a.id}
          variant={a.variant}
          title={a.title}
          message={a.message}
          dismissible
          duration={a.duration ?? 3000}
          onClose={() => removeAlert(a.id)}
        />
      ))}
    </>
  );
}
