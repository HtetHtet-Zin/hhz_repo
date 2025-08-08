document.addEventListener("DOMContentLoaded", () => {

  // Cache DOM elements
  const inchargeInput = document.getElementById("inchargePerson");
  const personList = document.getElementById("personList");
  const personSearch = document.getElementById("personSearch");

  const memberList = document.getElementById("memberList");
  const memberSearch = document.getElementById("memberSearch");
  const selectedMember = document.getElementById("selectedMember");
  const monthSelect = document.getElementById("monthSelect");
  const supportedList = document.getElementById("supportedList");

  // --- Person Modal Search Filter ---
  personSearch.addEventListener("input", () => {
    const filter = personSearch.value.toLowerCase();
    Array.from(personList.children).forEach(li => {
      const text = li.textContent.toLowerCase();
      li.style.display = text.includes(filter) ? "" : "none";
    });
  });

  // --- Member Modal Search Filter ---
  memberSearch.addEventListener("input", () => {
    const filter = memberSearch.value.toLowerCase();
    selectedMember.value = ""; // reset on typing new search
    Array.from(memberList.children).forEach(li => {
      const text = li.textContent.toLowerCase();
      li.style.display = text.includes(filter) ? "" : "none";
    });
    // Ensure member list visible while typing
    memberList.style.display = "";
  });


  // --- Modal Show Event Handlers ---
  document.getElementById("personModal").addEventListener("show.bs.modal", () => {
    personSearch.value = "";
    Array.from(personList.children).forEach(li => (li.style.display = ""));
    setTimeout(() => personSearch.focus(), 50);
  });

  document.getElementById("memberModal").addEventListener("show.bs.modal", () => {
    memberSearch.value = "";
    selectedMember.value = "";
    Array.from(memberList.children).forEach(li => (li.style.display = ""));
    monthSelect.selectedIndex = 0;
    memberList.style.display = ""; // Show the list again on modal open
    setTimeout(() => memberSearch.focus(), 50);
  });

  // --- Select handlers called from onclick in <li> ---
  window.selectIncharge = function (element) {
    inchargeInput.value = element.textContent;
    bootstrap.Modal.getInstance(document.getElementById("personModal")).hide();
  };

  window.selectMember = function (element) {
    selectedMember.value = element.textContent;
    memberSearch.value = element.textContent;
    document.getElementById('memberList').style.display = 'none';
    // Do NOT clear the member list; keep it visible for multiple adds
    // memberList.innerHTML = "";  <-- Removed this line
    // Instead, optionally hide the list visually if desired:
    // memberList.style.display = "none";
  };

  // --- Add supported member to table ---
  function escapeHtml(text) {
    return text.replace(/&/g, "&amp;")
               .replace(/</g, "&lt;")
               .replace(/>/g, "&gt;")
               .replace(/"/g, "&quot;");
  }

  function addSupportedMember(event) {
    if (event) event.preventDefault();

    const name = (selectedMember.value || memberSearch.value || "").trim();
    const month = (monthSelect.value || "").trim();

    if (!name) {
      alert("Please choose/select a member.");
      return;
    }
    if (!month) {
      alert("Please select a month.");
      return;
    }

    // Check for duplicates (name + month)
    const rows = Array.from(supportedList.querySelectorAll("tr"));
    const duplicate = rows.some(row => {
      const cells = row.querySelectorAll("td");
      return cells.length >= 2 && cells[0].textContent.trim() === name && cells[1].textContent.trim() === month;
    });
    if (duplicate) {
      alert("This member with the same month is already added.");
      return;
    }

    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${escapeHtml(name)}</td>
      <td>${escapeHtml(month)}</td>
      <td>
        <button type="button" class="btn btn-sm btn-danger remove-supported" aria-label="Remove supported member">
          <i class="bi bi-trash"></i>
        </button>
      </td>
    `;
    supportedList.appendChild(tr);

    // Clear inputs and close modal
    selectedMember.value = "";
    memberSearch.value = "";
    monthSelect.selectedIndex = 0;
    // Show the member list again just in case
    memberList.style.display = "";
    bootstrap.Modal.getInstance(document.getElementById("memberModal")).hide();

    // Optional: update count or UI here if needed
    updateSupportedCount();
    document.getElementById('memberList').style.display = 'block';
  }

  document.getElementById("addMemberBtn").addEventListener("click", addSupportedMember);

  // Remove supported member handler
  supportedList.addEventListener("click", (e) => {
    if (e.target.closest(".remove-supported")) {
      const row = e.target.closest("tr");
      row.remove();

      // Optional: update count or UI here if needed
      updateSupportedCount();
    }
  });

  function updateSupportedCount() {
    const count = supportedList.querySelectorAll("tr").length;
    const countElem = document.getElementById("supportedCount"); // Add this element in your HTML if you want
    if (countElem) {
      countElem.textContent = `Supported Members: ${count}`;
    }
  }


  // ==== Wizard Step Navigation ====

  function goToStep(step) {
    // Validate current step required inputs before proceeding
    const currentStep = document.querySelector(".wizard-step.active");
    const requiredInputs = currentStep.querySelectorAll(
      "input[required], select[required], textarea[required]"
    );

    for (const input of requiredInputs) {
      if (!input.checkValidity()) {
        input.reportValidity();
        return;
      }
    }

    document.querySelectorAll(".wizard-step").forEach((el) =>
      el.classList.remove("active")
    );
    document.getElementById(`step${step}`).classList.add("active");
  }

  // ==== Time Slot Planner (Step 2) ====

  const daysOfWeek = [
    "Sunday",
    "Monday",
    "Tuesday",
    "Wednesday",
    "Thursday",
    "Friday",
    "Saturday",
  ];
  const tableBody = document.getElementById("tableBody");

  // Start of this week (Sunday)
  let weekStart = new Date();
  weekStart.setDate(weekStart.getDate() - weekStart.getDay());

  // Format date like "Aug 8, 2025"
  function formatDate(date) {
    return date.toLocaleDateString(undefined, {
      month: "short",
      day: "numeric",
      year: "numeric",
    });
  }

  // Get today index relative to weekStart
  function getTodayIndex() {
    const now = new Date();
    const diffDays = Math.floor((now - weekStart) / (1000 * 60 * 60 * 24));
    if (diffDays >= 0 && diffDays < 7) {
      return diffDays;
    }
    return -1;
  }

  // Create a time slot input group
  function createSlotInput(day, isOnly = false) {
    const slot = document.createElement("div");
    slot.className = "time-slot d-flex gap-1 mb-1 align-items-center";

    const start = document.createElement("input");
    start.type = "time";
    start.name = `startTime_${day}[]`;
    start.className = "form-control form-control-sm";

    const end = document.createElement("input");
    end.type = "time";
    end.name = `endTime_${day}[]`;
    end.className = "form-control form-control-sm";

    const removeBtn = document.createElement("button");
    removeBtn.type = "button";
    removeBtn.textContent = "×";
    removeBtn.className = "btn btn-sm btn-danger removeSlotBtn";

    if (isOnly) {
      removeBtn.disabled = true;
      removeBtn.title = "At least one slot required";
    }

    slot.append(start, end, removeBtn);
    return slot;
  }

  function renderTable() {
    const todayIndex = getTodayIndex();
    tableBody.innerHTML = "";

    daysOfWeek.forEach((day, i) => {
      const date = new Date(weekStart);
      date.setDate(weekStart.getDate() + i);

      const row = document.createElement("tr");
      row.dataset.day = day;

      const dayCell = document.createElement("td");
      dayCell.innerHTML = `<strong>${day}</strong><br><small>${formatDate(date)}</small>`;
      if (i === todayIndex) {
        dayCell.style.backgroundColor = "#d1e7dd";
        dayCell.style.fontWeight = "bold";
      }

      const slotWrapper = document.createElement("td");
      slotWrapper.colSpan = 2;
      slotWrapper.className = "slot-wrapper";
      slotWrapper.dataset.day = day;

      // Add initial slot input
      slotWrapper.appendChild(createSlotInput(day, true));

      const addBtnCell = document.createElement("td");
      const addBtn = document.createElement("button");
      addBtn.type = "button";
      addBtn.className = "btn btn-sm btn-success addSlotBtn";
      addBtn.textContent = "+";
      addBtn.dataset.day = day;
      addBtnCell.appendChild(addBtn);

      row.append(dayCell, slotWrapper, addBtnCell);
      tableBody.appendChild(row);
    });
  }

  // Event delegation for add/remove time slots
  tableBody.addEventListener("click", (e) => {
    if (e.target.classList.contains("addSlotBtn")) {
      const day = e.target.dataset.day;
      const wrapper = document.querySelector(`.slot-wrapper[data-day="${day}"]`);
      if (wrapper) {
        wrapper.appendChild(createSlotInput(day));
        if (wrapper.children.length > 1) {
          wrapper.querySelectorAll(".removeSlotBtn").forEach((btn) => {
            btn.disabled = false;
          });
        }
      }
    }
    if (e.target.classList.contains("removeSlotBtn")) {
      const slotDiv = e.target.closest(".time-slot");
      const wrapper = slotDiv.parentElement;
      if (wrapper.children.length > 1) {
        slotDiv.remove();
        if (wrapper.children.length === 1) {
          wrapper.querySelector(".removeSlotBtn").disabled = true;
        }
      }
    }
  });

  document.getElementById("nextWeekBtn").addEventListener("click", () => {
    weekStart.setDate(weekStart.getDate() + 7);
    renderTable();
  });

  document.getElementById("prevWeekBtn").addEventListener("click", () => {
    weekStart.setDate(weekStart.getDate() - 7);
    renderTable();
  });

  // Initial render
  renderTable();

  // Submit form handler (example, enhance as needed)
  function submitEvent() {
    const form = document.getElementById("eventForm");

//    if (!form.checkValidity()) {
//      form.reportValidity();
//      return;
//    }

    const members = Array.from(supportedList.querySelectorAll("tr")).map((row) => {
      const tds = row.querySelectorAll("td");
      return { member: tds[0].textContent.trim(), month: tds[1].textContent.trim() };
    });

    if (members.length === 0) {
      alert("Please add at least one supported member.");
      return;
    }

//    const formData = {
//        eventName:
//        inchargePerson:
//
//    };


    alert(
      `Submitting event:\nName: ${form.eventName.value}\nIncharge: ${form.inchargePerson}\nSupported Members: ${members}`
    );

    // form.submit();
  }

  // Expose functions globally
  window.goToStep = goToStep;
  window.submitEvent = submitEvent;
  window.selectIncharge = window.selectIncharge;
  window.selectMember = window.selectMember;
});
