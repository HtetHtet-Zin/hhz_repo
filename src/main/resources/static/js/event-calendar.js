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
  const nextWeekBtn = document.getElementById("nextWeekBtn");
  const prevWeekBtn = document.getElementById("prevWeekBtn")


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
    inchargeInput.dataset.dataId = element.getAttribute("data-id");
    bootstrap.Modal.getInstance(document.getElementById("personModal")).hide();
  };

  window.selectMember = function (element) {
    selectedMember.value = element.textContent;
    selectedMember.dataset.id = element.getAttribute('data-id');
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

  async function addSupportedMember(event) {
    if (event) event.preventDefault();

    const name = (selectedMember.value || memberSearch.value || "").trim();
    const staffNo = selectedMember.getAttribute('data-id');
    const month = (monthSelect.value || "").trim();

    if (!name) {
      await alertAction("Please select a member.", { title: "Require selection!", variant: "danger"});
      return;
    }
    if (!month) {
      await alertAction("Please select a month.", { title: "Require selection!", variant: "danger"});
      return;
    }

    // Check for duplicates (name + month)
    const rows = Array.from(supportedList.querySelectorAll("tr"));
    const duplicate = rows.some(row => {
      const cells = row.querySelectorAll("td");
      return cells.length >= 2 && cells[0].textContent.trim() === name && cells[1].textContent.trim() === month;
    });
    if (duplicate) {
      await alertAction("This member with the same month is already added.", { title: "Adding same member in a month!", variant: "danger"});
      return;
    }

    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${escapeHtml(name)}</td>
      <td>${escapeHtml(month)}</td>
      <td style="display: none;">${escapeHtml(staffNo)}</td>
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
  function createSlotInput(day, disabledAction, isOnly = false) {
    const slot = document.createElement("div");
    slot.className = "time-slot d-flex gap-1 mb-1 align-items-center";

    const startLabel = document.createElement('label');
    const startId = uniqueSuffix();
    startLabel.textContent = "Start time : ";
    startLabel.htmlFor = startId;

    const start = document.createElement("input");
    start.type = "time";
    start.id = startId;
    start.name = `startTime_${day}[]`;
    start.className = "form-control form-control-sm";
    disableOrEnableInput(start, disabledAction);

    const endLabel = document.createElement('label');
    const endId = uniqueSuffix();
    endLabel.textContent = "End time : ";
    endLabel.htmlFor = endId;

    const end = document.createElement("input");
    end.type = "time";
    end.id = endId;
    end.name = `endTime_${day}[]`;
    end.className = "form-control form-control-sm";
    disableOrEnableInput(end, disabledAction);

    const removeBtn = document.createElement("button");
    removeBtn.type = "button";
    removeBtn.textContent = "-";
    removeBtn.className = "btn btn-sm btn-danger removeSlotBtn";
    disableOrEnableInput(removeBtn, disabledAction);
    if (isOnly) {
      disableInput(removeBtn);
      removeBtn.title = "At least one slot required";
    }
    slot.append(startLabel, start, endLabel, end, removeBtn);
    return slot;
  }

  let latestPage = 0;
  let currentPage = 1;
  const maximumPage = 4;

  function renderTable() {
    const todayIndex = getTodayIndex();
   // tableBody.innerHTML = "";

    if(/*canCreateNewPage*/ latestPage < currentPage){
     latestPage++;
     daysOfWeek.forEach((day, i) => {
          const date = new Date(weekStart);
          const disabledAction = i < todayIndex;
          date.setDate(weekStart.getDate() + i);

          const isoDate = date.toISOString().split('T')[0];
          const row = document.createElement("tr");
          row.classList.add('page');
          row.classList.add('page-' + currentPage);
          row.dataset.day = day;
          row.dataset.date = isoDate;

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
          slotWrapper.classList.add('group-' + currentPage);

          // Add initial slot input
          slotWrapper.appendChild(createSlotInput(day, disabledAction, true));

          const addBtnCell = document.createElement("td");
          const addBtn = document.createElement("button");
          addBtn.type = "button";
          addBtn.className = "btn btn-sm btn-success addSlotBtn";
          addBtn.textContent = "+";
          addBtn.dataset.day = day;
          disableOrEnableInput(addBtn, disabledAction);
          addBtnCell.appendChild(addBtn);

          row.append(dayCell, slotWrapper, addBtnCell);
          tableBody.appendChild(row);
        });
    }
    showPage();
    disableOrEnableInput(nextWeekBtn, currentPage == latestPage && latestPage == maximumPage);
    disableOrEnableInput(prevWeekBtn, currentPage == 1);
  }

  function showPage(){
    document.querySelectorAll('.page').forEach(element => {
        element.style.display = "none";
    });
    document.querySelectorAll(`.page-${currentPage}`).forEach(element => {
        element.style.display = "table-row";
    });
  }

  // Event delegation for add/remove time slots
  tableBody.addEventListener("click", (e) => {
    if (e.target.classList.contains("addSlotBtn")) {
      const day = e.target.dataset.day;
      const wrapper = document.querySelector(`.group-${currentPage}.slot-wrapper[data-day="${day}"]`);
      const addBtn = wrapper.parentElement.lastElementChild.lastElementChild;
      if(wrapper.children.length > 1) {
        disableInput(addBtn);
      }
      if (wrapper) {
        wrapper.appendChild(createSlotInput(day, false));
        if (wrapper.children.length > 1) {
            wrapper.querySelectorAll(".removeSlotBtn").forEach((btn) => {
            enableInput(btn);
            btn.onclick = function (event) {
                if(wrapper.children.length < 4){
                    enableInput(addBtn);
                }
            }
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
          disableInput(wrapper.querySelector(".removeSlotBtn"));
        }
      }
    }
  });

  nextWeekBtn.addEventListener("click", () => {
    currentPage++;
    weekStart.setDate(weekStart.getDate() + 7);
    renderTable();
  });

  prevWeekBtn.addEventListener("click", () => {
    currentPage--;
    weekStart.setDate(weekStart.getDate() - 7);
    renderTable();
  });

  // Initial render
  renderTable();

  // Submit form handler (example, enhance as needed)
  function submitEvent() {
    const dateTimeList = [];
    const form = document.getElementById("eventForm");
    const members = Array.from(supportedList.querySelectorAll("tr")).map((row) => {
    const tds = row.querySelectorAll("td");
      return {
        name: tds[0].textContent.trim(),
        month: tds[1].textContent.trim(),
        memberId: tds[2].textContent.trim(),
        };
    });

      tableBody.querySelectorAll("tr").forEach(row => {
        const date = row.dataset.date;
         if (!date) {
            console.error("Missing date for a time slot wrapper. Skipping.");
            return; // Skips this iteration if date is missing
          }
        row.querySelectorAll(".time-slot").forEach(slot => {
          const timeInput = slot.querySelectorAll('input[type="time"]');
          const startTime = timeInput[0].value;
          const endTime = timeInput[1].value;
          if (startTime && endTime) {
            dateTimeList.push({
              startDateTime: `${date} ${startTime}`,
              endDateTime: `${date} ${endTime}`
            });
          }
        });
      });

    const jsonPayload = {
        eventName: form.eventName.value,
        description: form.eventDesc.value,
        inChargePerson: form.inchargePerson.dataset.dataId,
        supportedMembers: members,
        eventTimes: dateTimeList
      };

   const formData = new FormData();
   const fileInput = document.getElementById("eventPhoto");
   if (fileInput.files.length > 0) {
       formData.append("eventPhoto", fileInput.files[0]);
   }
    formData.append("eventData", new Blob([JSON.stringify(jsonPayload)], { type: 'application/json' }));

   fetch('/club/event/create', {
       method: 'POST',
       body: formData
   })
   .then(response => {
       if (!response.ok) {
           throw new Error("Network response was not ok");
       }
       window.location.href = "/club/event";
       return Promise.resolve();
   })
   .then(data => {
    console.log(data);
   })
   .catch(error => console.error("Error:", error));

  }

  // Expose functions globally
  window.goToStep = goToStep;
  window.submitEvent = submitEvent;
  window.selectIncharge = window.selectIncharge;
  window.selectMember = window.selectMember;
});
