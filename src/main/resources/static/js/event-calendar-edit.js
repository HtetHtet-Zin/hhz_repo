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


function setNoSupportedMember() {
    supportedList.innerHTML = `
          <tr>
              <td colspan="${supportedThCount}" style="text-align:center;">No supported members</td>
          </tr>
      `;
}

document.getElementById('eventName').addEventListener('input', function(e) {
    this.value = this.value.replace(/[^a-zA-Z\s]/g, '');
});

document.addEventListener("DOMContentLoaded", () => {

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
            <td data-name="supName">${escapeHtml(name)}</td>
            <td data-name="supMonth">${escapeHtml(month)}</td>
            <td style="display: none;" data-name="supStaff">${escapeHtml(staffNo)}</td>
            <td style="display: none;"  data-name="supPlanner"></td>
            <td>
                <i class="bi bi-trash text-danger remove-supported" role="button" aria-label="Remove supported member"></i>
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

    document.querySelectorAll('input[name="eventLocation"]').forEach(radio => {
        radio.addEventListener("change", validateEventLocation);
    });
//    document.getElementById("otherLocation").addEventListener("input", validateEventLocation);

    function validateEventLocation() {
        const selectedLocation = document.querySelector('input[name="eventLocation"]:checked');
        const errorDiv = document.getElementById("eventLocationError");
        let isValid = true;

        if (!selectedLocation) {
           errorDiv.style.display = "block";
           isValid = false;
        } else {
            errorDiv.style.display = "none";
            if (selectedLocation.value === "OTHER" || selectedLocation.value.trim() === "") {
//                const otherInput = document.getElementById("otherLocation");
                let otherError = document.getElementById("otherLocationErrorMessage");

                if (!otherError) {
                    otherError = document.createElement("div");
                    otherError.id = "otherLocationErrorMessage";   // unique id
                    otherError.className = "text-danger mt-1";
                }


            } else {
                const otherError = document.getElementById("otherLocationErrorMessage");
                if (otherError) {
                    otherError.style.display = "none";
                }
            }
        }
        return isValid;
    }

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
            if (!validateEventLocation()) {
                isValid = false;
            }
        }

        if(document.getElementById("eventDesc").value.length > 255) {
            document.getElementById("eventDescError").style.display = "block";
            isValid = false;
        }

        if (!isValid) return;
        const eventNameValue = document.querySelector("#eventName").value.trim();
        if (currentEventName === eventNameValue) {
            switchStep(step);
        } else if( step > 1 ){
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
              // --- Validation function ---
                        async function validateTimes1() {

                         if (start.value){

                           if (start.value && end.value && start.value >= end.value) {
                                         if(await alertAction("End time must be later than start time.", { title: "Invalid input!", variant: "danger"})){

                                         }
                                          start.value = "";
                                          end.value = "";
                                     }

                            }else{
                             if(await alertAction("Please fill AM or PM.", { title: "Invalid input!", variant: "danger"})){

                                                                }
                                                                 start.value = "";
                                                                 end.value = "";
                            }


                        }
                           // --- Validation function ---
                                async function validateTimes() {

                                 if (end.value){

                                   if (start.value && end.value && start.value >= end.value) {
                                             if(await alertAction("End time must be later than start time.", { title: "Invalid input!", variant: "danger"})){

                                                     }
                                                          start.value = "";
                                                          end.value = "";
                                                     }

                                      }else{
                                             if(await alertAction("Please fill AM or PM.", { title: "Invalid input!", variant: "danger"})){
                                                                                      }
                                                                              start.value = "";
                                                                             end.value = "";
                                        }

                                        }


        // Hook validation on input changes
        start.addEventListener("input", validateTimes1);
        end.addEventListener("input", validateTimes);
        const removeBtn = document.createElement("button");
        removeBtn.type = "button";
        removeBtn.textContent = "-";
        removeBtn.className = "btn btn-sm btn-danger removeSlotBtn";
        //disableOrEnableInput(removeBtn, disabledAction);
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

                // --- Validation function ---
                async function validateTimes1() {

                 if (start.value){

                   if (start.value && end.value && start.value >= end.value) {
                                 if(await alertAction("End time must be later than start time.", { title: "Invalid input!", variant: "danger"})){

                                 }
                                  start.value = "";
                                  end.value = "";
                             }

                    }else{
                     if(await alertAction("Please fill AM or PM.", { title: "Invalid input!", variant: "danger"})){

                                                        }
                                                         start.value = "";
                                                         end.value = "";
                    }


                }
                   // --- Validation function ---
                        async function validateTimes() {

                         if (end.value){

                           if (start.value && end.value && start.value >= end.value) {
                                     if(await alertAction("End time must be later than start time.", { title: "Invalid input!", variant: "danger"})){

                                             }
                                                  start.value = "";
                                                  end.value = "";
                                             }

                              }else{
                                     if(await alertAction("Please fill AM or PM.", { title: "Invalid input!", variant: "danger"})){
                                                                              }
                                                                      start.value = "";
                                                                     end.value = "";
                                }

                                }

                // Hook validation on input changes
                start.addEventListener("input", validateTimes1);
                end.addEventListener("input", validateTimes);

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
    let startDate = new Date(); // today
    const maximumPage = 5; // next 5 weeks
    const totalDays = maximumPage * 7; // 35 days

    function renderTable() {
        const todayIndex = getTodayIndex();
        // tableBody.innerHTML = "";

        if (latestPage < currentPage) {
            latestPage++;

            for (let i = (currentPage - 1) * 7; i < currentPage * 7; i++) {
                if (i >= totalDays) break;

                const date = new Date(startDate);
                date.setDate(startDate.getDate() + i);

                const day = daysOfWeek[date.getDay()];
                const isoDate = date.toISOString().split('T')[0];
               // const disabledAction = date < new Date().setHours(0,0,0,0);
                let disabledAction = false;

                const row = document.createElement("tr");
                row.classList.add("page");
                row.classList.add("page-" + currentPage);
                row.dataset.day = day;
                row.dataset.date = isoDate;

                const dayCell = document.createElement("td");
                dayCell.innerHTML = `<strong style="margin-right: 10px;">${day}</strong><small>(${formatDate(date)})</small>`;
                if (isoDate === new Date().toISOString().split('T')[0]) {
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
                    if (isoDate === member.date) {
                        addEmptyRow = false;
                        if(member.rejectFlag){
                        disabledAction = true;
                        }else{
                         //bookingFlag can be null
                         disabledAction = member?.bookingFlag == true ? true : false;
                        }
                        slotWrapper.appendChild(createSlotInput1(day, disabledAction, member, true));
                    }
                    disabledAction = false;
                });

                if (addEmptyRow) {
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

            }
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
                 const input = slotDiv.querySelector("input[type='hidden']");
                              const deletedId = input?.value; // shorter than getAttribute
                                const id = Number(deletedId);
                                console.log(id);
                                  deleteScheduleList.push(id);

                    slotDiv.remove();
                } else {
                    const inputs = slotDiv.querySelectorAll("input[type='time']");
                      const input = slotDiv.querySelector("input[type='hidden']");
                                      const deletedId = input?.value; // shorter than getAttribute
                                        const id = Number(deletedId);
                                          deleteScheduleList.push(id);
                    inputs.forEach(inp =>{ inp.value = "";
                     inp.disabled = false;
                     inp.style.cursor = 'pointer';
                    });
                      input.value = "";

                }
            }
        });



    nextWeekBtn.addEventListener("click", () => {
        if (currentPage < maximumPage) {
            currentPage++;
            renderTable();
        }
    });

    prevWeekBtn.addEventListener("click", () => {
        if (currentPage > 1) {
            currentPage--;
            renderTable();
        }
    });



    // Initial render
    renderTable();
    const supportedMembersList = [];
    let deleteScheduleList = [];

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
            const supName =   row.querySelector('[data-name = "supName"]')?.textContent || '';
            const supMonth =   row.querySelector('[data-name = "supMonth"]')?.textContent || '';
            const supStaff =   row.querySelector('[data-name = "supStaff"]')?.textContent || '';
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
                const eventTimeId =  Number(hiddenInput[0]?.value) == 0 ? null : Number(hiddenInput[0]?.value) ;
                if (startTime && endTime) {
                    dateTimeList.push({
                        startDateTime: formatDateTime(date, startTime),
                        endDateTime: formatDateTime(date, endTime),
                        eventTimeId: eventTimeId
                    });
                }else{
                           const deletedId = Number(hiddenInput[0]?.value);
                              deleteScheduleList.push(deletedId);
                }
            });
        });

        const jsonPayload = {
            eventId:form.eventId.value,
            eventName: form.eventName.value,
            eventLocation: form.eventLocation.value,
            description: form.eventDesc.value,
            inChargePersonPlannerId:form.inChargePersonPlannerId.value,
            inChargePerson: form.inchargePerson.dataset.dataId,
            supportedMembers: supportedMembersList,
            eventTimes: dateTimeList,
            deleteScheduleList:deleteScheduleList
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