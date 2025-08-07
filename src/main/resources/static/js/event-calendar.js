const daysOfWeek = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
const tableBody = document.getElementById("tableBody");

// Utility: Format date like "Aug 8, 2025"
function formatDate(date) {
  return date.toLocaleDateString(undefined, { month: 'short', day: 'numeric', year: 'numeric' });
}

// Start of this week (Sunday)
let weekStart = new Date();
weekStart.setDate(weekStart.getDate() - weekStart.getDay());

// Calculate "today" index relative to the current weekStart
function getTodayIndex() {
  const now = new Date();
  const diffDays = Math.floor((now - weekStart) / (1000 * 60 * 60 * 24));
  if (diffDays >= 0 && diffDays < 7) {
    return diffDays;
  }
  return -1; // no highlight if not current week
}

function renderTable() {
  const todayIndex = getTodayIndex();
  tableBody.innerHTML = ""; // clear first

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

    // Add initial slot
    slotWrapper.appendChild(createSlotInput(day, true));

    const addBtnCell = document.createElement("td");
    const addBtn = document.createElement("button");
    addBtn.type = "button"; // Important to prevent submit
    addBtn.className = "btn btn-sm btn-success addSlotBtn";
    addBtn.textContent = "+";
    addBtn.dataset.day = day;
    addBtnCell.appendChild(addBtn);

    row.append(dayCell, slotWrapper, addBtnCell);
    tableBody.appendChild(row);
  });
}

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
  removeBtn.type = "button"; // Important!
  removeBtn.textContent = "×";
  removeBtn.className = "btn btn-sm btn-danger removeSlotBtn";

  if (isOnly) {
    removeBtn.disabled = true;
    removeBtn.title = "At least one slot required";
  }

  slot.append(start, end, removeBtn);
  return slot;
}

// Event delegation for dynamically created add/remove buttons
tableBody.addEventListener("click", (e) => {
  if (e.target.classList.contains("addSlotBtn")) {
    const day = e.target.dataset.day;
    const wrapper = document.querySelector(`.slot-wrapper[data-day="${day}"]`);
    if (wrapper) {
      const newSlot = createSlotInput(day);
      wrapper.appendChild(newSlot);

      // Enable remove buttons if more than one slot
      if (wrapper.children.length > 1) {
        wrapper.querySelectorAll(".removeSlotBtn").forEach(btn => btn.disabled = false);
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

function refreshTable() {
  renderTable();
}

document.getElementById("nextWeekBtn").addEventListener("click", () => {
  weekStart.setDate(weekStart.getDate() + 7);
  refreshTable();
});

document.getElementById("prevWeekBtn").addEventListener("click", () => {
  weekStart.setDate(weekStart.getDate() - 7);
  refreshTable();
});

// Initial render
renderTable();

function goToStep(step) {
  const currentStep = document.querySelector(".wizard-step.active");

  if (step !== 'submit') {
    const requiredInputs = currentStep.querySelectorAll("input[required], select[required], textarea[required]");
    for (const input of requiredInputs) {
      if (!input.checkValidity()) {
        input.reportValidity();
        return;
      }
    }

    document.querySelectorAll(".wizard-step").forEach(el => el.classList.remove("active"));
    document.getElementById(`step${step}`).classList.add("active");
    return;
  }

  // Submit
  const requiredInputs = currentStep.querySelectorAll("input[required], select[required], textarea[required]");
  for (const input of requiredInputs) {
    if (!input.checkValidity()) {
      input.reportValidity();
      return;
    }
  }

  // Disable inputs in hidden steps to prevent validation blocking
  document.querySelectorAll('.wizard-step:not(.active) input, .wizard-step:not(.active) select, .wizard-step:not(.active) textarea')
    .forEach(el => el.disabled = true);

  document.getElementById("eventForm").submit();
}
