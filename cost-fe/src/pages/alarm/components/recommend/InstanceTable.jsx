export default function InstanceTable({ instances, selected, onToggle, max }) {
  const atLimit = selected.length >= max;
  return (
    <table className="table table-hover align-middle">
      <thead>
        <tr>
          <th style={{ width: 40 }}></th>
          <th>Instance</th>
          <th>Name</th>
          <th>CSP</th>
          <th>Type</th>
          <th>Region</th>
        </tr>
      </thead>
      <tbody>
        {instances.map((inst) => {
          const checked = selected.includes(inst.instanceId);
          return (
            <tr key={inst.instanceId}>
              <td>
                <input
                  type="checkbox"
                  className="form-check-input"
                  checked={checked}
                  disabled={!checked && atLimit}
                  onChange={() => onToggle(inst.instanceId)}
                />
              </td>
              <td><code>{inst.instanceId}</code></td>
              <td>{inst.name}</td>
              <td>{inst.csp}</td>
              <td>{inst.type}</td>
              <td>{inst.region}</td>
            </tr>
          );
        })}
      </tbody>
    </table>
  );
}
