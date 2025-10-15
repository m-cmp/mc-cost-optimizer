import React, { useState } from "react";
import Modal from "@/components/common/modal/Modal";
import GuideStep from "@/components/common/guide/GuideStep";
import Button from "@/components/common/button/Button";
import { slackGuideStyles } from "@/utils/styles/guideStyles";
import InputField from "@/components/common/input/InputField";
import Card from "@/components/common/card/Card";
import Alert from "@/components/common/alert/Alert";
import { insertSlackToken } from "@/api/alarm/alarm";
import { useAlertStore } from "@/stores/useAlertStore";
import { logger } from "@/utils/logger";

export default function SlackGuideModal() {
  const [open, setOpen] = useState(false);
  const [slackToken, setSlackToken] = useState("");
  const [channelId, setChannelId] = useState("");
  const { addAlert } = useAlertStore();

  const handleSave = async () => {
    try {
      const payload = {
        id: "mcmp-user", // Fixed value
        token: slackToken,
        channel: channelId,
      };

      await insertSlackToken(payload);
      addAlert({
        variant: "success",
        title: "Success",
        message: "Slack Token and Channel ID have been saved successfully.",
      });
    } catch (err) {
      logger.error("Insert Slack Token Error:", err);
      addAlert({
        variant: "danger",
        title: "Error",
        message: "An error occurred while saving Slack Token.",
      });
    } finally {
      setOpen(false);
    }
  };

  return (
    <>
      <Button variant="primary" onClick={() => setOpen(true)}>
        Slack Apply/Guide
      </Button>

      <Modal
        id="slackGuideModal"
        open={open}
        onClose={() => setOpen(false)}
        title="Slack Apply/Guide"
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
        <p style={slackGuideStyles.guideNote}>
          This guide covers the process of receiving notifications through Slack
          only. <br />
          It does not include the registration process for cases where a Slack
          account does not exist.
        </p>

        <h2 style={slackGuideStyles.h2}>
          1. Slack ChatBot Creation and Permission Setup
        </h2>
        <GuideStep
          styles={slackGuideStyles}
          step="1-1."
          img="/images/slackGuide/slackIMG01.png"
          alt="Slide 1"
        >
          <p style={slackGuideStyles.comment}>
            Go to https://api.slack.com and click <strong>'Your apps'</strong>.
          </p>
        </GuideStep>

        <GuideStep
          styles={slackGuideStyles}
          step="1-2."
          img="/images/slackGuide/slackIMG02.png"
          alt="Slide 2"
        >
          <p style={slackGuideStyles.comment}>
            Click the <strong>'Create New App'</strong> button.
          </p>
        </GuideStep>

        <GuideStep
          styles={slackGuideStyles}
          step="1-3."
          img="/images/slackGuide/slackIMG03.png"
          alt="Slide 3"
        >
          <p style={slackGuideStyles.comment}>
            Select <strong>'From scratch'</strong> for the app settings to be
            created.
          </p>
        </GuideStep>

        <GuideStep
          styles={slackGuideStyles}
          step="1-4."
          img="/images/slackGuide/slackIMG04.png"
          alt="Slide 4"
        >
          <blockquote style={slackGuideStyles.blockquote}>
            <p style={slackGuideStyles.comment}>
              <strong>App Name</strong> : This is the name of the chatbot app
              that will send notifications.
            </p>
          </blockquote>
          <p style={slackGuideStyles.comment}>
            Specify the workspace where you want to create the chatbot app and
            click <strong>'Create App'</strong>.
          </p>
        </GuideStep>

        <GuideStep
          styles={slackGuideStyles}
          step="1-5."
          img="/images/slackGuide/slackIMG05-1.png"
          alt="Slide 5"
        >
          <p style={slackGuideStyles.comment}>
            To grant permissions for the Bot app to send notifications, go to
            the <strong>'OAuth & Permissions'</strong> tab and navigate to the{" "}
            <strong>'Scopes'</strong> section. <br />
            <br />
            Click the <strong>'Add an OAuth Scope'</strong> button and allow the
            <strong>'chat:write' permission</strong> so the Bot app can send
            notifications.
          </p>
        </GuideStep>

        <h2 style={slackGuideStyles.h2}>
          2. Slack App Installation and Token Generation
        </h2>
        <GuideStep
          styles={slackGuideStyles}
          step="2-1."
          img="/images/slackGuide/slackIMG07.png"
          alt="Slide 6"
        >
          <p style={slackGuideStyles.comment}>
            To install the app you've configured so far to your workspace, click
            the <strong>'Install to Workspace'</strong> button.
          </p>
        </GuideStep>

        <GuideStep
          styles={slackGuideStyles}
          step="2-2."
          img="/images/slackGuide/slackIMG07-1.png"
          alt="Slide 7"
        >
          <p style={slackGuideStyles.comment}>
            Allow permissions for the created app to access and be installed in
            your workspace.
          </p>
        </GuideStep>

        <GuideStep
          styles={slackGuideStyles}
          step="2-3."
          img="/images/slackGuide/slackIMG08.png"
          alt="Slide 8"
        >
          <p style={slackGuideStyles.comment}>
            Once the app installation is complete, you will{" "}
            <strong>receive an OAuth Token</strong> for the app.
          </p>
          <blockquote style={slackGuideStyles.blockquote}>
            <p style={slackGuideStyles.comment}>
              <strong>
                The generated token is required for the final step of MCMP
                notification setup, and care must be taken to ensure the token
                is not exposed externally.
              </strong>
            </p>
          </blockquote>
        </GuideStep>

        <GuideStep
          styles={slackGuideStyles}
          step="2-4."
          img="/images/slackGuide/slackIMG10-1.png"
          alt="Slide 10"
        >
          <p style={slackGuideStyles.comment}>
            Open the Slack App and navigate to the workspace and channel you
            created, then click the channel name (e.g., #alarm-channel). <br />
            In the upper right corner, click{" "}
            <strong>'View all members of this channel'</strong> and then click
            the <strong>'Add apps'</strong> button in the{" "}
            <strong>'Integrations'</strong> tab.
          </p>
        </GuideStep>

        <GuideStep
          styles={slackGuideStyles}
          step="2-5."
          img="/images/slackGuide/slackIMG12.png"
          alt="Slide 12"
        >
          <p style={slackGuideStyles.comment}>
            Add the created app to the channel.
          </p>
        </GuideStep>

        <GuideStep
          styles={slackGuideStyles}
          step="2-6."
          img="/images/slackGuide/slackIMG13.png"
          alt="Slide 13"
        >
          <p style={slackGuideStyles.comment}>
            Return to the channel information window and check the{" "}
            <strong>Channel ID</strong>.
          </p>
          <blockquote style={slackGuideStyles.blockquote}>
            <p style={slackGuideStyles.comment}>
              <strong>
                The confirmed channel ID is required for the final step of MCMP
                notification setup.
              </strong>
            </p>
          </blockquote>
        </GuideStep>

        <h2 style={slackGuideStyles.h2}>3. MCMP Slack Alarm Integration</h2>
        <GuideStep
          styles={slackGuideStyles}
          step="3-1."
          img="/images/slackGuide/slackIMG14.png"
          alt="Slide 14"
        >
          <p style={slackGuideStyles.comment}>
            You can open the Slack chatbot creation and token generation guide.
          </p>
          <p style={slackGuideStyles.comment}>
            <strong>Enter the generated OAuth Token and Channel ID.</strong>
          </p>
        </GuideStep>

        <GuideStep
          styles={slackGuideStyles}
          step="3-2."
          img="/images/slackGuide/slackIMG15.png"
          alt="Slide 15"
        >
          <p style={slackGuideStyles.comment}>
            <strong>Save</strong> the entered Token and Channel ID.
          </p>
          <p style={slackGuideStyles.comment}>
            After completing the Slack integration setup, you can run alarm
            tests using the Slack Test button.
          </p>
        </GuideStep>

        <h2 style={slackGuideStyles.h2}>4. MCMP Slack Integration Setup</h2>
        <blockquote style={slackGuideStyles.blockquote}>
          <p style={slackGuideStyles.comment}>
            Please enter the Workspace OAuth Token generated in{" "}
            <strong>step 2-2</strong> and the Channel ID confirmed in{" "}
            <strong>step 2-7</strong>.
          </p>
          <p style={slackGuideStyles.comment}>
            <strong>
              Slack App Token and Channel ID are saved each time you click the
              save button, and you don't need to enter them again after saving
              once.
            </strong>
          </p>
        </blockquote>

        {/* 입력폼 */}
        <Card>
          <InputField
            label="Slack App Token"
            value={slackToken}
            onChange={(e) => setSlackToken(e.target.value)}
            placeholder="Input Slack App Token"
            dense
            divider
            inputMaxWidth="350px"
            showRowDivider
          />

          <InputField
            label="Channel ID"
            value={channelId}
            onChange={(e) => setChannelId(e.target.value)}
            placeholder="Input Slack Channel ID"
            dense
            divider
            inputMaxWidth="350px"
          />
        </Card>
      </Modal>
    </>
  );
}
