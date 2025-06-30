// --- Variables Globales ---
let scene, camera, renderer;
let particles, lines;
let isMouseDown = false;
const PARTICLE_COUNT = 800;
const MAX_CONNECTION_DISTANCE = 120;

const mouse = new THREE.Vector2(-1000, -1000);
const cursorDot = document.querySelector('.cursor-dot');

// --- Función de Inicialización ---
function init() {
    scene = new THREE.Scene();
    camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 1, 10000);
    camera.position.z = 600;

    // Hacemos el fondo del renderizador transparente para que el CSS del body se vea
    renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
    renderer.setSize(window.innerWidth, window.innerHeight);
    renderer.setPixelRatio(window.devicePixelRatio);
    // El canvas se añade al body y el CSS se encarga de posicionarlo detrás del resto del contenido
    document.body.appendChild(renderer.domElement);

    createWeb();

    window.addEventListener('resize', onWindowResize, false);
    document.addEventListener('mousemove', onMouseMove, false);
    document.addEventListener('mouseleave', onMouseLeave, false);
    document.addEventListener('mousedown', onMouseDown, false);
    document.addEventListener('mouseup', onMouseUp, false);
}

// --- Creación de la Red ---
function createWeb() {
    const particleGeometry = new THREE.BufferGeometry();
    const positions = new Float32Array(PARTICLE_COUNT * 3);
    const velocities = new Float32Array(PARTICLE_COUNT * 3);
    const originalPositions = new Float32Array(PARTICLE_COUNT * 3);

    for (let i = 0; i < PARTICLE_COUNT; i++) {
        const i3 = i * 3;
        positions[i3] = (Math.random() - 0.5) * 1500;
        positions[i3 + 1] = (Math.random() - 0.5) * 1500;
        positions[i3 + 2] = (Math.random() - 0.5) * 1500;
        
        originalPositions[i3] = positions[i3];
        originalPositions[i3+1] = positions[i3+1];
        originalPositions[i3+2] = positions[i3+2];

        velocities[i3] = (Math.random() - 0.5) * 0.2;
        velocities[i3+1] = (Math.random() - 0.5) * 0.2;
        velocities[i3+2] = (Math.random() - 0.5) * 0.2;
    }
    particleGeometry.setAttribute('position', new THREE.BufferAttribute(positions, 3));
    particleGeometry.setAttribute('velocity', new THREE.BufferAttribute(velocities, 3));
    particleGeometry.setAttribute('originalPosition', new THREE.BufferAttribute(originalPositions, 3));

    const particleMaterial = new THREE.PointsMaterial({
        color: 0x00ffff,
        size: 2,
        transparent: true,
        opacity: 0.7,
        blending: THREE.AdditiveBlending,
        sizeAttenuation: true
    });

    particles = new THREE.Points(particleGeometry, particleMaterial);
    scene.add(particles);

    const lineGeometry = new THREE.BufferGeometry();
    const linePositions = new Float32Array(PARTICLE_COUNT * PARTICLE_COUNT * 3 * 2); 
    lineGeometry.setAttribute('position', new THREE.BufferAttribute(linePositions, 3));

    const lineMaterial = new THREE.LineBasicMaterial({
        color: 0xffffff,
        transparent: true,
        opacity: 0.1,
        blending: THREE.AdditiveBlending
    });

    lines = new THREE.LineSegments(lineGeometry, lineMaterial);
    scene.add(lines);
}

// --- Bucle de Animación ---
function animate() {
    requestAnimationFrame(animate);
    updateParticles();
    updateLines();
    render();
}

