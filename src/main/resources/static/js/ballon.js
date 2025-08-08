function createBalloon() {
  const balloonContainer = document.getElementById('balloon-container');
  const balloon = document.createElement('div');
  balloon.classList.add('balloon');

balloon.style.bottom = '0px';
balloon.style.position = 'absolute';

  // Randomize position and color
  balloon.style.left = `${Math.random() * 100}%`;
  balloon.style.backgroundColor = getRandomColor();

  // Randomize animation duration
  const duration = 4 + Math.random() * 4;
  balloon.style.animationDuration = `${duration}s`;

  balloonContainer.appendChild(balloon);

  // Remove balloon after animation ends
  setTimeout(() => {
    balloon.remove();
  }, duration * 1000);
}

function getRandomColor() {
  const colors = ['#ff6b6b', '#feca57', '#1dd1a1', '#5f27cd', '#54a0ff', '#ff9ff3'];
  return colors[Math.floor(Math.random() * colors.length)];
}

// Continuously generate balloons
setInterval(createBalloon, 800);
