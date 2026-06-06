export default function ProviderSelect({ value, onChange }) {
  return (
    <select
      className="form-select"
      style={{ width: 220 }}
      value={value}
      onChange={(e) => onChange(e.target.value)}
    >
      <option value="gemini">Gemini (gemini-flash-latest)</option>
      <option value="gpt" disabled>GPT (coming soon)</option>
      <option value="claude" disabled>Claude (coming soon)</option>
    </select>
  );
}
