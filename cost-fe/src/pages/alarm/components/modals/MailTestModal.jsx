import { useState } from "react";
import Modal from "@/components/common/modal/Modal";
import Button from "@/components/common/button/Button";
import InputField from "@/components/common/input/InputField";
import Card from "@/components/common/card/Card";
import { useAlertStore } from "@/stores/useAlertStore";
import { saveMailReceiver, sendAlertMail } from "@/api/alarm/alarm";
import { logger } from "@/utils/logger";

export default function MailTestModal() {
  const [open, setOpen] = useState(false);
  const [email, setEmail] = useState("");
  const [loading, setLoading] = useState(false);

  const { addAlert } = useAlertStore();

  const handleSaveAndTest = async () => {
    if (!email.trim()) {
      addAlert({ variant: "warning", title: "Required", message: "Please enter a recipient email." });
      return;
    }
    try {
      setLoading(true);

      await saveMailReceiver(email.trim());

      const res = await sendAlertMail({
        to: [email.trim()],
        subject: "MCMP Mail Notification Test",
        message: "This is a test email from MCMP. Mail notification is configured successfully.",
      });

      if (res.data?.status === "fail") {
        addAlert({
          variant: "danger",
          title: "Send Failed",
          message: res.data?.error?.Message || "Recipient saved, but test email failed to send.",
        });
      } else {
        addAlert({
          variant: "success",
          title: "Success",
          message: `Recipient saved and test email sent to ${email.trim()}.`,
        });
        setOpen(false);
      }
    } catch (err) {
      logger.error("Mail save & test error:", err);
      addAlert({
        variant: "danger",
        title: "Error",
        message: "An error occurred. Please check sender account settings.",
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <Button variant="outline-primary" onClick={() => setOpen(true)}>
        Mail Test
      </Button>

      <Modal
        id="mailTestModal"
        open={open}
        onClose={() => setOpen(false)}
        title="Mail Recipient Setup & Test"
        footer={
          <div className="d-flex justify-content-between w-100">
            <Button variant="secondary" onClick={() => setOpen(false)} disabled={loading}>
              Close
            </Button>
            <Button variant="primary" onClick={handleSaveAndTest} disabled={loading}>
              {loading ? "Processing..." : "Save & Send Test"}
            </Button>
          </div>
        }
      >
        <p className="text-muted" style={{ fontSize: 14 }}>
          Enter the recipient email address. It will be saved as the alarm notification target and a test email will be sent immediately.
        </p>
        <Card>
          <InputField
            label="Recipient Email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="recipient@example.com"
            dense
            divider
          />
        </Card>
      </Modal>
    </>
  );
}
