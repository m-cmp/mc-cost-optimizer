import { useState } from "react";
import Modal from "@/components/common/modal/Modal";
import GuideStep from "@/components/common/guide/GuideStep";
import Button from "@/components/common/button/Button";
import { mailingGuideStyles } from "@/utils/styles/guideStyles";
import InputField from "@/components/common/input/InputField";
import Card from "@/components/common/card/Card";
import { useAlertStore } from "@/stores/useAlertStore";
import { insertMailInfo } from "@/api/alarm/alarm";
import { logger } from "@/utils/logger";

export default function MailingGuideModal() {
  const [open, setOpen] = useState(false);
  const [mailUserId, setMailUserId] = useState("");
  const [mailAppPassword, setMailAppPassword] = useState("");
  const { addAlert } = useAlertStore();

  const handleSave = async () => {
    try {
      const payload = { username: mailUserId, password: mailAppPassword };
      await insertMailInfo(payload);
      addAlert({
        variant: "success",
        title: "Success",
        message: "Mail account information has been saved successfully.",
      });
    } catch (err) {
      logger.error("Insert Mail Info Error:", err);
      addAlert({
        variant: "danger",
        title: "Error",
        message: "An error occurred while saving mail account information.",
      });
    } finally {
      setOpen(false);
    }
  };

  return (
    <>
      <Button variant="primary" onClick={() => setOpen(true)}>
        Mailing Apply/Guide
      </Button>

      <Modal
        id="mailingGuideModal"
        open={open}
        onClose={() => setOpen(false)}
        title="Mailing Guide"
        size="lg"
        centered
        scrollable
        footer={
          <div className="d-flex justify-content-between w-100">
            <Button variant="secondary" onClick={() => setOpen(false)}>
              Close
            </Button>
            <Button variant="primary" onClick={handleSave}>
              Save
            </Button>
          </div>
        }
      >
        <p style={mailingGuideStyles.guideNote}>
          This guide only covers the process of receiving notifications through
          Gmail. <br />
          Please create an account that will serve as the mail provider in
          advance.
        </p>

        <h2 style={mailingGuideStyles.h2}>1. App Password Setup</h2>
        <GuideStep
          styles={mailingGuideStyles}
          img="/images/mailingGuide/mailingGuide1.png"
          alt="Slide 1"
        >
          <p style={mailingGuideStyles.comment}>
            Access Google Account settings and select the{" "}
            <strong>Security</strong> tab on the left to set up app password.
          </p>
        </GuideStep>

        <h2 style={mailingGuideStyles.h2}>2. Two-Step Verification Setup</h2>
        <GuideStep
          styles={mailingGuideStyles}
          img="/images/mailingGuide/mailingGuide2.png"
          alt="Slide 2"
          step="2-1."
        >
          <p style={mailingGuideStyles.comment}>
            Register two-step verification for your Google account.
          </p>
          <blockquote style={mailingGuideStyles.blockquote}>
            <p style={mailingGuideStyles.comment}>
              App passwords can only be used on accounts with{" "}
              <strong>Two-Step Verification</strong> enabled. <br />
              If 'App passwords' doesn't appear, please set up
              <strong>Two-Step Verification</strong> first.
            </p>
          </blockquote>
        </GuideStep>

        <GuideStep
          styles={mailingGuideStyles}
          step="2-2."
          img="/images/mailingGuide/mailingGuide3.png"
          alt="Slide 3"
        >
          <p style={mailingGuideStyles.comment}>
            Navigate to{" "}
            <span style={{ fontWeight: "bold", textDecoration: "underline" }}>
              2-Step Verification &gt; Signing in with 2-Step Verification &gt;
              Sign in with app passwords
            </span>{" "}
            and click Create and manage your app passwords.
          </p>
        </GuideStep>

        <GuideStep
          styles={mailingGuideStyles}
          step="2-3."
          img="/images/mailingGuide/mailingGuide4.png"
          alt="Slide 4"
        >
          <p style={mailingGuideStyles.comment}>
            Set the app name for which you want to generate an app password.
          </p>
        </GuideStep>

        <GuideStep
          styles={mailingGuideStyles}
          step="2-4."
          img="/images/mailingGuide/mailingGuide5.png"
          alt="Slide 5"
        >
          <p style={mailingGuideStyles.comment}>
            An app password will be generated.
          </p>
          <blockquote style={mailingGuideStyles.blockquote}>
            <p style={mailingGuideStyles.comment}>
              The generated app password is required for the final step of MCMP
              Mail notification setup. <br />
              Since the app password
              <strong>
                <u> cannot be viewed again </u>
              </strong>
              , please remember it well.
            </p>
          </blockquote>
        </GuideStep>

        <h2 style={mailingGuideStyles.h2}>3. Mail Account Setup</h2>
        <p style={mailingGuideStyles.comment}>
          Enter your Mail User ID and App Password in the form below and save.
        </p>
        <p style={mailingGuideStyles.comment}>
          You can test the mail integration using the Mail Test button outside
          this modal.
        </p>
        <blockquote style={mailingGuideStyles.blockquote}>
          <p style={mailingGuideStyles.comment}>
            Please enter the Gmail account ID that will send emails and the App
            Password generated in <strong>step 2-3</strong>.
          </p>
          <p style={mailingGuideStyles.comment}>
            <strong>
              Mail User ID and App Password are saved each time you click the
              save button, and you don't need to enter them again after saving
              once.
            </strong>
          </p>
        </blockquote>

        {/* 입력폼 */}
        <Card>
          <InputField
            label="Mail User ID"
            value={mailUserId}
            onChange={(e) => setMailUserId(e.target.value)}
            placeholder="Input Mail User ID"
            dense
            divider
            showRowDivider
          />
          <InputField
            label="Mail App Password"
            type="password"
            value={mailAppPassword}
            onChange={(e) => setMailAppPassword(e.target.value)}
            placeholder="Input Mail App Password"
            dense
            divider
            inputMaxWidth="350px"
          />
        </Card>
      </Modal>
    </>
  );
}
