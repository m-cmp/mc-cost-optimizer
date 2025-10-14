import { useState } from "react";
import Modal from "@/components/common/modal/Modal";
import Button from "@/components/common/button/Button";
import InputField from "@/components/common/input/InputField";
import Card from "@/components/common/card/Card";
import { useAlertStore } from "@/stores/useAlertStore";
import { sendAlertMail } from "@/api/alarm/alarm";
import { logger } from "@/utils/logger";

export default function MailTestModal() {
  const [open, setOpen] = useState(false);
  const [to, setTo] = useState("");
  const [title, setTitle] = useState("");
  const [loading, setLoading] = useState(false);

  const { addAlert } = useAlertStore();

  const handleSendMail = async () => {
    try {
      setLoading(true);

      const payload = {
        to: [to],
        subject: title,
        message: "This is a test email.",
      };

      const res = await sendAlertMail(payload);

      if (res.data?.status === "fail") {
        addAlert({
          variant: "danger",
          title: "Error",
          message:
            res.data?.error?.Message || "An error occurred while sending the email.",
        });
      } else {
        addAlert({
          variant: "success",
          title: "Success",
          message: "Email has been sent successfully.",
        });
      }
    } catch (err) {
      logger.error("Mail Test Error:", err);
      addAlert({
        variant: "danger",
        title: "Error",
        message: "An error occurred while sending the email.",
      });
    } finally {
      setLoading(false);
      setOpen(false);
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
        title="Mail Test"
        footer={
          <div className="d-flex justify-content-between w-100">
            <Button
              variant="secondary"
              onClick={() => setOpen(false)}
              disabled={loading}
            >
              Close
            </Button>
            <Button
              variant="primary"
              onClick={handleSendMail}
              disabled={loading}
            >
              {loading ? "Sending..." : "Send Mail"}
            </Button>
          </div>
        }
      >
        <p className="text-muted">
          Only one recipient can be specified for mail testing.
          <br />
          The email will be sent within 30 seconds.
        </p>
        <Card>
          <InputField
            label="Recipient"
            type="text"
            value={to}
            onChange={(e) => setTo(e.target.value)}
            placeholder="Input To"
            dense
            divider
            showRowDivider
          />

          <InputField
            label="Mail Title"
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="Input Mail Title"
            dense
            divider
          />
        </Card>
      </Modal>
    </>
  );
}