// --- Actualización de Partículas ---
function updateParticles() {
    const positions = particles.geometry.attributes.position.array;
    const velocities = particles.geometry.attributes.velocity.array;
    const originalPositions = particles.geometry.attributes.originalPosition.array;

    const vector = new THREE.Vector3(mouse.x, mouse.y, 0.5);
    vector.unproject(camera);
    const dir = vector.sub(camera.position).normalize();
    const distance = -camera.position.z / dir.z;
    const mouseWorldPos = camera.position.clone().add(dir.multiplyScalar(distance));
    
    const currentInfluenceRadius = isMouseDown ? 300 : 150;
    const attractionMultiplier = isMouseDown ? 0.001 : 0.0005;

    for (let i = 0; i < PARTICLE_COUNT; i++) {
        const i3 = i * 3;
        
        const particlePos = new THREE.Vector3(positions[i3], positions[i3+1], positions[i3+2]);
        const originalPos = new THREE.Vector3(originalPositions[i3], originalPositions[i3+1], originalPositions[i3+2]);

        let distToMouse = particlePos.distanceTo(mouseWorldPos);
        if (distToMouse < currentInfluenceRadius) {
            const attractForce = new THREE.Vector3().subVectors(mouseWorldPos, particlePos);
            attractForce.multiplyScalar(attractionMultiplier);
            
            velocities[i3] += attractForce.x;
            velocities[i3 + 1] += attractForce.y;
            velocities[i3 + 2] += attractForce.z;
        }

        const springForce = new THREE.Vector3().subVectors(originalPos, particlePos).multiplyScalar(0.0002);
        velocities[i3] += springForce.x;
        velocities[i3 + 1] += springForce.y;
        velocities[i3 + 2] += springForce.z;

        positions[i3] += velocities[i3];
        positions[i3 + 1] += velocities[i3 + 1];
        positions[i3 + 2] += velocities[i3 + 2];
        
        velocities[i3] *= 0.97;
        velocities[i3 + 1] *= 0.97;
        velocities[i3 + 2] *= 0.97;
    }

    particles.geometry.attributes.position.needsUpdate = true;
}

// --- Actualización de Líneas ---
function updateLines() {
    const particlePositions = particles.geometry.attributes.position.array;
    const linePositions = lines.geometry.attributes.position.array;
    let lineIndex = 0;

    for (let i = 0; i < PARTICLE_COUNT; i++) {
        for (let j = i + 1; j < PARTICLE_COUNT; j++) {
            const i3 = i * 3;
            const j3 = j * 3;
            
            const dx = particlePositions[i3] - particlePositions[j3];
            const dy = particlePositions[i3 + 1] - particlePositions[j3 + 1];
            const dz = particlePositions[i3 + 2] - particlePositions[j3 + 2];
            const dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (dist < MAX_CONNECTION_DISTANCE) {
                linePositions[lineIndex++] = particlePositions[i3];
                linePositions[lineIndex++] = particlePositions[i3 + 1];
                linePositions[lineIndex++] = particlePositions[i3 + 2];
                linePositions[lineIndex++] = particlePositions[j3];
                linePositions[lineIndex++] = particlePositions[j3 + 1];
                linePositions[lineIndex++] = particlePositions[j3 + 2];
            }
        }
    }
    lines.geometry.setDrawRange(0, lineIndex / 3);
    lines.geometry.attributes.position.needsUpdate = true;
}

// --- Renderizado y Cámara ---
function render() {
    camera.position.x += (mouse.x * 100 - camera.position.x) * 0.05;
    camera.position.y += (-mouse.y * 100 - camera.position.y) * 0.05;
    camera.lookAt(scene.position);
    renderer.render(scene, camera);
}

// --- Manejadores de Eventos ---
function onWindowResize() {
    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();
    renderer.setSize(window.innerWidth, window.innerHeight);
}

function onMouseMove(event) {
    mouse.x = (event.clientX / window.innerWidth) * 2 - 1;
    mouse.y = -(event.clientY / window.innerHeight) * 2 + 1;
    cursorDot.style.left = `${event.clientX}px`;
    cursorDot.style.top = `${event.clientY}px`;
    cursorDot.style.opacity = 1;
}

function onMouseLeave() {
    mouse.x = -1000;
    mouse.y = -1000;
    cursorDot.style.opacity = 0;
    isMouseDown = false; 
    cursorDot.style.transform = 'translate(-50%, -50%) scale(1)';
}

// --- NUEVAS FUNCIONES PARA EL CLIC ---
function onMouseDown() {
    isMouseDown = true;
    cursorDot.style.transform = 'translate(-50%, -50%) scale(2.5)';
}

function onMouseUp() {
    isMouseDown = false;
    cursorDot.style.transform = 'translate(-50%, -50%) scale(1)';
}

// --- Iniciar la aplicación ---
init();
animate(); 