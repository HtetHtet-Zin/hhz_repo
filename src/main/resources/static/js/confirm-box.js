// animated-confirm.js
(function () {
    // Inject CSS once
    const style = document.createElement("style");
    style.textContent = `
        :root{
          --bg: #0f172a;
          --card: #0b1220;
          --accent: #6366f1;
          --danger: #ef4444;
          --muted: #94a3b8;
        }
        .modal-backdrop{
          position:fixed;inset:0;display:flex;align-items:center;justify-content:center;z-index:9999;
          background:rgba(255,255,255,0.01);/*backdrop-filter:blur(1px);*/
          opacity:0;pointer-events:none;transition:opacity .25s ease;
        }
        .modal-backdrop.open{opacity:1;pointer-events:auto}
        .modal-card{
          width:min(420px,92%);
          background:linear-gradient(180deg,rgba(255,255,255),transparent);
          border-radius:14px;padding:18px;
          box-shadow:0 10px 40px rgba(2,6,23,0.7);
          transform:translateY(18px) scale(.996);
          opacity:0;
          transition:transform .32s cubic-bezier(.2,.9,.3,1),opacity .28s ease;
        }
        .modal-backdrop.open .modal-card{transform:translateY(0) scale(1);opacity:1}
        .modal-header{display:flex;align-items:center;gap:12px}
        .badge{width:44px;height:44px;border-radius:10px;background:linear-gradient(135deg,var(--accent),#8b5cf6);
          display:flex;align-items:center;justify-content:center;font-weight:700;color:white;
          box-shadow:0 6px 20px rgba(99,102,241,0.16);
        }
        .modal-title{font-size:18px;font-weight:700;color:#8b5cf6}
        .modal-body{color:var(--muted);margin-top:10px;line-height:1.5}
        .controls{display:flex;justify-content:flex-end;gap:10px;margin-top:18px}
        .btn{border:none;padding:8px 14px;width: ;border-radius:10px;cursor:pointer;font-weight:600;transition:transform .18s ease}
        .btn:hover{transform:translateY(-3px) scale(1.02)}
        .btn.cancel{width: 80px;background:linear-gradient(180deg,rgba(255,255,255),transparent);border:1px solid #8b5cf6;color:#8b5cf6}
        .btn.confirm{width: 80px;background:linear-gradient(90deg,var(--accent),#8b5cf6);color:white}
        body.modal-open{overflow:hidden}
    `;
    document.head.appendChild(style);

    // Create modal HTML
    const template = `
        <div id="confirmModalBackdrop" class="modal-backdrop">
            <div class="modal-card">
                <div class="modal-header">
                    <div class="badge" id="modalBadge">?</div>
                    <div>
                        <div id="modalTitle" class="modal-title">Confirm</div>
                    </div>
                </div>
                <div id="modalBody" class="modal-body">Are you sure?</div>
                <div class="controls">
                    <button class="btn cancel" id="modalCancel">Cancel</button>
                    <button class="btn confirm" id="modalConfirm">OK</button>
                </div>
            </div>
        </div>
    `;
    document.body.insertAdjacentHTML("beforeend", template);

    const backdrop = document.getElementById("confirmModalBackdrop");
    const titleEl = document.getElementById("modalTitle");
    const bodyEl = document.getElementById("modalBody");
    const badgeEl = document.getElementById("modalBadge");
    const confirmBtn = document.getElementById("modalConfirm");
    const cancelBtn = document.getElementById("modalCancel");

    let resolver = null;

    function openModal({ title, message, variant }) {
        titleEl.textContent = title || "Confirm";
        bodyEl.textContent = message || "Are you sure?";
        if (variant === "danger") {
            badgeEl.textContent = "!";
            badgeEl.style.background = "linear-gradient(135deg,var(--danger),#fb7185)";
        } else {
            badgeEl.textContent = "?";
            badgeEl.style.background = "linear-gradient(135deg,var(--accent),#8b5cf6)";
        }

        document.body.classList.add("modal-open");
        backdrop.classList.add("open");

        return new Promise((resolve) => {
            resolver = resolve;
        });
    }

    function closeModal(result) {
        backdrop.classList.remove("open");
        document.body.classList.remove("modal-open");
        if (resolver) {
            resolver(result);
            resolver = null;
        }
    }

    confirmBtn.addEventListener("click", () => closeModal(true));
    cancelBtn.addEventListener("click", () => closeModal(false));
    backdrop.addEventListener("click", (e) => {
        if (e.target === backdrop) closeModal(false);
    });

    // Global function
    window.confirmAction = function (message, { title = "Confirm", variant = "default" } = {}) {
        return openModal({ title, message, variant });
    };
})();
