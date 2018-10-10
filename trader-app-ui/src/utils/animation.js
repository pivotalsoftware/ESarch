const scrollToAnimated = (scrollTarget, duration) => {
  const initialY = document.body.scrollTop;
  const baseY = (initialY + scrollTarget) * 0.5;
  const difference = initialY - baseY;
  const startTime = performance.now();

  const step = () => {
    let normalizedTime = (performance.now() - startTime) / duration;
    if (normalizedTime > 1) normalizedTime = 1;

    window.scrollTo(0, baseY + difference * Math.cos(normalizedTime * Math.PI));
    if (normalizedTime < 1) window.requestAnimationFrame(step);
  };

  window.requestAnimationFrame(step);
};

export { scrollToAnimated };
