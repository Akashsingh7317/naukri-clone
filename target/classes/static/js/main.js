// ===== NaukriHub Main JS =====

document.addEventListener('DOMContentLoaded', () => {

  // ---- Scroll reveal ----
  const reveals = document.querySelectorAll('.reveal');
  const io = new IntersectionObserver((entries) => {
    entries.forEach((e, i) => {
      if (e.isIntersecting) {
        setTimeout(() => e.target.classList.add('visible'), i * 80);
        io.unobserve(e.target);
      }
    });
  }, { threshold: 0.1 });
  reveals.forEach(el => io.observe(el));

  // ---- Auto dismiss alerts ----
  document.querySelectorAll('.alert').forEach(a => {
    setTimeout(() => {
      a.style.transition = 'opacity 0.5s, transform 0.5s';
      a.style.opacity = '0';
      a.style.transform = 'translateY(-8px)';
      setTimeout(() => a.remove(), 500);
    }, 5000);
  });

  // ---- Navbar shadow on scroll ----
  const navbar = document.querySelector('.navbar');
  if (navbar) {
    window.addEventListener('scroll', () => {
      navbar.style.boxShadow = window.scrollY > 40
        ? '0 4px 30px rgba(0,0,0,0.12)'
        : '0 2px 20px rgba(0,0,0,0.06)';
    }, { passive: true });
  }

  // ---- Job card click ----
  document.querySelectorAll('.job-card[data-href], .job-list-card[data-href]').forEach(card => {
    card.style.cursor = 'pointer';
    card.addEventListener('click', function(e) {
      if (!e.target.closest('a, button, form')) {
        window.location.href = this.dataset.href;
      }
    });
  });

  // ---- Search form submit button state ----
  const searchForms = document.querySelectorAll('form[data-search]');
  searchForms.forEach(form => {
    form.addEventListener('submit', () => {
      const btn = form.querySelector('[type=submit]');
      if (btn) { btn.disabled = true; btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Searching...'; }
    });
  });

  // ---- Role picker ----
  document.querySelectorAll('.role-opt').forEach(opt => {
    opt.addEventListener('click', function() {
      document.querySelectorAll('.role-opt').forEach(o => o.classList.remove('selected'));
      this.classList.add('selected');
      this.querySelector('input[type=radio]').checked = true;
    });
  });

  // ---- File upload drag & drop ----
  const uploadArea = document.querySelector('.file-upload-area');
  if (uploadArea) {
    uploadArea.addEventListener('dragover', e => { e.preventDefault(); uploadArea.classList.add('dragover'); });
    uploadArea.addEventListener('dragleave', () => uploadArea.classList.remove('dragover'));
    uploadArea.addEventListener('drop', e => {
      e.preventDefault();
      uploadArea.classList.remove('dragover');
      const file = e.dataTransfer.files[0];
      if (file) handleFileSelect(file);
    });
    const fileInput = uploadArea.querySelector('input[type=file]');
    if (fileInput) {
      fileInput.addEventListener('change', () => {
        if (fileInput.files[0]) handleFileSelect(fileInput.files[0]);
      });
    }
  }

  // ---- Profile strength ----
  calcProfileStrength();
});

function handleFileSelect(file) {
  const allowed = ['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'];
  if (!allowed.includes(file.type)) { showToast('Only PDF or Word files allowed', 'error'); return; }
  if (file.size > 10 * 1024 * 1024) { showToast('File too large (max 10MB)', 'error'); return; }
  const preview = document.getElementById('filePreview');
  if (preview) {
    preview.innerHTML = `
      <div class="uploaded-file">
        <i class="fas fa-file-pdf" style="color:var(--danger);font-size:1.3rem"></i>
        <span class="file-name">${file.name}</span>
        <span class="remove-file" onclick="removeFile()"><i class="fas fa-times"></i></span>
      </div>`;
    preview.style.display = 'block';
  }
  showToast('Resume selected: ' + file.name, 'success');
}

function removeFile() {
  const preview = document.getElementById('filePreview');
  const fileInput = document.querySelector('.file-upload-area input[type=file]');
  if (preview) preview.innerHTML = '';
  if (fileInput) fileInput.value = '';
}

function openModal(id) {
  const m = document.getElementById(id);
  if (m) { m.classList.add('open'); document.body.style.overflow = 'hidden'; }
}

function closeModal(id) {
  const m = document.getElementById(id);
  if (m) { m.classList.remove('open'); document.body.style.overflow = ''; }
}

// Close modal on backdrop click
document.addEventListener('click', e => {
  if (e.target.classList.contains('modal-overlay')) {
    e.target.classList.remove('open');
    document.body.style.overflow = '';
  }
});

// Close on ESC
document.addEventListener('keydown', e => {
  if (e.key === 'Escape') {
    document.querySelectorAll('.modal-overlay.open').forEach(m => {
      m.classList.remove('open');
      document.body.style.overflow = '';
    });
  }
});

function showToast(msg, type = 'info') {
  const colors = { success: '#10b981', error: '#ef4444', info: '#4f46e5', warning: '#f59e0b' };
  const icons = { success: '✅', error: '❌', info: 'ℹ️', warning: '⚠️' };
  const t = document.createElement('div');
  t.style.cssText = `
    position:fixed;bottom:24px;right:24px;z-index:99999;
    background:${colors[type]};color:white;
    padding:14px 20px;border-radius:12px;
    font-weight:600;font-family:'Poppins',sans-serif;font-size:0.88rem;
    box-shadow:0 8px 30px rgba(0,0,0,0.2);
    display:flex;align-items:center;gap:10px;
    animation:slideUp 0.4s ease;max-width:340px;
  `;
  t.innerHTML = `<span>${icons[type]}</span><span>${msg}</span>`;
  document.body.appendChild(t);
  setTimeout(() => { t.style.opacity = '0'; t.style.transform = 'translateY(8px)'; t.style.transition = 'all 0.4s'; setTimeout(() => t.remove(), 400); }, 3500);
}

function calcProfileStrength() {
  const bar = document.getElementById('strengthBar');
  const label = document.getElementById('strengthLabel');
  if (!bar || !label) return;
  const pct = parseInt(bar.dataset.pct || 0);
  setTimeout(() => { bar.style.width = pct + '%'; }, 400);
  label.textContent = pct < 40 ? 'Basic' : pct < 70 ? 'Good' : pct < 90 ? 'Strong' : 'Complete';
}

// Popular search tag click
function searchTag(val) {
  const inp = document.querySelector('input[name="keyword"]');
  if (inp) { inp.value = val; inp.closest('form').submit(); }
}
