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