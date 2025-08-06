const canvas = document.getElementById('fireworks');
const ctx = canvas.getContext('2d');

let cw, ch;
function resize() {
    cw = canvas.width = canvas.clientWidth;
    ch = canvas.height = canvas.clientHeight;
}
resize();
window.addEventListener('resize', resize);

// Firework particle class
class Particle {
    constructor(x, y, color) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.radius = Math.random() * 2 + 1;
        this.speedX = (Math.random() - 0.5) * 6;
        this.speedY = (Math.random() - 0.5) * 6;
        this.alpha = 1;
        this.decay = Math.random() * 0.015 + 0.005;
    }
    update() {
        this.x += this.speedX;
        this.y += this.speedY;
        this.alpha -= this.decay;
        this.alpha = Math.max(this.alpha, 0);
    }
    draw() {
        ctx.save();
        ctx.globalAlpha = this.alpha;
        ctx.fillStyle = this.color;
        ctx.beginPath();
        ctx.arc(this.x, this.y, this.radius, 0, Math.PI * 2);
        ctx.fill();
        ctx.restore();
    }
}

// Firework explosion class
class Firework {
    constructor() {
        this.x = Math.random() * cw;
        this.y = Math.random() * ch * 0.7 + ch * 0.15; // avoid edges
        this.particles = [];
        this.color = `hsl(${Math.random() * 360}, 100%, 60%)`;
        this.particleCount = 30 + Math.floor(Math.random() * 20);
        for (let i = 0; i < this.particleCount; i++) {
            this.particles.push(new Particle(this.x, this.y, this.color));
        }
    }
    update() {
        this.particles.forEach(p => p.update());
        this.particles = this.particles.filter(p => p.alpha > 0);
    }
    draw() {
        this.particles.forEach(p => p.draw());
    }
    isDone() {
        return this.particles.length === 0;
    }
}

let fireworks = [];

function animate() {
    ctx.clearRect(0, 0, cw, ch);
        if (fireworks.length < 5 && Math.random() < 0.05) {
            fireworks.push(new Firework());
        }
    fireworks.forEach(fw => {
        fw.update();
        fw.draw();
    });
    fireworks = fireworks.filter(fw => !fw.isDone());

    requestAnimationFrame(animate);
}

animate();

const starField = document.querySelector('.star-field');

const colors = [
    '#FF3C78', // pink
    '#FFAA1D', // orange
    '#FFE600', // yellow
    '#1DE9B6', // turquoise
    '#007AFF', // blue
    '#FF2D55', // red
    '#9C27B0', // purple
    '#00E676'  // green
];

function createStar() {
    const star = document.createElement('div');
    star.classList.add('star');

    const size = Math.random() * 2 + 2;
    const duration = Math.random() * 3 + 2;
    const blur = Math.floor(Math.random() * 10) + 4;
    const brightness = Math.random() * 0.5 + 0.5;

    // Pick a random color for the star
    const color = colors[Math.floor(Math.random() * colors.length)];

    star.style.width = `${size}px`;
    star.style.height = `${size}px`;
    star.style.left = `${Math.random() * 100}%`;
    star.style.top = `-10px`;
    star.style.animationDuration = `${duration}s`;
    star.style.opacity = brightness;
    star.style.backgroundColor = color;
    star.style.boxShadow = `0 0 ${blur}px ${size / 2}px ${color}`;

    starField.appendChild(star);
    setTimeout(() => star.remove(), duration * 1000);
}

setInterval(createStar, 50);

// validation
/*function validateLoginForm() {
    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value.trim();
    let isValid = 0;

    // Clear previous error messages
    document.getElementById("username-error").style.display = "none";
    document.getElementById("password-error").style.display = "none";

    // Check if username is null or whitespace
    if (!username || username.trim() === "") {
        document.getElementById("username-error").innerText = "Username cannot be empty.";
        document.getElementById("username-error").style.display = "block";
        isValid ++;
    } else {
        document.getElementById("username-error").innerText = "";
    }

    // Check if password is null or whitespace
    if (!password || password.trim() === "") {
        document.getElementById("password-error").innerText = "Password cannot be empty.";
        document.getElementById("password-error").style.display = "block";
        isValid ++;
    } else {
        document.getElementById("password-error").innerText = "";
    }
    if (isValid == 0) {
        return true;
    };
    return false;
}*/

function clearError(errorId) {
    document.getElementById(errorId).innerText = "";
    document.getElementById(errorId).style.display = "none";
}


