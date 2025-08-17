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
        document.getElementById("inchargePersonError").hidden = true;
  };

  window.selectMember = function (element) {
    selectedMember.value = element.textContent;
    selectedMember.dataset.id = element.getAttribute('data-id');
    memberSearch.value = element.textContent;
    document.getElementById('memberList').style.display = 'none';
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
          <td></td>
          <td data-name="supName">${escapeHtml(name)}</td>
           <td data-name="supMonth">${escapeHtml(month)}</td>
           <td style="display: none;" data-name="supStaff">${escapeHtml(staffNo)}</td>
           <td style="display: none;"  data-name="supPlanner"></td>
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
          const trList = supportedList.querySelectorAll("tr");
          const count = trList.length;
          const countElem = document.getElementById("supportedCount"); // Add this element in your HTML if you want
          trList.forEach((tr, index) => {
              tr.firstElementChild.textContent = (index + 1) + ".";
          });
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
        let isValid = true;

    for (const input of requiredInputs) {
      if (!input.checkValidity()) {
        input.reportValidity();
                isValid = false;
            } else  if (input.id === "inchargePerson" && !input.value.trim()) {
                document.getElementById("inchargePersonError").style.display = "block";
                isValid = false;
      }
    }

        if (!isValid) return;
        const eventNameValue = document.querySelector("#eventName").value.trim();
      switchStep(step);

    }
  function switchStep(step) {
      // Hide all steps and remove active class
      document.querySelectorAll(".wizard-step").forEach(el => el.classList.remove("active"));
      document.querySelectorAll(".wizard-step").forEach(el => el.classList.add("d-none"));

      // Show selected step
      const selectedStep = document.getElementById(`step${step}`);
      selectedStep.classList.add("active");
      selectedStep.classList.remove("d-none");

      // Update navigation underline
      document.querySelectorAll(".wizard-step-link").forEach(el => el.classList.remove("active"));
      const navStep = document.getElementById(`navStep${step}`);
      if (navStep) navStep.classList.add("active");
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
  function createSlotInput1(day, disabledAction,member, isOnly = false) {

    const slot = document.createElement("div");
    slot.className = "time-slot d-flex gap-1 mb-1 align-items-center";

        const hidden = document.createElement("input");
       hidden.type = "hidden";
       hidden.id = uniqueSuffix();
       hidden.name=  `hidden_${day}[]`
       hidden.value =member.id;
       hidden.dataset.id = "hidden";
       hidden.className = "form-control form-control-sm";
       disableOrEnableInput(hidden, disabledAction);

    const startLabel = document.createElement('label');
    const startId = uniqueSuffix();
    startLabel.textContent = "Start time : ";
    startLabel.htmlFor = startId;

    const start = document.createElement("input");
    start.type = "time";
    start.id = startId;
    start.name = `startTime_${day}[]`;
    start.value =member.startTime;
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
    end.value= member.endTime;
    end.className = "form-control form-control-sm";
    disableOrEnableInput(end, disabledAction);

    const removeBtn = document.createElement("button");
    removeBtn.type = "button";
    removeBtn.textContent = "-";
    removeBtn.className = "btn btn-sm btn-danger removeSlotBtn";
    disableOrEnableInput(removeBtn, disabledAction);
    if (isOnly) {
      removeBtn.title = "At least one slot required";
    }
    slot.append(hidden,startLabel, start, endLabel, end, removeBtn);
    return slot;
  }

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
          let addEmptyRow = true;
        eventScheduleList.forEach(member => {
          // Add initial slot input
          if(isoDate == member.date){
            addEmptyRow = false;
            slotWrapper.appendChild(createSlotInput1(day, disabledAction,member, true));
            }
         });

         if(addEmptyRow){
              slotWrapper.appendChild(createSlotInput(day, disabledAction, true));
         }
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
        } else {
            const inputs = slotDiv.querySelectorAll("input[type='time']");
            inputs.forEach(inp => inp.value = ""); // clear times
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
const supportedMembersList = [];
function formatDateTime(dateStr, timeStr) {
  const [year, month, day] = dateStr.split('-').map(Number);
  const [hour, minute] = timeStr.split(':').map(Number);

  const dateObj = new Date(year, month - 1, day, hour, minute);
  const yyyy = dateObj.getFullYear();
  const MM = String(dateObj.getMonth() + 1).padStart(2, '0');
  const dd = String(dateObj.getDate()).padStart(2, '0');
  const HH = String(dateObj.getHours()).padStart(2, '0');
  const mm = String(dateObj.getMinutes()).padStart(2, '0');
  const ss = String(dateObj.getSeconds()).padStart(2, '0');

  return `${yyyy}-${MM}-${dd} ${HH}:${mm}:${ss}`;
}

  // Submit form handler (example, enhance as needed)
  function submitEvent() {
    const dateTimeList = [];
    const form = document.getElementById("eventForm");


     document.querySelectorAll("#supportedList tr").forEach(row => {
     console.log(1);

         const supName =   row.querySelector('[data-name = "supName"]').textContent;
          const supMonth =   row.querySelector('[data-name = "supMonth"]').textContent;
          const supStaff =   row.querySelector('[data-name = "supStaff"]').textContent;
        const supPlanner = row.querySelector('[data-name="supPlanner"]')?.textContent || '';
        const supportedMembers ={
                plannerId : supPlanner,
                memberId :supStaff,
                name : supName,
                month : supMonth
        };
        supportedMembersList.push(supportedMembers);

     });

      tableBody.querySelectorAll("tr").forEach(row => {
        const date = row.dataset.date;
         if (!date) {
            console.error("Missing date for a time slot wrapper. Skipping.");
            return; // Skips this iteration if date is missing
          }
        row.querySelectorAll(".time-slot").forEach(slot => {
          const timeInput = slot.querySelectorAll('input[type="time"]');
          const hiddenInput = slot.querySelectorAll('[data-id = "hidden"]');
          const startTime = timeInput[0]?.value;
          const endTime = timeInput[1]?.value;
         const eventTimeId =  Number(hiddenInput[0]?.value);
          if (startTime && endTime) {
            dateTimeList.push({
              startDateTime: formatDateTime(date, startTime),
                endDateTime: formatDateTime(date, endTime),
              eventTimeId: eventTimeId
            });
          }
        });
      });

    const jsonPayload = {
        eventId:form.eventId.value,
        eventName: form.eventName.value,
        description: form.eventDesc.value,
        inChargePersonPlannerId:form.inChargePersonPlannerId.value,
        inChargePerson: form.inchargePerson.dataset.dataId,
        supportedMembers: supportedMembersList,
        eventTimes: dateTimeList
      };

   const formData = new FormData();
   const fileInput = document.getElementById("eventPhoto");
   if (fileInput.files.length > 0) {
       formData.append("eventPhoto", fileInput.files[0]);
   }
    formData.append("eventData", new Blob([JSON.stringify(jsonPayload)], { type: 'application/json' }));

   fetch('/club/event-edit', {
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
