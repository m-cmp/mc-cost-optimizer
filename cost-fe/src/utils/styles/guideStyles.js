// 공통 스타일
const baseGuideStyles = {
  container: {
    width: "100%",
    overflow: "hidden",
    fontFamily: `"Noto Sans KR", sans-serif`,
  },
  guideIMG: {
    margin: "7px 0 15px",
    border: "none",
    maxWidth: "calc(100% - 14px)",
    boxShadow: "1px 1px 2px 2px rgba(0,0,0,0.05)",
    outline: "1px solid #ccc",
    verticalAlign: "middle",
  },
  comment: {
    lineHeight: 1.6,
    display: "block",
    marginBlockStart: "1em",
    marginBlockEnd: "1em",
    fontSize: "0.875rem",
    color: "#333",
  },
  h2: {
    fontSize: "1.25rem",
    fontWeight: 700,
    lineHeight: "1.5rem",
    paddingTop: "24px",
    paddingBottom: "8px",
    margin: "16px 0",
  },
  h4: {
    fontSize: "0.9375rem",
    fontWeight: 700,
    lineHeight: "1.5em",
    margin: "8px 0",
    padding: 0,
  },
};

// Mailing 전용
export const mailingGuideStyles = {
  ...baseGuideStyles,
  guideNote: {
    display: "block",
    marginTop: "15px",
    fontSize: "1rem",
    fontWeight: 400,
    color: "#999",
    lineHeight: "1.6em",
    paddingLeft: "5px",
  },
  blockquote: {
    margin: "10px 0",
    display: "inline-block",
    marginTop: "18px",
    padding: "0 10px",
    fontSize: "0.8125rem",
    color: "#4A4A4A",
    lineHeight: "30px",
    borderRadius: "1px",
    backgroundColor: "#E6EFFA",
    border: "0 none",
  },
};

// Slack 전용
export const slackGuideStyles = {
  ...baseGuideStyles,
  guideNote: {
    display: "block",
    marginTop: "15px",
    fontSize: "1rem",
    fontWeight: 400,
    color: "#666",
    lineHeight: "1.6em",
    paddingLeft: "5px",
  },
  blockquote: {
    margin: "10px 0",
    display: "inline-block",
    marginTop: "18px",
    padding: "0 10px",
    fontSize: "0.8125rem",
    color: "#4A4A4A",
    lineHeight: "30px",
    borderRadius: "1px",
    backgroundColor: "#E6F7FA",
    border: "0 none",
  },
};
