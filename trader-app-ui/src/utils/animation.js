const scrollToAnimated = (scrollTarget, duration) => {
    var initialY = document.body.scrollTop;
    var baseY = (initialY + scrollTarget) * 0.5;
    var difference = initialY - baseY;
    var startTime = performance.now();

    const step = () => {
        var normalizedTime = (performance.now() - startTime) / duration;
        if (normalizedTime > 1) normalizedTime = 1;

        window.scrollTo(0, baseY + difference * Math.cos(normalizedTime * Math.PI));
        if (normalizedTime < 1) window.requestAnimationFrame(step);
    }

    window.requestAnimationFrame(step);
}

export { scrollToAnimated }