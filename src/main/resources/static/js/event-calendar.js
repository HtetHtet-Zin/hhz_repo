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
const prevWeekBtn = document.getElementById("prevWeekBtn");

const supportedListTable = supportedList.closest('table');
const supportedThCount = supportedListTable.querySelectorAll('th').length;
const otherLocation = document.getElementById('otherLocation');


otherLocation.addEventListener('input', function(e) {
     document.getElementById('eventLocationOther').value = this.value;
});

function setNoSupportedMember() {
    supportedList.innerHTML = `
          <tr>
              <td colspan="${supportedThCount}" style="text-align:center;">No supported members</td>
          </tr>
      `;
}
function toggleOtherLocation(show) {
    document.getElementById("otherLocationDiv").style.display = show ? "block" : "none";
}

document.getElementById('eventName').addEventListener('input', function(e) {
    this.value = this.value.replace(/[^a-zA-Z\s]/g, '');
});

otherLocation.addEventListener('input', function(e) {
    this.value = this.value.replace(/[^a-zA-Z\s]/g, '');
});

document.addEventListener("DOMContentLoaded", () => {
    setNoSupportedMember();

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
          return cells.length >= 3 && cells[1].textContent.trim() === name && cells[2].textContent.trim() === month;
        });
        if (duplicate) {
          await alertAction("This member with the same month is already added.", { title: "Adding same member in a month!", variant: "danger"});
          return;
        }

        const noMemberRow = supportedList.querySelector("tr td[colspan='4']");
            if (noMemberRow) {
                noMemberRow.parentElement.remove();
            }

        const tr = document.createElement("tr");
        tr.innerHTML = `
          <td></td>
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
        const trList = supportedList.querySelectorAll("tr");
        const count = trList.length;
        if (count == 0 ) {
            setNoSupportedMember();
        }
        const countElem = document.getElementById("supportedCount"); // Add this element in your HTML if you want
        trList.forEach((tr, index) => {
            tr.firstElementChild.textContent = (index + 1) + ".";
        });
        if (countElem) {
          countElem.textContent = `Supported Members: ${count}`;
        }
    }

    // --- Auto hide errors when user fixes input ---
    document.getElementById("eventName").addEventListener("input", function() {
        if (this.value.trim()) {
            document.getElementById("eventNameError").style.display = "none";
        }
    });

    document.getElementById("eventPhoto").addEventListener("change", function() {
        if (this.files.length > 0) {
            document.getElementById("eventPhotoError").style.display = "none";
        }
    });

    document.getElementById("inchargePerson").addEventListener("input", function() {
        if (this.value.trim()) {
            document.getElementById("inchargePersonError").style.display = "none";
        }
    });

    document.getElementById("eventDesc").addEventListener("input", function() {
        if (this.value.length <= 255) {
            document.getElementById("eventDescError").style.display = "none";
        }
    });

    // ==== Wizard Step Navigation ====
    function goToStep(step) {
        // Validate current step required inputs before proceeding
        const currentStep = document.querySelector(".wizard-step.active");
        const requiredInputs = currentStep.querySelectorAll(
          "input[required], select[required]"
        );
        let isValid = true;

        for (const input of requiredInputs) {
            if (input.id === "eventName") {
                if (!input.value.trim()) {
                    document.getElementById("eventNameError").style.display = "block";
                    isValid = false;
                }
            }
            else if (input.id === "eventPhoto") {
                if (!input.files || input.files.length === 0) {
                    document.getElementById("eventPhotoError").style.display = "block";
                    isValid = false;
                }
            }
            else if (input.id === "inchargePerson") {
                if (!input.value.trim()) {
                    document.getElementById("inchargePersonError").style.display = "block";
                    isValid = false;
                }
            }
            else {
                if (!input.checkValidity()) {
                    input.reportValidity();
                    isValid = false;
                }
            }
        }
        if(currentStep.id === "step1") {
            const selectedLocation = document.querySelector('input[name="eventLocation"]:checked');
            if(!selectedLocation) {
                document.getElementById("eventLocationError").style.display = "block";
                isValid = false;
            } else {
                document.getElementById("eventLocationError").style.display = "none";
                if(selectedLocation.value === "OTHER") {
                    const otherVal = document.getElementById("otherLocation").value.trim();
                    if(!otherVal) {
                        let otherError = document.getElementById("otherLocationDiv").querySelector('.text-danger');
                        if(!otherError) {
                            otherError = document.createElement("div");
                            otherError.className = "text-danger mt-1";
                            otherError.textContent = "Please enter other location.";
                            document.getElementById("otherLocationDiv").appendChild(otherError);
                        }
                        otherError.style.display = "block";
                        isValid = false;
                    }
                }
            }
        }

        if( document.getElementById("eventDesc").value.length > 255) {
            document.getElementById("eventDescError").style.display = "block";
            isValid = false;
        }

        if (!isValid) return;
        const eventNameValue = document.querySelector("#eventName").value.trim();
        if( step > 1 ){
            const formData = new FormData();
            formData.append("eventName", eventNameValue);

            fetch('/club/event-check', {
                method: 'POST',
                body: formData
            })
            .then(res =>  res.json())
            .then(async (data) => {
                if( data.status === "error"){
                    await alertAction("Please enter different event name.", { title: "Duplicated Event!", variant: "danger"});
                } else {
                     switchStep(step);
                }

            })
            .catch(error => console.error("Error:", error));
        } else {
           switchStep(step);
        }

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

    // Event delegation for real-time duplicate & overlap check
   // Real-time validation for time slots
    tableBody.addEventListener("change",async (e) => {
       if (e.target.type === "time") {
            const slot = e.target.closest(".time-slot");
            const wrapper = slot.closest(".slot-wrapper");
            const day = wrapper.dataset.day;

            const startInput = slot.querySelector("input[type='time']:first-of-type");
            const endInput = slot.querySelector("input[type='time']:last-of-type");
            const startTime = startInput.value;
            const endTime = endInput.value;

            if (!startTime || !endTime) {
                return;
            }

            // Check if endTime is after startTime
            if (parseInt(endTime.replace(":", "")) <= parseInt(startTime.replace(":", ""))) {
               await alertAction("End time must be after Start time..", { title: "Select Correct Time!", variant: "danger"});
               endInput.value = "";
               return;
            }

            // Check duplicates (ignore current slot)
            const isDuplicate = Array.from(wrapper.querySelectorAll(".time-slot"))
            .filter(s => s !== slot)
            .some(s => {
               const sInputs = s.querySelectorAll("input[type='time']");
               return sInputs[0].value === startTime && sInputs[1].value === endTime;
            });
            if (isDuplicate) {
               await alertAction("Can't add duplicate schedule for this day. Please Select Another Different Time.", { title: "Select Correct Time!", variant: "danger"});
               startInput.value = "";
               endInput.value = "";
               return;
            }

           // Check overlap (ignore current slot)
           const startInt = parseInt(startTime.replace(":", ""));
           const endInt = parseInt(endTime.replace(":", ""));

           const isOverlap = Array.from(wrapper.querySelectorAll(".time-slot"))
               .filter(s => s !== slot)
               .some(s => {
                   const sInputs = s.querySelectorAll("input[type='time']");
                   const sStart = parseInt(sInputs[0].value.replace(":", ""));
                   const sEnd = parseInt(sInputs[1].value.replace(":", ""));
                   return startInt < sEnd && endInt > sStart;
               });

           if (isOverlap) {
               await alertAction("Can't add overlapping schedule for this day. Please Select Another Different Time.", { title: "Select Correct Time!", variant: "danger"});
               startInput.value = "";
               endInput.value = "";
               return;
           }
       }
   });



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
                dayCell.innerHTML = `<strong style="margin-right: 10px;">${day}</strong><small>(${formatDate(date)})</small>`;
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
                addBtnCell.className = "add-btn-td";
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
    tableBody.addEventListener("click", async (e) => {
        if (e.target.classList.contains("addSlotBtn")) {
            const day = e.target.dataset.day;
            const wrapper = document.querySelector(`.group-${currentPage}.slot-wrapper[data-day="${day}"]`);
            const addBtn = wrapper.parentElement.lastElementChild.lastElementChild;

            const lastSlot = wrapper.lastElementChild;
            const inputs = lastSlot.querySelectorAll('input[type="time"]');
            const startTime = inputs[0].value;
            const endTime = inputs[1].value;
            if (!startTime || !endTime) {
                await alertAction("Please select both start and end time before adding new slot.", { title: "Select Both Start Time And End Time!", variant: "danger"});
                return;
            }
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
                inputs.forEach(inp => inp.value = "");
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
                    name: tds[1]?.textContent.trim() || '',
                    month: tds[2]?.textContent.trim() || '',
                    memberId: tds[3]?.textContent.trim() || '',
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
            eventLocation: form.eventLocation.value,
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

        fetch('/club/event-create', {
            method: 'POST',
            body: formData
        })
        .then(response =>  response.json())
        .then(data => {
            const redirectUrl = new URL(data.redirectUrl, window.location.origin);
            redirectUrl.searchParams.append('message', data.message || '');
            redirectUrl.searchParams.append('messageType', data.status || '');
            window.location.href = redirectUrl;
        })
        .catch(error => console.error("Error:", error));
    }
    // Expose functions globally
    window.goToStep = goToStep;
    window.submitEvent = submitEvent;
    window.selectIncharge = window.selectIncharge;
    window.selectMember = window.selectMember;

});