/**
 * @component InputField
 * @description 라벨 + 인풋을 한 줄에 표시하는 컴포넌트
 *
 * @prop {string} label - 인풋 좌측에 표시할 라벨 텍스트
 * @prop {string} [type="text"] - 인풋 타입 (text, password, email 등)
 * @prop {string} value - 인풋 값
 * @prop {function} onChange - 값 변경 핸들러
 * @prop {string} [placeholder] - placeholder 텍스트
 * @prop {number} [labelWidth=3] - 좌측 라벨 col 비율
 * @prop {number} [inputWidth=9] - 우측 인풋 col 비율
 * @prop {boolean} [showRowDivider=false] - 아래 가로 구분선 표시 여부
 */
export default function InputField({
  label,
  type = "text",
  value,
  onChange,
  placeholder,
  labelWidth = 5,
  inputWidth = 7,
  showRowDivider = false,
}) {
  return (
    <>
      <div className="mb-2 row align-items-start">
        {/* 라벨 */}
        <label className={`col-${labelWidth} col-form-label text-start`}>
          <strong>{label}</strong>
        </label>

        {/* 인풋 */}
        <div className={`col-${inputWidth}`}>
          <input
            type={type}
            className="form-control"
            value={value}
            onChange={onChange}
            placeholder={placeholder}
          />
        </div>
      </div>

      {/* 구분선 */}
      {showRowDivider && (
        <hr
          style={{
            border: "0",
            borderTop: "2px solid #ccc",
            margin: "6px 0",
          }}
        />
      )}
    </>
  );
}
