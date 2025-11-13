package com.mcmp.slack_demo.slack.api_client;

import com.mcmp.slack_demo.common.model.costOpti.CostOptiAlarmReqModel;
import com.mcmp.slack_demo.common.service.CommonService;
import com.mcmp.slack_demo.slack.encryto.TokenService;
import com.mcmp.slack_demo.slack.model.SendSlackFormModel;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.model.Attachment;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;

@Service
@Slf4j
public class SlackACService {
    private final TokenService tokenService;

    @Autowired
    public SlackACService(TokenService tokenService) {
        this.tokenService = tokenService;
    }
    @Autowired
    private CommonService commonService;

    public void sendSlack(CostOptiAlarmReqModel costOptiAlarmReqModel) throws SlackApiException, IOException {
        SendSlackFormModel slackFormModel = new SendSlackFormModel();
        BeanUtils.copyProperties(costOptiAlarmReqModel, slackFormModel);
        slackFormModel.setOccure_time(ZonedDateTime.now().toLocalDateTime());
        slackFormModel.setAlarm_impl("slack");

        int checkDuplicateMail = commonService.getSlackDuplicate(slackFormModel);
        if(checkDuplicateMail >= 1){
            log.info("############Send OptiAlertSlack Duplicate : {} - {} - {}############", slackFormModel.getEvent_type(), slackFormModel.getResource_id()
                    , (slackFormModel.getAccount_id() != null ? slackFormModel.getAccount_id() : slackFormModel.getProject_cd()));
            return;
        }

        Slack slack = Slack.getInstance();
        String message = "[MCMP-Notice] Cost Alarm occurred";
        switch (costOptiAlarmReqModel.getEvent_type()){
            case "Unused":
                slackFormModel.setTitle("[MCMP-Notice] Cost Alarm occurred : Caution Unused Resources");
                message = "MCMP Cost에서 미사용 자원 주의 알람이 발생했습니다." +
                        "\n\n" +
                        "CSP : " + slackFormModel.getCsp_type() + "\n" +
                        "리소스 ID : " + slackFormModel.getResource_id() + "\n" +
                        "리소스 Type : " + slackFormModel.getResource_type() + "\n" +
                        "해당 자원이 미사용 자원으로 의심됩니다.";
                break;
            case "Abnormal":
                slackFormModel.setTitle("[MCMP-Notice] Cost Alarm occurred : Warning Abnormal Cost");
                message = "MCMP Cost에서 이상 비용 경고 알람이 발생했습니다." +
                        "\n\n" +
                        "CSP : " + slackFormModel.getCsp_type() + "\n" +
                        "제품군 : " + slackFormModel.getResource_type() + "\n" +
                        "이상비용 등급 : " + slackFormModel.getPlan() + "\n" +
                        "이상비용이 발생했습니다. " + slackFormModel.getNote();
                break;
            case "Resize":
                slackFormModel.setTitle("[MCMP-Notice] Cost Alarm occurred : Advise Right Size Resources");
                message = "MCMP Cost에서 자원 최적화 권고 알람이 발생했습니다." +
                        "\n\n" +
                        "CSP : " + slackFormModel.getCsp_type() + "\n" +
                        "리소스 ID : " + slackFormModel.getResource_id() + "\n" +
                        "리소스 Type : " + slackFormModel.getResource_type() + "\n" +
                        "추천 Plan : " + slackFormModel.getPlan() + "\n" +
                        slackFormModel.getNote();
                break;
            case "Budget":
                String urgencyLevel = "Caution".equals(slackFormModel.getUrgency()) ? "주의" : "위험";
                slackFormModel.setTitle("[MCMP-Notice] Cost Alarm occurred : " +
                        (urgencyLevel.equals("위험") ? "Critical" : "Caution") + " Budget Usage");
                message = "MCMP Cost에서 예산 초과 " + urgencyLevel + " 알람이 발생했습니다." +
                        "\n\n" +
                        "CSP : " + slackFormModel.getCsp_type() + "\n" +
                        "프로젝트 : " + slackFormModel.getProject_cd() + "\n" +
                        "예산 사용률 등급 : " + slackFormModel.getUrgency() + "\n" +
                        slackFormModel.getNote();
                break;
            default:
                log.warn("Unknown event_type: {}", costOptiAlarmReqModel.getEvent_type());
                slackFormModel.setTitle("[MCMP-Notice] Cost Alarm occurred : Unknown Event");
                message = "MCMP Cost에서 알람이 발생했습니다." +
                        "\n\n" +
                        "Event Type : " + costOptiAlarmReqModel.getEvent_type() + "\n" +
                        "CSP : " + slackFormModel.getCsp_type() + "\n" +
                        slackFormModel.getNote();
                break;
        }
        try {
            Map<String, String> result = tokenService.retrieveToken("mcmp-user");

            Attachment attachment = Attachment.builder()
                    .title(slackFormModel.getTitle())
                    .text(message)
                    .color("#36a64f")
                    .fallback(slackFormModel.getTitle())  // fallback 추가 (레거시 필드지만 권장)
                    .build();

            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .channel(result.get("channel"))
                    .text(slackFormModel.getTitle())  // top-level text 추가 (best practice)
                    .attachments(Collections.singletonList(attachment))
                    .build();
            ChatPostMessageResponse response = slack.methods(result.get("token")).chatPostMessage(request);
            if (!response.isOk()) {
                switch (response.getError()) {
                    case "invalid_auth":
                        throw new AuthenticationException("Invalid authentication credentials for Slack.");
                    case "channel_not_found":
                        throw new Exception("The specified channel was not found.");
                    default:
                        throw new Exception("An error occurred with Slack API: " + response.getError());
                }
            }
        } catch (IOException | SlackApiException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        commonService.insertSlackHistory(slackFormModel);
    }

    public void sendMessage(String userId, String message, String linkUrl, String linkText) throws SlackApiException, IOException {
        Slack slack = Slack.getInstance();
        String fullMessage = message;
        if(message.isEmpty()){
            fullMessage = "Slack Test Message";
        }

        if (linkUrl != null && !linkUrl.isEmpty() && linkText != null && !linkText.isEmpty()) {
            fullMessage += " <" + linkUrl + "|" + linkText + ">";
        }

        try {
            Map<String, String> result = tokenService.retrieveToken(userId);

            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .channel(result.get("channel"))
                    .text(fullMessage)
                    .build();
            ChatPostMessageResponse response = slack.methods(result.get("token")).chatPostMessage(request);
            if (!response.isOk()) {
                switch (response.getError()) {
                    case "invalid_auth":
                        throw new AuthenticationException("Invalid authentication credentials for Slack.");
                    case "channel_not_found":
                        throw new Exception("The specified channel was not found.");
                    default:
                        throw new Exception("An error occurred with Slack API: " + response.getError());
                }
            }
        } catch (IOException | SlackApiException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, String> getSlackToken(String userId) throws Exception {
        return tokenService.retrieveToken(userId);
    }
}
