class SurvivalGame {
  constructor(size = 10) {
    this.size = size;
    this.player = { x: 5, y: 5, hp: 100, hunger: 100, wood: 0, food: 2 };
    this.day = 1;
    this.dead = false;
    this.generateMap();
  }

  generateMap() {
    this.map = [];
    for (let y = 0; y < this.size; y++) {
      const row = [];
      for (let x = 0; x < this.size; x++) {
        const r = Math.random();
        if (r < 0.1) row.push('T');       // tree (wood)
        else if (r < 0.15) row.push('F');  // food
        else if (r < 0.05) row.push('E');  // enemy
        else row.push('.');
      }
      this.map.push(row);
    }
  }

  render() {
    let out = `Day ${this.day} | HP:${this.player.hp} Hunger:${this.player.hunger} Wood:${this.player.wood} Food:${this.player.food}\n`;
    for (let y = 0; y < this.size; y++) {
      let line = '';
      for (let x = 0; x < this.size; x++) {
        line += (x === this.player.x && y === this.player.y) ? 'P' : this.map[y][x];
      }
      out += line + '\n';
    }
    console.log(out);
    return out;
  }

  tick() {
    this.player.hunger -= 5;
    if (this.player.hunger <= 0) {
      this.player.hunger = 0;
      this.player.hp -= 10;
    }
    if (this.player.hp <= 0) {
      console.log('You died on day ' + this.day);
      this.dead = true;
    }
    this.day++;
  }

  move(dir) {
    if (this.dead) return console.log('Game over.');
    const dirs = { w: [0, -1], s: [0, 1], a: [-1, 0], d: [1, 0] };
    const [dx, dy] = dirs[dir] || [0, 0];
    this.player.x = Math.max(0, Math.min(this.size - 1, this.player.x + dx));
    this.player.y = Math.max(0, Math.min(this.size - 1, this.player.y + dy));

    const tile = this.map[this.player.y][this.player.x];
    if (tile === 'T') { this.player.wood++; this.map[this.player.y][this.player.x] = '.'; }
    else if (tile === 'F') { this.player.food++; this.map[this.player.y][this.player.x] = '.'; }
    else if (tile === 'E') { this.player.hp -= 20; this.map[this.player.y][this.player.x] = '.'; console.log('Enemy attacked!'); }

    this.tick();
    this.render();
  }

  eat() {
    if (this.player.food > 0) {
      this.player.food--;
      this.player.hunger = Math.min(100, this.player.hunger + 30);
    } else {
      console.log('No food!');
    }
    this.render();
  }
}

function attachTouchControls(game, element) {
  let startX = 0, startY = 0;

  element.addEventListener('touchstart', (e) => {
    const t = e.touches[0];
    startX = t.clientX;
    startY = t.clientY;
  });

  element.addEventListener('touchend', (e) => {
    const t = e.changedTouches[0];
    const dx = t.clientX - startX;
    const dy = t.clientY - startY;
    const absX = Math.abs(dx), absY = Math.abs(dy);

    if (Math.max(absX, absY) < 20) return; // ignore taps/jitter

    if (absX > absY) {
      game.move(dx > 0 ? 'd' : 'a'); // swipe right/left
    } else {
      game.move(dy > 0 ? 's' : 'w'); // swipe down/up
    }
  });

  // double-tap to eat
  let lastTap = 0;
  element.addEventListener('touchend', () => {
    const now = Date.now();
    if (now - lastTap < 300) game.eat();
    lastTap = now;
  });
}

// Usage:
const game = new SurvivalGame();
game.render();
// attachTouchControls(game, someElementYouAlreadyHave);
