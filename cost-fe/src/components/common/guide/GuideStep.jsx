const GuideStep = ({ step, img, alt, children, styles }) => {
  return (
    <div>
      <h4 style={styles.h4}>{step}</h4>
      {img && (
        <p>
          <img style={styles.guideIMG} src={img} alt={alt} />
        </p>
      )}
      <div>{children}</div>
    </div>
  );
};

export default GuideStep;
